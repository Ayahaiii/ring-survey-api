package com.monetware.ringsurvey.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.monetware.ringsurvey.business.dao.ProjectAnalysisModuleDao;
import com.monetware.ringsurvey.business.dao.ProjectAnalyzerDao;
import com.monetware.ringsurvey.business.dao.ProjectQuestionnaireDao;
import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectAnalysisModule;
import com.monetware.ringsurvey.business.pojo.vo.analyzer.*;
import com.monetware.ringsurvey.survml.SurvMLDocument;
import com.monetware.ringsurvey.survml.SurvMLDocumentParser;
import com.monetware.ringsurvey.survml.exceptions.SurvMLParseException;
import com.monetware.ringsurvey.survml.questions.*;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.file.Base64ToFileUtil;
import com.monetware.ringsurvey.system.util.redis.RedisUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class AnalyzerService {

    @Autowired
    private ProjectAnalysisModuleDao analysisModuleDao;

    @Autowired
    private ProjectAnalyzerDao analyzerDao;

    @Autowired
    private ProjectQuestionnaireDao questionnaireDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    Base64ToFileUtil toFileUtil;

    /**
     * 保存常用统计模型
     *
     * @param moduleVO
     * @return
     */
    public int saveAnalysisModule(AnalysisModuleVO moduleVO) {
        BaseProjectAnalysisModule analysisModule = new BaseProjectAnalysisModule();
        BeanUtils.copyProperties(moduleVO, analysisModule);
        if (analysisModule.getId() == null) {
            analysisModule.setCreateUser(ThreadLocalManager.getUserId());
            analysisModule.setCreateTime(new Date());
            return analysisModuleDao.insertSelective(analysisModule);
        } else {
            return analysisModuleDao.updateByPrimaryKeySelective(analysisModule);
        }
    }

    /**
     * 获取统计模型列表分页
     *
     * @param searchVO
     * @return
     */
    public PageList<Page> getAnalysisModuleList(AnalysisModuleSearchVO searchVO) {
        Page page = PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        Example example = new Example(BaseProjectAnalysisModule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", searchVO.getProjectId());
        if (searchVO.getCheckRole() == AuthorizedConstants.ROLE_OBSERVER) {
            criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        }
        if (StringUtils.isNotBlank(searchVO.getKeyword())) {
            criteria.andLike("name", "%" + searchVO.getKeyword() + "%");
        }
        analysisModuleDao.selectByExample(example);
        return new PageList<>(page);
    }

    /**
     * 获取统计模型详情
     *
     * @param infoVO
     * @return
     */
    public BaseProjectAnalysisModule getAnalysisModule(AnalysisModuleInfoVO infoVO) {
        return analysisModuleDao.selectByPrimaryKey(infoVO.getId());
    }

    /**
     * 删除统计模型
     *
     * @param infoVO
     * @return
     */
    public int deleteAnalysisModule(AnalysisModuleInfoVO infoVO) {
        return analysisModuleDao.deleteByPrimaryKey(infoVO.getId());
    }

    /**
     * 单题统计
     *
     * @param singleVO
     * @return
     */
    public List<HashMap<String, Object>> getSingleStatistics(AnalysisSingleVO singleVO) {
        List<HashMap<String, Object>> returnData = new ArrayList<HashMap<String, Object>>();
        //获取当前选择题目
        String xml = questionnaireDao.selectByPrimaryKey(singleVO.getQuestionnaireId()).getXmlContent();
        SurvMLDocumentParser parse = new SurvMLDocumentParser(xml);
        SurvMLDocument qDocument = null;
        List<Question> questions = new ArrayList<>();
        try {
            qDocument = parse.parse();
            for (String qId : singleVO.getQuestionIds()) {
                questions.add(qDocument.getQuestion(qId));
            }
        } catch (SurvMLParseException e) {
            e.printStackTrace();
        }

        for (Question tempQuestion : questions) {
            HashMap<String, Object> returnDataMap = new HashMap<String, Object>();
            HashMap<String, Object> selectedCount;
            // 添加问题的基本信息
            returnDataMap.put("qId", tempQuestion.getId());
            returnDataMap.put("qTitle", tempQuestion.getTitle());
            if (tempQuestion.getQuestionType() == QuestionType.Matrix) {
                // 矩阵题
                MatrixQuestion question = (MatrixQuestion) tempQuestion;
                // 添加行名称作为X轴
                List<MatrixQuestion.MatrixRow> rows = question.getRows();
                String[] xAxis = new String[rows.size()];
                for (int i = 0, length = rows.size(); i < length; i++) {
                    xAxis[i] = rows.get(i).getName();
                }
                returnDataMap.put("xAxis", xAxis);
                List<MatrixQuestion.MatrixCol> cols = question.getCols();
                List<HashMap<String, Object>> yAxis = new LinkedList<>();
                HashMap<String, Object> paraMap = new HashMap<>();
                Question colQuestion = null;
                ListQuestion listQuestion = null;
                List<ListItem> listItems = null;
                // 循环统计每一个列问题
                for (int i = 0, length = cols.size(); i < length; i++) {
                    colQuestion = cols.get(i).getQuestion();

                    if (QuestionType.SingleSelect.equals(colQuestion.getQuestionType())
                            || QuestionType.MultiSelect.equals(colQuestion.getQuestionType())
                            || QuestionType.Dropdown.equals(colQuestion.getQuestionType())) {
                        HashMap<String, Object> colQuestionData = new HashMap<>();
                        listQuestion = (ListQuestion) colQuestion;
                        listItems = listQuestion.getListItems();

                        colQuestionData.put("qId", listQuestion.getId());
                        colQuestionData.put("qTitle", listQuestion.getTitle());
                        String[] qOption = new String[listItems.size()];
                        for (int j = 0, jLength = listItems.size(); j < jLength; j++) {
                            qOption[j] = listItems.get(j).getName();
                        }
                        colQuestionData.put("qOption", qOption);

                        // 统计数据
                        HashMap<String, Object> data = new HashMap<>();
                        String countStr = "";
                        for (int j = 0, jLength = rows.size(); j < jLength; j++) {
                            for (int k = 0, kLength = listItems.size(); k < kLength; k++) {
                                countStr += "count(json_extract(r.response_data,'$." + question.getId() + "." + '"' + question.getId() + "." + rows.get(j).getId() + "."
                                        + listQuestion.getId() + "." + listItems.get(k).getId() + '"' + "' )) " + rows.get(j).getId() + "_" + listItems.get(k).getId() + "_option_count, ";
                            }
                        }
                        if (countStr.length() > 0) {
                            countStr = countStr.substring(0, countStr.lastIndexOf(","));
                            AnalysisSingleSearchVO searchVO = new AnalysisSingleSearchVO();
                            BeanUtils.copyProperties(singleVO, searchVO);
                            searchVO.setFindFields(countStr);
                            selectedCount = analyzerDao.getSingleStatisticsList(searchVO);
                            for (int j = 0, jLength = rows.size(); j < jLength; j++) {
                                HashMap<String, Object> rowData = new HashMap<>();
                                for (int k = 0, kLength = listItems.size(); k < kLength; k++) {
                                    String countKey = rows.get(j).getId() + "_" + listItems.get(k).getId() + "_option_count";
                                    rowData.put(listItems.get(k).getName(), selectedCount.get(countKey));
                                }
                                data.put(rows.get(j).getName(), rowData);
                            }
                        }
                        colQuestionData.put("data", data);
                        yAxis.add(colQuestionData);
                    } else { // 只统计单选和多选题和下拉题
                        continue;
                    }
                }
                returnDataMap.put("qType", 1);
                returnDataMap.put("yAxis", yAxis);
            } else {
                // 其他的是单选题 多选题 下拉题
                List<HashMap<String, Object>> returnDataList = new ArrayList<HashMap<String, Object>>();
                ListQuestion listQuestion = (ListQuestion) tempQuestion;
                String countStr = "count(json_extract(r.response_data,'$." + tempQuestion.getId() + "' )) " + "option_count, ";
                for (ListItem option : listQuestion.getListItems()) {
                    countStr += "count(json_extract(r.response_data,'$." + tempQuestion.getId() + "." + "\"" + tempQuestion.getId() + "." + option.getId() + "\"' )) " + tempQuestion.getId() + "_" + option.getId() + "_option_count, ";
                }
                List<String> xData = new ArrayList<>();
                List<Integer> yData = new ArrayList<>();
                if (countStr.length() > 0) {
                    countStr = countStr.substring(0, countStr.lastIndexOf(","));
                    AnalysisSingleSearchVO searchVO = new AnalysisSingleSearchVO();
                    BeanUtils.copyProperties(singleVO, searchVO);
                    searchVO.setFindFields(countStr);
                    selectedCount = analyzerDao.getSingleStatisticsList(searchVO);
                    int totalCount = Integer.parseInt(selectedCount.get("option_count").toString());
                    for (ListItem option : listQuestion.getListItems()) {
                        String countKey = tempQuestion.getId() + "_" + option.getId() + "_option_count";
                        LinkedHashMap<String, Object> optionCountMap = new LinkedHashMap<String, Object>();
                        optionCountMap.put("id", option.getId());
                        optionCountMap.put("name", option.getName());
                        optionCountMap.put("count", selectedCount.get(countKey));
                        xData.add(option.getName());
                        yData.add(Integer.parseInt(selectedCount.get(countKey).toString()));
                        returnDataList.add(optionCountMap);
                    }
                    for (Map<String, Object> optionCountMap : returnDataList) {
                        String scale = calculateScale((Integer.parseInt(String.valueOf(optionCountMap.get("count")))), totalCount);
                        optionCountMap.put("scale", scale);
                    }
                }
                returnDataMap.put("qType", 2);
                returnDataMap.put("xData", xData);
                returnDataMap.put("yData", yData);
                returnDataMap.put("data", returnDataList);
            }
            returnData.add(returnDataMap);
        }
        return returnData;
    }

    /**
     * 交叉统计
     *
     * @param insectVO
     * @return
     */
    public HashMap<String, Object> getInsectStatistics(AnalysisInsectVO insectVO) {
        HashMap<String, Object> returnData = new HashMap<String, Object>();

        //获取当前选择题目
        String xml = questionnaireDao.selectByPrimaryKey(insectVO.getQuestionnaireId()).getXmlContent();
        SurvMLDocumentParser parse = new SurvMLDocumentParser(xml);
        SurvMLDocument qDocument = null;
        try {
            qDocument = parse.parse();
        } catch (SurvMLParseException e) {
            e.printStackTrace();
        }

        AnalysisInsectSearchVO searchVO = new AnalysisInsectSearchVO();
        BeanUtils.copyProperties(insectVO, searchVO);
        List<String> rowNames = new ArrayList<String>();
        List<String> colNames = new ArrayList<String>();
        List<HashMap<String, Object>> rowColStat = new ArrayList<HashMap<String, Object>>();

        if ("sample".equals(insectVO.getXType()) && "questionnaire".equals(insectVO.getYType())) {
            searchVO.setQVariable(insectVO.getYVariable());
            searchVO.setSVariable(insectVO.getXVariable());
        } else if ("questionnaire".equals(insectVO.getXType()) && "sample".equals(insectVO.getYType())) {
            searchVO.setQVariable(insectVO.getXVariable());
            searchVO.setSVariable(insectVO.getYVariable());
        }
        Question question = qDocument.getQuestion(searchVO.getQVariable());
        ListQuestion listQuestion = (ListQuestion) question;
        List<HashMap<String, Object>> groupProperties = analyzerDao.getInsectSampleProperty(searchVO);
        String countStr = "";
        if (listQuestion != null) {
            for (ListItem option : listQuestion.getListItems()) {
                countStr += "count(json_extract(r.response_data,'$." + question.getId() + "." + "\"" + question.getId() + "." + option.getId() + "\"' )) " + question.getId() + "_" + option.getId() + "_option_count, ";
            }
        }
        if (countStr.length() > 0) {
            countStr = countStr.substring(0, countStr.lastIndexOf(","));
            searchVO.setFindFields(countStr);
            List<HashMap<String, Object>> statisticsCount = analyzerDao.getInsectQnaireStatisticsList(searchVO);
            if ("sample".equals(insectVO.getXType()) && "questionnaire".equals(insectVO.getYType())) {
                for (HashMap<String, Object> groupP : groupProperties) {// row
                    List<HashMap<String, Object>> tempStat = new ArrayList<HashMap<String, Object>>();
                    String rowName = "";
                    if (groupP != null) {
                        rowName = (String) groupP.get("name");
                    }
                    rowNames.add(rowName);
                    int total = 0;
                    for (ListItem option : listQuestion.getListItems()) {// col
                        String colName = option.getName();
                        if (!colNames.contains(colName)) {
                            colNames.add(colName);
                        }
                        int selectedCount = 0;
                        HashMap<String, Object> rowColMap = new HashMap<String, Object>();
                        String key = question.getId() + "_"+ option.getId() + "_option_count";
                        for (HashMap<String, Object> stData : statisticsCount) {
                            if(stData.get("name").equals(rowName) && stData.containsKey(key)){
                                selectedCount = Integer.parseInt(stData.get(key).toString());
                                total += selectedCount;
                                break;
                            }
                        }
                        rowColMap.put("count", selectedCount);
                        rowColMap.put("colName", colName);
                        rowColMap.put("rowName", rowName);
                        tempStat.add(rowColMap);
                    }
                    for (Map<String, Object> optionCountMap : tempStat) {
                        String scale = calculateScale((Integer.parseInt(String.valueOf(optionCountMap.get("count")))), total);
                        optionCountMap.put("scale", scale);
                    }
                    // 添加总计
                    HashMap<String, Object> rowColMap = new HashMap<String, Object>();
                    rowColMap.put("count", total);
                    rowColMap.put("colName", "总计");
                    rowColMap.put("rowName", rowName);
                    rowColMap.put("scale", total == 0 ? "0%" : "100%");
                    tempStat.add(rowColMap);
                    rowColStat.addAll(tempStat);
                }
            } else if (("questionnaire".equals(insectVO.getXType()) && "sample".equals(insectVO.getYType()))) {
                for (ListItem option : listQuestion.getListItems()) {// row
                    List<HashMap<String, Object>> tempStat = new ArrayList<HashMap<String, Object>>();
                    String rowName = option.getName();
                    rowNames.add(rowName);
                    int total = 0;
                    String key = question.getId() + "_"+ option.getId() + "_option_count";
                    for (HashMap<String, Object> groupP : groupProperties) {// col
                        String colName = "";
                        if (groupP != null) {
                            colName = (String) groupP.get("name");
                        }
                        if (!colNames.contains(colName)) {
                            colNames.add(colName);
                        }
                        int selectedCount = 0;
                        HashMap<String, Object> rowColMap = new HashMap<>();
                        for (HashMap<String, Object> stData : statisticsCount) {
                            if (stData.containsKey(key) && stData.get("name").equals(colName)) {
                                selectedCount = Integer.parseInt(stData.get(key).toString());
                                total += selectedCount;
                                break;
                            }
                        }
                        rowColMap.put("count", selectedCount);
                        rowColMap.put("colName", colName);
                        rowColMap.put("rowName", rowName);
                        tempStat.add(rowColMap);
                    }
                    for (Map<String, Object> optionCountMap : tempStat) {
                        String scale = calculateScale((Integer.parseInt(String.valueOf(optionCountMap.get("count")))), total);
                        optionCountMap.put("scale", scale);
                    }
                    // 添加总计
                    HashMap<String, Object> rowColMap = new HashMap<String, Object>();
                    rowColMap.put("count", total);
                    rowColMap.put("colName", "总计");
                    rowColMap.put("rowName", rowName);
                    rowColMap.put("scale", total == 0 ? "0%" : "100%");
                    tempStat.add(rowColMap);
                    rowColStat.addAll(tempStat);
                }
            }
        } else {
            if (("questionnaire".equals(insectVO.getXType()) && "questionnaire".equals(insectVO.getYType()))) {
                ListQuestion xQuestion = (ListQuestion) qDocument.getQuestion(insectVO.getXVariable());
                ListQuestion yQuestion = (ListQuestion) qDocument.getQuestion(insectVO.getYVariable());
                question = qDocument.getQuestion(searchVO.getXVariable());
                List<HashMap<String, Object>> statisticsCount = analyzerDao.getInsectQnaireAndQnaireStatisticsList(searchVO);
                for (ListItem yLi : yQuestion.getListItems()) {
                    colNames.add(yLi.getName());
                }
                for (ListItem xLi : xQuestion.getListItems()) {
                    int total = 0;
                    List<HashMap<String, Object>> tempStat = new ArrayList<>();
                    rowNames.add(xLi.getName());
                    for (ListItem yLi : yQuestion.getListItems()) {
                        int selectedCount = 0;
                        HashMap<String, Object> qRowColMap = new HashMap<>();
                        for (HashMap<String, Object> statisticsMap : statisticsCount) {
                            String xFieldValue = (String) statisticsMap.get(insectVO.getXVariable());
                            String yFieldValue = (String) statisticsMap.get(insectVO.getYVariable());
                            if (StringUtils.isNotBlank(xFieldValue) && StringUtils.isNotBlank(yFieldValue)) {
                                JSONObject xFieldObj = JSON.parseObject(xFieldValue);
                                JSONObject yFieldObj = JSON.parseObject(yFieldValue);
                                if (xFieldObj.containsKey(xLi.getItemValueKey()) && xFieldObj.getString(xLi.getItemValueKey()).equals("1")
                                        && yFieldObj.containsKey(yLi.getItemValueKey()) && yFieldObj.getString(yLi.getItemValueKey()).equals("1")) {
                                    selectedCount = Integer.parseInt(statisticsMap.get("statistics_count").toString());
                                    total += selectedCount;
                                    break;
                                }
                            }
                        }
                        qRowColMap.put("count", selectedCount);
                        qRowColMap.put("colName", yLi.getName());
                        qRowColMap.put("rowName", xLi.getName());
                        tempStat.add(qRowColMap);
                        for (Map<String, Object> optionCountMap : tempStat) {
                            String scale = calculateScale((Integer.parseInt(String.valueOf(optionCountMap.get("count")))), total);
                            optionCountMap.put("scale", scale);
                        }
                    }
                    // 添加总计
                    HashMap<String, Object> allRowColMap = new HashMap<>();
                    allRowColMap.put("count", total);
                    allRowColMap.put("colName", "总计");
                    allRowColMap.put("rowName", xLi.getName());
                    allRowColMap.put("scale", total == 0 ? "0%" : "100%");
                    tempStat.add(allRowColMap);
                    rowColStat.addAll(tempStat);
                }
            } else {
                //样本-样本
                int total = 0;
                List<HashMap<String, Object>> samplesProperties = analyzerDao.getInsectSamplesProperty(searchVO);
                Map<String, String> rowNameMap = new HashMap<>();
                Map<String, String> colNameMap = new HashMap<>();
                for (HashMap<String, Object> groupP : samplesProperties) {
                    List<HashMap<String, Object>> tempStat = new ArrayList<>();
                    String rowName = "";
                    String colName = "";
                    if (groupP.get(insectVO.getXVariable()) != null) {
                        rowName = groupP.get(insectVO.getXVariable()).toString();
                    }
                    if (groupP.get(insectVO.getYVariable()) != null) {
                        colName = groupP.get(insectVO.getYVariable()).toString();
                    }
                    rowNameMap.put(rowName, "1");
                    colNameMap.put(colName, "1");
                    int selectedCount = 0;
                    for (HashMap<String, Object> groupS : samplesProperties) {
                        HashMap<String, Object> sRowColMap = new HashMap<>();
                        if (groupS.get(insectVO.getXVariable()).equals(rowName) && groupS.get(insectVO.getYVariable()).equals(colName)) {
                            selectedCount = 1;
                            total = 1;
                        }
                        sRowColMap.put("count", selectedCount);
                        sRowColMap.put("colName", groupS.get(insectVO.getYVariable()));
                        sRowColMap.put("rowName", rowName);
                        tempStat.add(sRowColMap);
                        selectedCount = 0;
                    }
                    for (Map<String, Object> optionCountMap : tempStat) {
                        String scale = (int) optionCountMap.get("count") == 0 ? "0%" : "100%";
                        optionCountMap.put("scale", scale);
                    }
                    // 添加总计
                    HashMap<String, Object> allRowColMap = new HashMap<>();
                    allRowColMap.put("count", total);
                    allRowColMap.put("colName", "总计");
                    allRowColMap.put("rowName", rowName);
                    allRowColMap.put("scale", total == 0 ? "0%" : "100%");
                    tempStat.add(allRowColMap);
                    rowColStat.addAll(tempStat);
                }
                Iterator<Map.Entry<String, String>> rowIter = rowNameMap.entrySet().iterator();
                while (rowIter.hasNext()) {
                    Map.Entry<String, String> entry = rowIter.next();
                    String rowName = entry.getKey();
                    rowNames.add(rowName);
                }
                Iterator<Map.Entry<String, String>> colIter = colNameMap.entrySet().iterator();
                while (colIter.hasNext()) {
                    Map.Entry<String, String> entry = colIter.next();
                    String colName = entry.getKey();
                    colNames.add(colName);
                }
            }
        }
        returnData.put("rowNames", rowNames);
        returnData.put("colNames", colNames);
        returnData.put("rowColData", rowColStat);
        return returnData;
    }

    /**
     * 计算百分比
     *
     * @param num1
     * @param num2
     * @return
     */
    private String calculateScale(int num1, int num2) {
        if (num2 == 0) {
            return "0%";
        }
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) num1 / (float) num2);
        return result;
    }

    /**
     * 打分统计
     *
     * @param markVO
     * @return
     */
    public List<HashMap<String, Object>> getScaleStatistics(AnalysisMarkVO markVO) {

        List<HashMap<String, Object>> scaleList = new ArrayList<>();
        //1、获取符合条件的答卷
        if ("yb".equals(markVO.getType())) {
            scaleList = analyzerDao.getYbStatisticsScoreList(markVO);
        } else if ("mk".equals(markVO.getType())) {
            //获取符合条件答卷
            List<HashMap<String, Object>> resultList = analyzerDao.getMkStatisticsScoreList(markVO);
            //获取当前选择题目
            String xml = questionnaireDao.selectByPrimaryKey(markVO.getQuestionnaireId()).getXmlContent();
            SurvMLDocumentParser parse = new SurvMLDocumentParser(xml);
            SurvMLDocument qDocument = null;
            try {
                qDocument = parse.parse();
            } catch (SurvMLParseException e) {
                e.printStackTrace();
            }
            List<Question> questions = qDocument.getAllSelectQuestions();
            List<HashMap> listItemMap = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                ListQuestion listQuestion = (ListQuestion) questions.get(i);
                if (markVO.getVariable().equals(listQuestion.getId())) {
                    if (listQuestion.getListItems() != null) {
                        for (int j = 0; j < listQuestion.getListItems().size(); j++) {
                            HashMap itemMap = new HashMap();
                            itemMap.put("id", listQuestion.getListItems().get(j).getId());
                            itemMap.put("name", listQuestion.getListItems().get(j).getName());
                            listItemMap.add(itemMap);
                        }
                    }
                    break;
                }
            }
            //计算未选本题的分值
            double totalScore = 0;
            int count = 0;
            for (int i = 0; i < resultList.size(); i++) {
                HashMap<String, Object> tempMap = resultList.get(i);
                if (!com.alibaba.fastjson.JSONObject.parseObject(tempMap.get("responseData").toString()).containsKey(markVO.getVariable())) {
                    totalScore += Double.parseDouble(tempMap.get("score").toString());
                    count++;
                    resultList.remove(tempMap);
                    i--;
                }
            }
            HashMap<String, Object> notMap = new HashMap<>();
            notMap.put("name", "未答" + markVO.getVariable());
            notMap.put("count", count);
            notMap.put("totalScore", totalScore);
            notMap.put("avgScore", count == 0 ? 0.0 : totalScore / (count * 1.0));
            scaleList.add(notMap);
            //计算每一题选择的分值
            for (HashMap tempMap : listItemMap) {
                HashMap<String, Object> hasMap = new HashMap<>();
                hasMap.put("name", tempMap.get("name"));
                double hasTotalScore = 0;
                int hasCount = 0;
                for (int i = 0; i < resultList.size(); i++) {
                    if (com.alibaba.fastjson.JSONObject.parseObject(
                            com.alibaba.fastjson.JSONObject.parseObject(resultList.get(i).get("responseData").toString()).getString(markVO.getVariable())
                    ).containsKey(markVO.getVariable() + "." + tempMap.get("id"))) {
                        hasTotalScore += Double.parseDouble(resultList.get(i).get("score").toString());
                        hasCount++;
                    }
                }
                hasMap.put("count", hasCount);
                hasMap.put("totalScore", hasTotalScore);
                hasMap.put("avgScore", hasCount == 0 ? 0.0 : hasTotalScore / (hasCount * 1.0));
                scaleList.add(hasMap);
            }
        } else if ("fy".equals(markVO.getType())) {
            scaleList = analyzerDao.getFyStatisticsScoreList(markVO);
        }
        int size = scaleList.size();
        HashMap<String, Object> temp = new HashMap<>();
        //按照总分排序
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                HashMap<String, Object> resultA = scaleList.get(j);
                HashMap<String, Object> resultB = scaleList.get(j + 1);
                double totalScoreA = Double.parseDouble(resultA.get("totalScore").toString());
                double totalScoreB = Double.parseDouble(resultB.get("totalScore").toString());
                if (totalScoreA < totalScoreB) {
                    temp.putAll(resultA);
                    resultA.putAll(resultB);
                    resultB.putAll(temp);
                }
            }
        }
        for (int i = 0; i < size; i++) {
            scaleList.get(i).put("totalSort", i + 1);
        }
        //按照平均分排序
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                HashMap<String, Object> resultA = scaleList.get(j);
                HashMap<String, Object> resultB = scaleList.get(j + 1);
                double totalScoreA = Double.parseDouble(resultA.get("avgScore").toString());
                double totalScoreB = Double.parseDouble(resultB.get("avgScore").toString());
                if (totalScoreA < totalScoreB) {
                    temp.putAll(resultA);
                    resultA.putAll(resultB);
                    resultB.putAll(temp);
                }
            }
        }
        for (int i = 0; i < size; i++) {
            scaleList.get(i).put("avgSort", i + 1);
        }
        if (markVO.getSort().contains("Avg")) {
            Collections.sort(scaleList, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer sort1 = Integer.parseInt(o1.get("avgSort").toString());
                    Integer sort2 = Integer.parseInt(o2.get("avgSort").toString());
                    if ("reverseAvg".equals(markVO.getSort())) {
                        return sort2.compareTo(sort1);
                    }
                    return sort1.compareTo(sort2);
                }
            });
        } else if (markVO.getSort().contains("Total")) {
            Collections.sort(scaleList, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer sort1 = Integer.parseInt(o1.get("totalSort").toString());
                    Integer sort2 = Integer.parseInt(o2.get("totalSort").toString());
                    if ("reverseTotal".equals(markVO.getSort())) {
                        return sort2.compareTo(sort1);
                    }
                    return sort1.compareTo(sort2);
                }
            });
        }
        return scaleList;
    }

    /**
     * base64保存至redis
     *
     * @param codeVO
     * @return
     */
    public void saveBaseCode(BaseCodeVO codeVO) {
        redisUtil.set(codeVO.getKey(), codeVO.getImgStrs(), 25 * 60 * 60 * 1000);
    }

    /**
     * 导出pdf
     *
     * @param key
     * @param response
     */
    public void exportPdf(String key, HttpServletResponse response) {
        AnalysisPdfVO analysisPdfVO = new AnalysisPdfVO();
        if (redisUtil.hasKey(key)) {
            analysisPdfVO.setImgStrs((List<List<String>>) redisUtil.get(key));
            redisUtil.remove(key);
        }
        try {
            toFileUtil.createPDF(analysisPdfVO, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "EXPORT PDF FAILURE!");
        }
    }


}
