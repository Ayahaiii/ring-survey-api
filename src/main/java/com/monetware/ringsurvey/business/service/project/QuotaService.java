package com.monetware.ringsurvey.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.ProjectConstants;
import com.monetware.ringsurvey.business.pojo.dto.quota.ModuleSampleDTO;
import com.monetware.ringsurvey.business.pojo.dto.quota.ProjectQuotaImportDTO;
import com.monetware.ringsurvey.business.pojo.dto.quota.QuotaExportDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProject;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectProperty;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectQuestionnaire;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectQuota;
import com.monetware.ringsurvey.business.pojo.vo.quota.*;
import com.monetware.ringsurvey.survml.quota.Quota;
import com.monetware.ringsurvey.survml.quota.QuotaCompileResult;
import com.monetware.ringsurvey.survml.QuestionnaireQuotaSurvMLWriter;
import com.monetware.ringsurvey.survml.SurvMLDocument;
import com.monetware.ringsurvey.survml.SurvMLDocumentParser;
import com.monetware.ringsurvey.survml.compiler.SurvmlCompilerV2;
import com.monetware.ringsurvey.survml.exceptions.SurvMLParseException;
import com.monetware.ringsurvey.survml.questions.ListQuestion;
import com.monetware.ringsurvey.survml.questions.Question;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.file.CommonProperty;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.*;

/**
 * ??????
 *
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class QuotaService {

    @Value("${fileUrl.qnaire}")
    private String questionnaireJsDic;

    @Autowired
    private ProjectQuotaDao quotaDao;

    @Autowired
    private ProjectQuestionnaireDao questionnaireDao;

    @Autowired
    private ProjectPropertyDao propertyDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectModuleQuestionnaireDao moduleQuestionnaireDao;

    /**
     * ??????????????????
     *
     * @param quotaListVO
     * @return
     */
    public PageList<Page> getProjectQuotaList(ProjectQuotaListVO quotaListVO) {
        Page page = PageHelper.startPage(quotaListVO.getPageNum(), quotaListVO.getPageSize());
        quotaDao.getProjectQuotaList(quotaListVO);
        return new PageList<>(page);
    }

    /**
     * ??????????????????
     *
     * @param quotaVO
     * @return
     */
    public int saveProjectQuota(ProjectQuotaVO quotaVO) {
        //TODO ???????????????
        Date now = new Date();
        BaseProject project = projectDao.selectByPrimaryKey(quotaVO.getProjectId());
        BaseProjectQuota projectQuota;
        String exName = "";
        if (null == quotaVO.getId()) {
            projectQuota = new BaseProjectQuota();
        } else {
            projectQuota = quotaDao.selectByPrimaryKey(quotaVO.getId());
            exName = projectQuota.getName();
        }
        BeanUtils.copyProperties(quotaVO, projectQuota);
        // ??????????????????????????????
        if (quotaDao.checkQuotaExistByName(quotaVO.getProjectId(), quotaVO.getName(), exName) > 0) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "?????????????????????");
        }
        // ???????????????????????????
        if (StringUtils.isNotBlank(quotaVO.getQuestionnaireQuota())) {
            ValidateQuotaVO validateQuotaVO = new ValidateQuotaVO();
            validateQuotaVO.setQuotaCode(quotaVO.getQuestionnaireQuota());
            validateQuotaVO.setQuestionnaireId(quotaVO.getQuestionnaireId());
            validateQuotaVO.setType(1);
            QuotaCompileResult questionnaireResult = validateQuotaExpression(validateQuotaVO);
            if (!questionnaireResult.getStatus()) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????????????????");
            } else {
                List<String> questionIds = questionnaireResult.getRefFields();
                String questionnaireQuotaTestExpression = parseQuestionnaireQuota(quotaVO.getQuestionnaireQuota());
                String ruleId = "rule_" + System.currentTimeMillis();
                QuestionnaireQuotaSurvMLWriter writer = new QuestionnaireQuotaSurvMLWriter(
                        questionnaireQuotaTestExpression, ruleId, questionIds);
                quotaVO.setQuestionnaireQuotaSurvml(writer.toXML());
                quotaVO.setRuleSurvmlId(ruleId);
                createQuestionnaireRuleFile(project.getId(), quotaVO.getRuleSurvmlId(), quotaVO.getQuestionnaireQuotaSurvml());
            }
        }

        // ???????????????????????????
        if (StringUtils.isNotBlank(quotaVO.getSampleQuota())) {
            ValidateQuotaVO validateQuotaVO = new ValidateQuotaVO();
            validateQuotaVO.setProjectId(quotaVO.getProjectId());
            validateQuotaVO.setQuotaCode(quotaVO.getSampleQuota());
            validateQuotaVO.setQuestionnaireId(quotaVO.getQuestionnaireId());
            validateQuotaVO.setType(2);
            QuotaCompileResult questionnaireResult = validateQuotaExpression(validateQuotaVO);
            if (!questionnaireResult.getStatus()) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????????????????");
            }
        }
        int row;
        if (null == quotaVO.getId()) {
            projectQuota.setStatus(1);
            projectQuota.setCreateUser(ThreadLocalManager.getUserId());
            projectQuota.setCreateTime(now);
            row = quotaDao.insertSelective(projectQuota);
        } else {
            projectQuota.setLastModifyUser(ThreadLocalManager.getUserId());
            projectQuota.setLastModifyTime(now);
            row = quotaDao.updateByPrimaryKeySelective(projectQuota);
        }
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????");
        }
        return row;
    }

    private boolean createQuestionnaireRuleFile(Integer projectId, String ruleId, String survmlContent) {
        String filePathStr = questionnaireJsDic + "/" + projectId + "-rules";
        File filePath = new File(filePathStr);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        try {
            SurvmlCompilerV2.compileRule(survmlContent, filePathStr, ruleId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String parseQuestionnaireQuota(String str) {
        str = str.trim();
        str = str.replace(" ", "");
        str = str.replace("&", " && ");
        str = str.replace("|", " || ");
        str = str.replace("[", "#{");
        str = str.replace(",", ".");
        str = str.replace("]", "}==1");
        return str;
    }

    /**
     * ?????????????????????????????????
     *
     * @param projectId
     * @return
     */
    public List<ModuleSampleDTO> getModuleSample(Integer projectId) {
        return quotaDao.getModuleSample(projectId);
    }


    /**
     * ????????????
     *
     * @param deleteVO
     * @return
     */
    public int deleteProjectQuota(QuotaDeleteVO deleteVO) {
        Example example = new Example(BaseProjectQuota.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", deleteVO.getProjectId());
        criteria.andIn("id", deleteVO.getQuotaIds());
        List<BaseProjectQuota> projectQuotas = quotaDao.selectByExample(example);
        for (BaseProjectQuota quota : projectQuotas) {
            if (quota.getStatus().equals(ProjectConstants.OPEN)) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, quota.getName() + "???????????????");
            }
        }
        // ????????????
        return quotaDao.deleteByExample(example);
    }

    /**
     * ????????????
     *
     * @param quotaId
     * @return
     */
    public BaseProjectQuota getQuotaDetail(Integer quotaId) {
        return quotaDao.selectByPrimaryKey(quotaId);
    }

    /**
     * ??????????????????
     * 0:?????? 1:??????
     *
     * @return
     */
    public Integer updateQuotaStatus(ProjectQuotaStatusVO quotaStatusVO) {
        BaseProjectQuota quota = new BaseProjectQuota();
        quota.setId(quotaStatusVO.getId());
        quota.setStatus(quotaStatusVO.getStatus());
        quota.setLastModifyTime(new Date());
        quota.setLastModifyUser(ThreadLocalManager.getUserId());
        return quotaDao.updateByPrimaryKeySelective(quota);
    }

    /**
     * ????????????
     *
     * @param exportVO
     * @return
     */
    public List<QuotaExportDTO> exportQuota(QuotaExportVO exportVO) {
        return quotaDao.exportQuota(exportVO.getQuotaIds());
    }

    /**
     * ????????????
     *
     * @param batchImportQuotaVO
     * @return
     */
    public ProjectQuotaImportDTO insertQuotaByImport(BatchImportQuotaVO batchImportQuotaVO) {
        Date date = new Date();
        List<QuotaImportVO> errorList = new ArrayList<>();
        List<QuotaImportVO> correctList = new ArrayList<>();
        Map<String, List<QuotaImportVO>> resultMap = new HashMap<>();
        ProjectQuotaImportDTO importDTO = new ProjectQuotaImportDTO();
        for (QuotaImportVO importVO : batchImportQuotaVO.getQuotaList()) {
            // ?????????
            if (StringUtils.isBlank(importVO.getName())) {
                errorList.add(importVO);
                continue;
            } else {
                // ????????????????????????
                if (quotaDao.checkQuotaExistByName(batchImportQuotaVO.getProjectId(), importVO.getName(), null) > 0) {
                    errorList.add(importVO);
                    continue;
                }
            }

            // ????????????
            if (null == importVO.getLowerLimit()) {
                importVO.setLowerLimit(0);
            }
            // ????????????
            if (null == importVO.getUpperLimit()) {
                importVO.setUpperLimit(1);
            }
            // ??????????????????
            if (importVO.getUpperLimit() < importVO.getLowerLimit()) {
                errorList.add(importVO);
                continue;
            }
            // ????????????,????????????,???????????????
            if (StringUtils.isBlank(importVO.getQuestionnaireQuota()) && StringUtils.isBlank(importVO.getCode()) && StringUtils.isBlank(importVO.getSampleQuota())) {
                errorList.add(importVO);
                continue;
            }

            if (StringUtils.isNotBlank(importVO.getSampleQuota())) {// ???????????? ?????????
                if (StringUtils.isBlank(importVO.getCode())) {// ???????????????
                    // ????????????
                    ValidateQuotaVO validateSampleVO = new ValidateQuotaVO();
                    validateSampleVO.setProjectId(batchImportQuotaVO.getProjectId());
                    validateSampleVO.setType(2);
                    validateSampleVO.setQuotaCode(importVO.getSampleQuota());
                    QuotaCompileResult sampleResult = validateQuotaExpression(validateSampleVO);
                    if (!sampleResult.getStatus()) {
                        errorList.add(importVO);
                        continue;
                    } else {
                        BaseProjectQuota quota = new BaseProjectQuota();
                        quota.setName(importVO.getName());
                        quota.setProjectId(batchImportQuotaVO.getProjectId());
                        quota.setStatus(1);
                        quota.setType(2);
                        quota.setSampleQuota(importVO.getSampleQuota());
                        quota.setLowerLimit(importVO.getLowerLimit());
                        quota.setUpperLimit(importVO.getUpperLimit());
                        quota.setCreateUser(ThreadLocalManager.getUserId());
                        quota.setCreateTime(date);
                        quota.setLastModifyTime(date);
                        quota.setLastModifyUser(ThreadLocalManager.getUserId());
                        correctList.add(importVO);
                        quotaDao.insertSelective(quota);
                    }
                } else {
                    if (StringUtils.isBlank(importVO.getQuestionnaireQuota())) {// ??????????????????????????????
                        errorList.add(importVO);
                        continue;
                    } else {
                        Integer questionnaireId = moduleQuestionnaireDao.getQuestionnaireIdByCode(batchImportQuotaVO.getProjectId(), importVO.getCode());
                        if (null == questionnaireId) {
                            errorList.add(importVO);
                            continue;
                        } else {
                            // ???????????? + ????????????
                            ValidateQuotaVO validateQuestionnaireVO = new ValidateQuotaVO();
                            validateQuestionnaireVO.setProjectId(batchImportQuotaVO.getProjectId());
                            validateQuestionnaireVO.setType(1);
                            validateQuestionnaireVO.setQuestionnaireId(questionnaireId);
                            validateQuestionnaireVO.setQuotaCode(importVO.getQuestionnaireQuota());
                            QuotaCompileResult questionnaireResult = validateQuotaExpression(validateQuestionnaireVO);

                            ValidateQuotaVO validateSampleVO = new ValidateQuotaVO();
                            validateSampleVO.setProjectId(batchImportQuotaVO.getProjectId());
                            validateSampleVO.setType(2);
                            validateSampleVO.setQuotaCode(importVO.getSampleQuota());
                            QuotaCompileResult sampleResult = validateQuotaExpression(validateSampleVO);
                            if (!questionnaireResult.getStatus() && !sampleResult.getStatus()) {
                                errorList.add(importVO);
                                continue;
                            } else {
                                List<String> questionIds = questionnaireResult.getRefFields();
                                String questionnaireQuotaTestExpression = parseQuestionnaireQuota(importVO.getQuestionnaireQuota());
                                String ruleId = "rule_" + System.currentTimeMillis();
                                QuestionnaireQuotaSurvMLWriter writer = new QuestionnaireQuotaSurvMLWriter(
                                        questionnaireQuotaTestExpression, ruleId, questionIds);
                                importVO.setQuestionnaireQuotaSurvml(writer.toXML());
                                importVO.setRuleSurvmlId(ruleId);
                                createQuestionnaireRuleFile(batchImportQuotaVO.getProjectId(), importVO.getRuleSurvmlId(), importVO.getQuestionnaireQuotaSurvml());

                                BaseProjectQuota quota = new BaseProjectQuota();
                                quota.setName(importVO.getName());
                                quota.setProjectId(batchImportQuotaVO.getProjectId());
                                quota.setQuestionnaireQuota(importVO.getQuestionnaireQuota());
                                quota.setSampleQuota(importVO.getSampleQuota());
                                quota.setStatus(1);
                                quota.setType(3);
                                quota.setQuestionnaireId(questionnaireId);
                                quota.setLowerLimit(importVO.getLowerLimit());
                                quota.setUpperLimit(importVO.getUpperLimit());
                                quota.setCreateUser(ThreadLocalManager.getUserId());
                                quota.setCreateTime(date);
                                quota.setLastModifyTime(date);
                                quota.setLastModifyUser(ThreadLocalManager.getUserId());
                                correctList.add(importVO);
                                quotaDao.insertSelective(quota);
                            }
                        }
                    }
                }
            } else {// ???????????????
                if (StringUtils.isBlank(importVO.getCode())) {// ???????????????
                    errorList.add(importVO);
                    continue;
                } else {
                    if (StringUtils.isBlank(importVO.getQuestionnaireQuota())) {
                        errorList.add(importVO);
                        continue;
                    } else {
                        Integer questionnaireId = moduleQuestionnaireDao.getQuestionnaireIdByCode(batchImportQuotaVO.getProjectId(), importVO.getCode());
                        if (null == questionnaireId) {
                            errorList.add(importVO);
                            continue;
                        } else {
                            // ????????????
                            ValidateQuotaVO validateQuestionnaireVO = new ValidateQuotaVO();
                            validateQuestionnaireVO.setProjectId(batchImportQuotaVO.getProjectId());
                            validateQuestionnaireVO.setType(1);
                            validateQuestionnaireVO.setQuestionnaireId(questionnaireId);
                            validateQuestionnaireVO.setQuotaCode(importVO.getQuestionnaireQuota());
                            QuotaCompileResult questionnaireResult = validateQuotaExpression(validateQuestionnaireVO);
                            if (!questionnaireResult.getStatus()) {
                                errorList.add(importVO);
                                continue;
                            } else {
                                List<String> questionIds = questionnaireResult.getRefFields();
                                String questionnaireQuotaTestExpression = parseQuestionnaireQuota(importVO.getQuestionnaireQuota());
                                String ruleId = "rule_" + System.currentTimeMillis();
                                QuestionnaireQuotaSurvMLWriter writer = new QuestionnaireQuotaSurvMLWriter(
                                        questionnaireQuotaTestExpression, ruleId, questionIds);
                                importVO.setQuestionnaireQuotaSurvml(writer.toXML());
                                importVO.setRuleSurvmlId(ruleId);
                                createQuestionnaireRuleFile(batchImportQuotaVO.getProjectId(), importVO.getRuleSurvmlId(), importVO.getQuestionnaireQuotaSurvml());

                                BaseProjectQuota quota = new BaseProjectQuota();
                                quota.setName(importVO.getName());
                                quota.setProjectId(batchImportQuotaVO.getProjectId());
                                quota.setQuestionnaireQuota(importVO.getQuestionnaireQuota());
                                quota.setStatus(1);
                                quota.setType(1);
                                quota.setQuestionnaireId(questionnaireId);
                                quota.setLowerLimit(importVO.getLowerLimit());
                                quota.setUpperLimit(importVO.getUpperLimit());
                                quota.setCreateUser(ThreadLocalManager.getUserId());
                                quota.setCreateTime(date);
                                quota.setLastModifyTime(date);
                                quota.setLastModifyUser(ThreadLocalManager.getUserId());
                                correctList.add(importVO);
                                quotaDao.insertSelective(quota);
                            }
                        }
                    }
                }
            }
        }
        resultMap.put("correct", correctList);
        resultMap.put("error", errorList);
        importDTO.setResultMap(resultMap);
        return importDTO;
    }

    /**
     * ??????????????????????????????
     *
     * @param questionnaireId
     * @return
     */
    public List<HashMap> getSelectQuestions(Integer questionnaireId) {
        BaseProjectQuestionnaire surveyQuestionnaire = questionnaireDao.selectByPrimaryKey(questionnaireId);
        SurvMLDocumentParser parse = new SurvMLDocumentParser(surveyQuestionnaire.getXmlContent());
        SurvMLDocument qDocument = null;
        try {
            qDocument = parse.parse();
        } catch (SurvMLParseException e) {
            e.printStackTrace();
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????");
        }
        List<Question> questions = qDocument.getSelectQuestions();

        List<HashMap> questionNews = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            ListQuestion listQuestion = (ListQuestion) questions.get(i);
            HashMap<String, Object> map = new HashMap();
            listQuestion.setDocument(null);
            map.put("name", listQuestion.getTitle());
            map.put("id", listQuestion.getId());
            // ??????
            List<HashMap> listItemMap = new ArrayList<>();
            if (listQuestion.getListItems() != null) {
                for (int j = 0; j < listQuestion.getListItems().size(); j++) {
                    HashMap<String, String> itemMap = new HashMap();
                    itemMap.put("id", listQuestion.getListItems().get(j).getId());
                    itemMap.put("name", listQuestion.getListItems().get(j).getName());
                    listItemMap.add(itemMap);
                }
            }
            map.put("listItem", listItemMap);
            questionNews.add(map);
        }
        return questionNews;
    }


    /**
     * ??????????????????????????????
     *
     * @param validateQuotaVO
     * @return
     */
    public QuotaCompileResult validateQuotaExpression(ValidateQuotaVO validateQuotaVO) {
        QuotaCompileResult result = new QuotaCompileResult();
        // ????????????
        if (validateQuotaVO.getType().equals(1)) {
            // ???????????????????????????
            BaseProjectQuestionnaire questionnaire = questionnaireDao.selectByPrimaryKey(validateQuotaVO.getQuestionnaireId());
            SurvMLDocument qDocument;
            try {
                qDocument = new SurvMLDocumentParser(questionnaire.getXmlContent().toString()).parse();
                Quota quota = new Quota("", validateQuotaVO.getQuotaCode(), qDocument);
                result = quota.scan();
                if (result.getStatus()) {
                    result.setRefFields(quota.getQuotaCondition().getRefFields());
                }
            } catch (SurvMLParseException e) {
                e.printStackTrace();
                result.setStatus(false);
                result.setMessage(e.getMessage());
            }
        } else if (validateQuotaVO.getType().equals(2)) {
            // ????????????  ????????????
            Example example = new Example(BaseProjectProperty.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("projectId", validateQuotaVO.getProjectId());
            BaseProjectProperty sampleProperty = propertyDao.selectOneByExample(example);
            HashSet<String> sampleColumns = new HashSet<String>();
            if (sampleProperty != null && StringUtils.isNotBlank(sampleProperty.getUseProperty())) {
                JSONArray array = JSONArray.parseArray(sampleProperty.getUseProperty());
                //sampleColumns.addAll(JSONObject.parseObject(sampleProperty.getMarkProperty(), HashSet.class));
                for (int i = 0; i < array.size(); i++) {
                    sampleColumns.add(CommonProperty.sampleProperty2Field.get(array.get(i)));
                }
            } else {
                sampleColumns.add("name");
                sampleColumns.add("code");
            }
            Quota quota = new Quota("", validateQuotaVO.getQuotaCode(), sampleColumns);
            result = quota.scan();
        }
        return result;
    }

    /**
     * ??????????????????
     *
     * @param projectId
     * @return
     */
    public HashMap<Object, String> getMarkProperty(Integer projectId) {
        HashMap<Object, String> hashMap = new HashMap<>();
        Example example = new Example(BaseProjectProperty.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        BaseProjectProperty property = propertyDao.selectOneByExample(example);
        JSONArray useArray = JSON.parseArray(property.getUseProperty());
        Map<String, Object> allArray = (Map<String, Object>) JSON.parse(property.getAllProperty());
        if (useArray.size() > 0) {
            for (int i = 0; i < useArray.size(); i++) {
                hashMap.put(allArray.get(useArray.get(i)), CommonProperty.sampleProperty2Field.get(useArray.get(i)));
            }
        }
        return hashMap;
    }

}
