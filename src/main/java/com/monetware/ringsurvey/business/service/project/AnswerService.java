package com.monetware.ringsurvey.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.*;
import com.monetware.ringsurvey.business.pojo.dto.history.ExportHistoryDTO;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.ProjectModuleQuestionListDTO;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionnaireParseDTO;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionSelectedDTO;
import com.monetware.ringsurvey.business.pojo.dto.response.*;
import com.monetware.ringsurvey.business.pojo.dto.sample.SamplePropertyDTO;
import com.monetware.ringsurvey.business.pojo.vo.history.HistoryListVO;
import com.monetware.ringsurvey.business.pojo.vo.response.*;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectExportHistory;
import com.monetware.ringsurvey.business.pojo.po.BaseResponse;
import com.monetware.ringsurvey.business.pojo.po.BaseResponseAudio;
import com.monetware.ringsurvey.business.pojo.po.BaseResponseFile;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.survml.SurvMLDocument;
import com.monetware.ringsurvey.survml.SurvMLDocumentParser;
import com.monetware.ringsurvey.survml.compiler.SurvmlModel;
import com.monetware.ringsurvey.survml.compiler.SurvmlModel.*;
import com.monetware.ringsurvey.survml.compiler.SurvmlParse;
import com.monetware.ringsurvey.survml.questions.*;
import com.monetware.ringsurvey.survml.quota.Quota;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.codec.UUIDUtil;
import com.monetware.ringsurvey.system.util.date.DateUtil;
import com.monetware.ringsurvey.system.util.file.*;
import com.monetware.ringsurvey.system.util.reflect.ReflectUtil;
import com.monetware.ringsurvey.system.util.survml.SurvmlUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class AnswerService {

    @Value("${fileUrl.upload}")
    private String filePath;

    @Autowired
    private ProjectResponseDao responseDao;

    @Autowired
    private ProjectResponseAudioDao audioDao;

    @Autowired
    private ProjectResponseFileDao fileDao;

    @Autowired
    private ProjectResponseHistoryDao historyDao;

    @Autowired
    private ProjectQuestionnaireDao pQuestionnaireDao;

    @Autowired
    private ProjectSampleDao sampleDao;

    @Autowired
    private SampleService sampleService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectExportDao exportDao;

    @Autowired
    private ProjectModuleQuestionnaireDao moduleQuestionnaireDao;

    @Autowired
    private ProjectResponseAuditDao responseAuditDao;

    @Autowired
    private ProjectSampleStatusRecordDao statusRecordDao;

    @Autowired
    private ProjectSampleAssignDao assignDao;

    @Autowired
    private ProjectPropertyDao propertyDao;

    @Autowired
    private ProjectModuleDao moduleDao;

    @Autowired
    private ProjectResponseFileDao responseFileDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RedPacketService redPacketService;

    @Autowired
    private ProjectQuotaDao quotaDao;

    @Autowired
    private UserDao userDao;

    /**
     * 答卷列表
     *
     * @param responseListVO
     * @return
     */
    public PageList<Page> getResponseList(ResponseListVO responseListVO) {
        Example example = new Example(BaseProjectProperty.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", responseListVO.getProjectId());
        BaseProjectProperty property = propertyDao.selectOneByExample(example);
        JSONArray array = null;
        if (property != null && property.getMarkProperty() != null) {
            array = JSONObject.parseArray(property.getMarkProperty());
        }
        Page page = PageHelper.startPage(responseListVO.getPageNum(), responseListVO.getPageSize());
        List<ResponseListDTO> responseList = responseDao.getResponseList(responseListVO);
        // 筛选样本标识
        if (!StringUtils.isBlank(responseListVO.getSampleMark())) {
            Iterator<ResponseListDTO> iterator = responseList.listIterator();
            while (iterator.hasNext()) {
                ResponseListDTO r = iterator.next();
                JSONObject responseObj = (JSONObject) JSON.toJSON(r);
                boolean flag = true;
                for (int i = 0; i < array.size(); i++) {
                    if (responseObj.get(array.get(i)) == null) {
                        continue;
                    }
                    int sIndex = ((String) responseObj.get(array.get(i))).indexOf(responseListVO.getSampleMark());
                    if (sIndex >= 0) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    iterator.remove();
                }
            }
        }
        // 关键词搜索
        if (!StringUtils.isBlank(responseListVO.getKeyword())) {
            Iterator<ResponseListDTO> iterator = responseList.listIterator();
            while (iterator.hasNext()) {
                ResponseListDTO r = iterator.next();
                JSONObject responseObj = (JSONObject) JSON.toJSON(r);
                boolean flag = true;
                for (int i = 0; i < array.size(); i++) {
                    if (responseObj.get(array.get(i)) != null && ((String) responseObj.get(array.get(i))).indexOf(responseListVO.getKeyword()) >= 0) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    iterator.remove();
                }
            }
        }
        for (ResponseListDTO response : responseList) {
            String sampleMark = "";
            JSONObject responseObj = (JSONObject) JSON.toJSON(response);
            response.setDuration(DateUtil.secondToHourMinuteSecondChineseStrByLong(Long.valueOf(response.getDuration())));
            for (int i = 0; array != null && i < array.size(); i++) {
                Object obj = responseObj.get(array.get(i));
                if (array.get(i).equals("lastModifyTime")) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sampleMark += sf.format(responseObj.get(array.get(i))) + ",";
                    continue;
                }
                if (obj != null && StringUtils.isNotBlank(obj.toString())) {
                    sampleMark += responseObj.get(array.get(i)) + ",";
                }
            }
            if (sampleMark.length() > 0) {
                response.setSampleMark(sampleMark.substring(0, sampleMark.lastIndexOf(",")));
            }
        }
        return new PageList<>(page);
    }

    /**
     * 答卷列表页面的搜索条件
     *
     * @param projectId
     * @return
     */
    public SearchInfoDTO getSearchInfo(Integer projectId) {
        SearchInfoDTO searchInfo = new SearchInfoDTO();
        List<QuestionSelectedDTO> questionSelectedList = questionnaireService.getQuotaQuestionSelectedList(projectId);
        // 访问员
        List<InterviewerDTO> interviewerList = assignDao.getInterviewList(projectId);
        searchInfo.setQuestionSelectedList(questionSelectedList);
        searchInfo.setInterviewerList(interviewerList);
        return searchInfo;
    }

    /**
     * 获取访问员列表
     *
     * @param projectId
     * @return
     */
    public List<InterviewerDTO> getInterviewerList(Integer projectId) {
        return assignDao.getInterviewList(projectId);
    }

    /**
     * 查询某一份答卷
     *
     * @param projectId
     * @param responseId
     * @param responseGuid
     * @return
     */
    public BaseResponse getResponse(Integer projectId, Integer responseId, String responseGuid) {
        Example example = new Example(BaseResponse.class);
        example.setTableName(ProjectConstants.getResponseTableName(projectId));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", responseId);
        criteria.orEqualTo("responseGuid", responseGuid);
        return responseDao.selectOneByExample(example);
    }

    /**
     * 抽样审核
     *
     * @param samplingVO
     * @return
     */
    public Integer updateResponseSampling(ResponseSamplingVO samplingVO) {
        if (samplingVO.getSamplingPercent() != null) {
            Example example = new Example(BaseResponse.class);
            example.setTableName(ProjectConstants.getResponseTableName(samplingVO.getProjectId()));
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("responseStatus", ResponseConstants.RESPONSE_STATUS_SUCCESS);
            int count = responseDao.selectCountByExample(example);
            samplingVO.setSamplingCount((int) Math.round(count * samplingVO.getSamplingPercent() * 0.01));
        }
        return responseDao.updateResponseSampling(samplingVO.getProjectId(), samplingVO.getSamplingCount());
    }

    /**
     * 重置抽审
     *
     * @param baseVO
     * @return
     */
    public Integer updateResponseInit(BaseVO baseVO) {
        return responseDao.updateResponseInit(baseVO.getProjectId());
    }

    /**
     * 录音列表
     *
     * @param audioVO
     * @return
     */
    public PageList<Page> getAudioList(ResponseFileListVO audioVO) {
        Date now = new Date();
        Page page = PageHelper.startPage(audioVO.getPageNum(), audioVO.getPageSize());
        audioVO.setUserId(ThreadLocalManager.getUserId());
        List<ResponseAudioListDTO> audioList = audioDao.getResponseAudioList(audioVO);
        for (ResponseAudioListDTO listDTO : audioList) {
            File file = new File(filePath + listDTO.getFilePath());
            if (file.exists()) {
                listDTO.setSize(FileUtil.byteFormat(file.length(), true));
            }
            long ct = DateUtil.getDateDuration(listDTO.getCreateTime(), now);
            if (ct < 7 * 24 * 60 * 60L) {
                listDTO.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(ct));
            }
            long d = Long.parseLong(listDTO.getDuration());
            listDTO.setDurationStr(DateUtil.secondToHourMinuteSecondEnStrByLong(d));
        }
        return new PageList<>(page);
    }

    /**
     * 附件列表
     *
     * @param fileVO
     * @return
     */
    public PageList<Page> getResponseFileList(ResponseFileListVO fileVO) {
        Page page = PageHelper.startPage(fileVO.getPageNum(), fileVO.getPageSize());
        fileVO.setUserId(ThreadLocalManager.getUserId());
        responseFileDao.getResponseFileList(fileVO);
        return new PageList<>(page);
    }

    /**
     * 删除/批量删除录音或附件
     *
     * @param fileVO
     * @return
     */
    public Integer deleteResponseFile(DeleteFileVO fileVO) {
        //录音
        if (fileVO.getType() == 1) {
            Example example = new Example(BaseResponseFile.class);
            example.setTableName(ProjectConstants.getResponseAudioTableName(fileVO.getProjectId()));
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", fileVO.getFileIds());
            return audioDao.deleteByExample(example);
        } else {
            Example example = new Example(BaseResponseFile.class);
            example.setTableName(ProjectConstants.getResponseFileTableName(fileVO.getProjectId()));
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", fileVO.getFileIds());
            return fileDao.deleteByExample(example);
        }
    }

    /**
     * 历史答卷列表
     *
     * @param historyListVO
     * @return
     */
    public PageList<Page> getResponseHistory(ResponseHistoryListVO historyListVO) {
        Example example = new Example(BaseProjectProperty.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", historyListVO.getProjectId());
        BaseProjectProperty property = propertyDao.selectOneByExample(example);
        JSONArray array = null;
        if (property != null && property.getMarkProperty() != null) {
            array = JSONObject.parseArray(property.getMarkProperty());
        }
        Page page = PageHelper.startPage(historyListVO.getPageNum(), historyListVO.getPageSize());
        List<ResponseHistoryListDTO> responseHistoryList = historyDao.getResponseHistoryList(historyListVO);
        for (ResponseHistoryListDTO historyList : responseHistoryList) {
            String sampleMark = "";
            JSONObject responseObj = (JSONObject) JSON.toJSON(historyList);
            historyList.setResponseDuration(DateUtil.secondToHourMinuteSecondChineseStrByLong(Long.valueOf(historyList.getResponseDuration())));
            for (int i = 0; array != null && i < array.size(); i++) {
                if (StringUtils.isNotBlank((String) responseObj.get(array.get(i)))) {
                    sampleMark += responseObj.get(array.get(i)) + ",";
                }
            }
            if (sampleMark.length() > 0) {
                historyList.setSampleMark(sampleMark.substring(0, sampleMark.lastIndexOf(",")));
            }
        }
        return new PageList<>(page);
    }

    /**
     * 审核页面的相关信息
     *
     * @param infoVO
     * @return
     */
    public ResponseAuditInfoDTO getResponseAuditInfo(ResponseAuditInfoVO infoVO) {
        ProjectConfigDTO config = projectService.getProjectConfig(infoVO.getProjectId());
        // 答卷信息
        ResponseAuditInfoDTO responseAuditInfo = responseDao.getResponseAuditInfo(infoVO);
        Integer projectId = infoVO.getProjectId();
        BaseResponse response = null;
        BaseRedPacketRecord record = null;
        if (infoVO.getFrom() == 1) {
            response = this.getResponse(projectId, infoVO.getResponseId(), infoVO.getResponseGuid());
        } else {
            record = redPacketService.getRedPacketRecord(projectId, infoVO.getRecordId(), null);
            response = this.getResponse(projectId, null, record.getResponseGuid());
        }
        // 问卷选项及答案
        List<HashMap<String, Object>> answeredQuestions = getAnsweredQuestions(projectId, infoVO.getQuestionnaireId(), response.getId(), response.getResponseGuid(), 1);
        responseAuditInfo.setAnsweredQuestions(answeredQuestions);
        // 答卷时长
        responseAuditInfo.setResponseDurationStr(DateUtil.secondToHourMinuteSecondChineseStrByLong(responseAuditInfo.getResponseDuration()));
        // 审核日志
        List<ResponseAuditLogDTO> responseAuditLogs = responseDao.getResponseAuditLogs(response.getResponseGuid(), projectId);
        responseAuditInfo.setResponseAuditLogs(responseAuditLogs);
        // 样本属性
        List<HashMap<String, Object>> samplePropertyMapList = new ArrayList<>();
        BaseProjectSample sample = sampleService.getSampleByGuid(projectId, response.getSampleGuid());
        HashMap<String, Object> samplePropertyMap = sampleDao.getSampleMapById(projectId, sample.getId());
        SamplePropertyDTO sampleProperty = sampleService.getSampleProperty(projectId);
        JSONArray useProperty = JSONArray.parseArray(sampleProperty.getUseProperty());
        if (sampleProperty.getUseProperty() != null && samplePropertyMap != null) {
            for (Object obj : useProperty) {
                HashMap<String, Object> propertyMap = new HashMap<>();
                propertyMap.put((String) obj, samplePropertyMap.get(obj));
                samplePropertyMapList.add(propertyMap);
            }
        }
        responseAuditInfo.setSamplePropertyMapList(samplePropertyMapList);

        // 录音列表
        if (config.getResponseAudio() == ProjectConstants.OPEN) {
            Example example = new Example(BaseResponseAudio.class);
            example.setTableName(ProjectConstants.getResponseAudioTableName(infoVO.getProjectId()));
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("responseGuid", infoVO.getResponseGuid());
            List<BaseResponseAudio> audioList = audioDao.selectByExample(example);
            responseAuditInfo.setAudioList(audioList);
        }

        ResponseAuditInfoVO tempInfo = null;
        BaseProjectSample tempSample = null;
        BaseRedPacketRecord tempRecord = null;
        // 是否存在上一个
        if (response.getId() == 1 || (infoVO.getFrom() == 2 && infoVO.getRecordId() == 1)) {
            responseAuditInfo.setHasPrev(false);
        } else {
            responseAuditInfo.setHasPrev(true);
            BaseResponse prevResponse;
            tempInfo = new ResponseAuditInfoVO();
            if (infoVO.getFrom() == 1) {
                prevResponse = this.getResponse(projectId, response.getId() - 1, null);
                tempInfo.setFrom(1);
            } else {
                tempRecord = redPacketService.getRedPacketRecord(projectId, infoVO.getRecordId() - 1, null);
                prevResponse = this.getResponse(projectId, null, tempRecord.getResponseGuid());
                tempInfo.setFrom(2);
                tempInfo.setRecordId(tempRecord.getId());
            }
            tempSample = sampleService.getSampleByGuid(projectId, prevResponse.getSampleGuid());
            tempInfo.setSampleId(tempSample.getId());
            tempInfo.setQuestionnaireId(prevResponse.getQuestionnaireId());
            tempInfo.setResponseId(prevResponse.getId());
            tempInfo.setResponseGuid(prevResponse.getResponseGuid());
            responseAuditInfo.setPrevInfo(tempInfo);
        }
        // 是否存在下一个
        BaseResponse nextResponse = this.getResponse(projectId, response.getId() + 1, null);
        if (null == nextResponse) {
            responseAuditInfo.setHasNext(false);
        } else {
            responseAuditInfo.setHasNext(true);
            tempInfo = new ResponseAuditInfoVO();
            if (infoVO.getFrom() == 1) {
                tempInfo.setFrom(1);
                tempSample = sampleService.getSampleByGuid(projectId, nextResponse.getSampleGuid());
                tempInfo.setSampleId(tempSample.getId());
                tempInfo.setQuestionnaireId(nextResponse.getQuestionnaireId());
                tempInfo.setResponseId(nextResponse.getId());
                tempInfo.setResponseGuid(nextResponse.getResponseGuid());
                responseAuditInfo.setNextInfo(tempInfo);
            } else {
                tempRecord = redPacketService.getRedPacketRecord(projectId, infoVO.getRecordId() + 1, null);
                if (null == tempRecord) {
                    responseAuditInfo.setHasNext(false);
                } else {
                    nextResponse = this.getResponse(projectId, null, tempRecord.getResponseGuid());
                    tempInfo.setFrom(2);
                    tempInfo.setRecordId(tempRecord.getId());
                    tempSample = sampleService.getSampleByGuid(projectId, nextResponse.getSampleGuid());
                    tempInfo.setSampleId(tempSample.getId());
                    tempInfo.setQuestionnaireId(nextResponse.getQuestionnaireId());
                    tempInfo.setResponseId(nextResponse.getId());
                    tempInfo.setResponseGuid(nextResponse.getResponseGuid());
                    responseAuditInfo.setNextInfo(tempInfo);
                }
            }
        }
        // 是否含有红包
        boolean hasRedPacket = false;
        boolean hasSendFlag = false;
        if (infoVO.getFrom() == 2) {
            hasRedPacket = true;
            if (!record.getStatus().equals(RedPacketConstants.SEND_WAIT)) {
                hasSendFlag = true;
            }
        }
//        ProjectConfigDTO config = projectService.getProjectConfig(projectId);
//        if (config.getAllowRedPacket().equals(ProjectConstants.OPEN)) {
//            BaseRedPacketConfig redPacketConfig = redPacketService.getRedPacketConfig(projectId);
//            if (redPacketConfig.getSendType().equals(RedPacketConstants.SEND_TYPE_AUDIT)) {
//                if (!record.getStatus().equals(RedPacketConstants.SEND_WAIT)) {
//                    hasRedPacket = true;
//                }
//            }
//        }
        responseAuditInfo.setHasRedPacket(hasRedPacket);
        responseAuditInfo.setHasSendFlag(hasSendFlag);
        return responseAuditInfo;
    }

    /**
     * 答卷审核
     *
     * @param auditVO
     * @return
     */
    public Integer saveResponseAudit(ResponseAuditVO auditVO) {
        BaseProject project = projectDao.selectByPrimaryKey(auditVO.getProjectId());
        ProjectConfigDTO config = projectService.getProjectConfig(project.getId());
        // 答卷
        BaseResponse response = this.getResponse(project.getId(), auditVO.getResponseId(), null);
        // 项目问卷
        BaseProjectQuestionnaire projectQuestionnaire = pQuestionnaireDao.selectByPrimaryKey(response.getQuestionnaireId());
        // 样本
        BaseProjectSample sample = sampleService.getSampleByGuid(project.getId(), response.getSampleGuid());
        HashMap<String, Object> sampleMap = sampleService.getSampleMapByGuid(project.getId(), sample.getSampleGuid());

        Integer postResponseType = null;
        Integer postResponseStatus = null;
        Integer sampleStatus = null;

        boolean isFirstAudit = false;
        boolean isSecondAudit = false;
        boolean isThirdAudit = false;
        if (response.getAuditResult() == null) {
            isFirstAudit = true;
        } else {
            if (ResponseConstants.AUDIT_RESULT_INVALID_ONE.equals(response.getAuditResult()) ||
                    ResponseConstants.AUDIT_RESULT_VALID_ONE.equals(response.getAuditResult())) {
                isSecondAudit = true;
            } else if (ResponseConstants.AUDIT_RESULT_INVALID_TWO.equals(response.getAuditResult()) ||
                    ResponseConstants.AUDIT_RESULT_VALID_TWO.equals(response.getAuditResult())) {
                //上次二审
                isThirdAudit = true;
            } else if (ResponseConstants.AUDIT_RESULT_INVALID_THREE.equals(response.getAuditResult()) ||
                    ResponseConstants.AUDIT_RESULT_VALID_THREE.equals(response.getAuditResult())) {
                //上次三审
                isThirdAudit = true;
            } else {
                isFirstAudit = true;
            }
        }
        if (ResponseConstants.AUDIT_RESULT_VALID.equals(auditVO.getAuditResult())) {
            if (config.getMultipleAudit() == 1) {
                //一审就是终审
                auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_VALID_THREE);
                postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_THIRD_SUCCESS;
            } else if (config.getMultipleAudit() == 2) {
                //二审就是终审
                if (isFirstAudit || isSecondAudit) {
                    if (isFirstAudit) {
                        auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_VALID_ONE);
                        postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_FIRST_SUCCESS;
                    }
                    if (isSecondAudit) {
                        auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_VALID_THREE);
                        postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_THIRD_SUCCESS;
                    }
                } else {
                    auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_VALID_ONE);
                    postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_FIRST_SUCCESS;
                }
            } else if (config.getMultipleAudit() == 3) {
                //三审
                if (isFirstAudit || isSecondAudit || isThirdAudit) {
                    if (isFirstAudit) {
                        auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_VALID_ONE);
                        postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_FIRST_SUCCESS;
                    }
                    if (isSecondAudit) {
                        auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_VALID_TWO);
                        postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_SECOND_SUCCESS;
                    }
                    if (isThirdAudit) {
                        auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_VALID_THREE);
                        postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_THIRD_SUCCESS;
                    }
                } else {
                    auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_VALID_ONE);
                    postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_FIRST_SUCCESS;
                }
            }
            postResponseType = ResponseConstants.RESPONSE_TYPE_VALID;
            sampleStatus = SampleConstants.STATUS_AUDIT_SUCCESS;
        }
        //审核无效
        else if (ResponseConstants.AUDIT_RESULT_INVALID.equals(auditVO.getAuditResult())) {
            if (config.getMultipleAudit() == 1) {
                //一审即终审
                auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_INVALID);
            } else if (config.getMultipleAudit() == 2) {
                if (isFirstAudit || isSecondAudit) {
                    if (isFirstAudit) {
                        auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_INVALID_ONE);
                    }
                    if (isSecondAudit) {
                        auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_INVALID);
                    }
                }
            } else if (config.getMultipleAudit() == 3) {
                //三审
                if (isFirstAudit) {
                    auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_INVALID_ONE);
                }
                if (isSecondAudit) {
                    auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_INVALID_TWO);
                }
                if (isThirdAudit) {
                    auditVO.setAuditResult(ResponseConstants.AUDIT_RESULT_INVALID);
                }
            }
            postResponseType = ResponseConstants.RESPONSE_TYPE_INVALID;
            postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_FAIL;
            sampleStatus = SampleConstants.STATUS_AUDIT_INVALID;

            // 配额
            if (config.getIfOpenQuota() == ProjectConstants.OPEN) {
                if (response.getResponseStatus() != ResponseConstants.RESPONSE_STATUS_AUDIT_FAIL) {
                    List<BaseProjectQuota> quotaList = quotaDao.getQuotaList(project.getId(), QuotaConstants.ENABLE);
                    if (null != quotaList && !quotaList.isEmpty()) {
                        BaseProjectProperty projectProperty = sampleService.getProjectSampleProperty(project.getId());
                        HashSet<String> sampleColumns = sampleService.getSamplePropertySet(projectProperty);
                        QuestionnaireParseDTO parseDTO = SurvmlUtil.getQuestionsInfoByXml(projectQuestionnaire.getXmlContent());
                        for (BaseProjectQuota projectQuota : quotaList) {
                            String name = projectQuota.getName();
                            Integer type = projectQuota.getType();// 1:问卷配额 2:样本配额 3:问卷配额+样本配额
                            String sampleCondition = projectQuota.getSampleQuota();// 样本条件
                            String questionnaireCondition = projectQuota.getQuestionnaireQuota();// 问卷条件
                            Integer currentQuantity = projectQuota.getCurrentQuantity();// 满足配额的答卷数量

                            // 该答卷的满足配额集合
                            JSONArray jsonArray = JSONArray.parseArray(response.getQuotaIds());
                            if (null == jsonArray || jsonArray.isEmpty()) {
                                jsonArray = new JSONArray();
                            }

                            boolean matchSample = false;
                            if (StringUtils.isNotBlank(sampleCondition)) {
                                Quota quota = new Quota(name, sampleCondition, sampleColumns);
                                matchSample = quota.check(sampleMap);
                            }
                            boolean matchQuestionnaire = false;
                            if (StringUtils.isNotBlank(questionnaireCondition)) {
                                Quota quota = new Quota(name, questionnaireCondition, parseDTO.getSurvMLDocument());
                                matchQuestionnaire = quota.check(sampleMap);
                            }

                            // 配额满足的情况下执行
                            if ((type.equals(QuotaConstants.BOTH_QUOTA) && matchQuestionnaire && matchSample) ||
                                    (!type.equals(QuotaConstants.BOTH_QUOTA) && (matchQuestionnaire || matchSample))) {
                                if (null == currentQuantity || currentQuantity <= 1) {
                                    currentQuantity = 0;
                                } else {
                                    currentQuantity--;
                                }
                                if (jsonArray.contains(projectQuota.getId())) {
                                    jsonArray.remove(projectQuota.getId());
                                    response.setQuotaIds(jsonArray.toJSONString());
                                }
                                projectQuota.setCurrentQuantity(currentQuantity);
                                quotaDao.updateByPrimaryKeySelective(projectQuota);
                            }
                        }
                    }
                }
            }

            projectDao.updateByPrimaryKeySelective(project);
        }
        //审核打回
        else if ((ResponseConstants.AUDIT_RESULT_MODIFY.equals(auditVO.getAuditResult()))) {
            postResponseType = ResponseConstants.RESPONSE_TYPE_INVALID;
            postResponseStatus = ResponseConstants.RESPONSE_STATUS_AUDIT_BACK;
            sampleStatus = SampleConstants.STATUS_RUNNING;
            project.setNumOfAnswer(project.getNumOfAnswer() - 1);
            projectDao.updateByPrimaryKeySelective(project);
        }
        // 答卷审核表
        BaseResponseAudit responseAudit = new BaseResponseAudit();
        responseAudit.setDynamicTableName(ProjectConstants.getResponseAuditTableName(auditVO.getProjectId()));
        responseAudit.setResponseGuid(response.getResponseGuid());
        responseAudit.setAuditComments(auditVO.getAuditComments());
        responseAudit.setAuditQuestions(auditVO.getAuditQuestions());
        responseAudit.setAuditResult(auditVO.getAuditResult());
        responseAudit.setAuditTime(new Date());
        responseAudit.setAuditUser(ThreadLocalManager.getUserId());
        responseAudit.setAuditScore(auditVO.getAuditScore());
        responseAudit.setPreResponseType(response.getResponseType());
        responseAudit.setPreResponseStatus(response.getResponseStatus());
        responseAudit.setPostResponseStatus(postResponseStatus);
        responseAudit.setPostResponseType(postResponseType);
        responseAuditDao.insertSelective(responseAudit);

        // 更新样本表、多问卷不改样本状态
        if (config.getMultipleQuestionnaire() == ProjectConstants.CLOSE) {
            sample.setDynamicTableName(ProjectConstants.getSampleTableName(auditVO.getProjectId()));
            sample.setId(auditVO.getSampleId());
            sample.setStatus(sampleStatus);
            sample.setLastModifyTime(new Date());
            sample.setLastModifyUser(ThreadLocalManager.getUserId());
            sample.setSampleGuid(response.getSampleGuid());
            sampleDao.updateByPrimaryKeySelective(sample);

            // 样本状态变化记录
            BaseProjectSampleStatusRecord record = new BaseProjectSampleStatusRecord();
            record.setProjectId(auditVO.getProjectId());
            record.setSampleGuid(response.getSampleGuid());
            record.setStatus(sampleStatus);
            record.setCreateTime(new Date());
            statusRecordDao.insertSelective(record);
        }

        //更新审核记录
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setDynamicTableName(ProjectConstants.getResponseTableName(auditVO.getProjectId()));
        baseResponse.setId(auditVO.getResponseId());
        baseResponse.setAuditResult(auditVO.getAuditResult());
        baseResponse.setAuditScore(auditVO.getAuditScore());
        baseResponse.setAuditComments(auditVO.getAuditComments());
        baseResponse.setAuditTime(new Date());
        baseResponse.setAuditUser(ThreadLocalManager.getUserId());
        baseResponse.setResponseType(postResponseType);
        baseResponse.setResponseStatus(postResponseStatus);
        baseResponse.setQuotaIds(response.getQuotaIds());
        baseResponse.setLastModifyTime(new Date());
        return responseDao.updateByPrimaryKeySelective(baseResponse);

    }


    /**
     * 根据项目ID和问卷ID以及答卷ID获取相关问题
     *
     * @param projectId
     * @param questionnaireId
     * @param responseId
     * @return
     */
    public List<HashMap<String, Object>> getAnsweredQuestions(Integer projectId, Integer questionnaireId, Integer responseId, String responseGuid, Integer isHistory) {

        //基本参数
        Example example = new Example(BaseResponse.class);
        example.setTableName(ProjectConstants.getResponseTableName(projectId));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", responseId);
        BaseResponse response = responseDao.selectOneByExample(example);

        if (isHistory == 2) {
            // response = getHistoryResponseById(Integer.valueOf(projectId), Integer.valueOf(responseId));
        }

        BaseProjectQuestionnaire questionnaire = pQuestionnaireDao.selectByPrimaryKey(questionnaireId);
        Integer repeatPage = 0;
//        if (questionnaire.getRepeatPage() == null || questionnaire.getIsHasRepeat() == null || questionnaire.getIsHasRepeat() == 0) {
//            repeatPage = -1;
//        } else {
//            repeatPage = questionnaire.getRepeatPage();
//        }
        Integer maxRepeatCount = 0;

        // 问卷内容解析
        SurvmlParse survmlParse = new SurvmlParse();
        SurvmlModel survmlModel = new SurvmlModel();
        SurvMLDocument survMLDocument = null;
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(questionnaire.getXmlContent().getBytes("utf-8"));
            survmlModel = survmlParse.parseWithInputStream(inputStream);
            survMLDocument = new SurvMLDocumentParser(questionnaire.getXmlContent()).parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<SurvmlModel.DVariable> variableList = survmlModel.variables;
        List<SurvmlModel.DQuestion> questionList = survmlModel.questions;
        List<Question> questions = survMLDocument.getQuestions();
        Map<String, Question> questionsMap = new HashMap<>();
        for (Question q : questions) {
            questionsMap.put(q.getId(), q);
        }

        JSONObject varDictObject = new JSONObject();
        if (response.getSubmitData() != null) {
            // json 转换
            // JSONObject submitObject = new JSONObject(response.getSubmitData());
            JSONObject submitObject = JSONObject.parseObject(response.getSubmitData());
            varDictObject = submitObject.getJSONObject("var_dict");
        }

        // 追加批注
        JSONObject noteJsonObject = null;
        if (response.getAuditNotes() != null) {
            noteJsonObject = JSONObject.parseObject(response.getAuditNotes());
        }


        // qidAndValueMap存放的是问卷的qid和对应的答卷值value的Map
        HashMap<String, String> qidAndValueMap = new HashMap<>();
        //responseData迭代 json转换
        //JSONObject responseDataObject = new JSONObject(response.getResponseData());
        JSONObject responseDataObject = JSONObject.parseObject(response.getResponseData());
        if (responseDataObject == null) {
            return null;
        }
        Iterator iterator = responseDataObject.keySet().iterator();
        while (iterator.hasNext()) {
            String qid = (String) iterator.next();
            String value = responseDataObject.get(qid).toString();
            qidAndValueMap.put(qid, value);
        }

        // 结果
        LinkedList<HashMap<String, Object>> questionsList = new LinkedList<>();
        for (SurvmlModel.DQuestion tempQuestion : questionList) {
            if (20 == tempQuestion.type) {
                if (tempQuestion.index == repeatPage) {
                    for (int j = 0; j <= maxRepeatCount; j++) {
                        List<Object> elementList = tempQuestion.elements;
                        for (Object object : elementList) {
                            SurvmlModel.DQuestion dQuestion = (SurvmlModel.DQuestion) object;
                            dQuestion.setRepeatCurrent(j);
                            getAnsweredQuestion(questionsList, questionsMap, dQuestion, qidAndValueMap,
                                    varDictObject, noteJsonObject, questionnaire);
                        }
                    }
                } else {
                    List<Object> elementList = tempQuestion.elements;
                    for (Object object : elementList) {
                        SurvmlModel.DQuestion dQuestion = (SurvmlModel.DQuestion) object;
                        getAnsweredQuestion(questionsList, questionsMap, dQuestion, qidAndValueMap,
                                varDictObject, noteJsonObject, questionnaire);
                    }
                }
            } else {
                getAnsweredQuestion(questionsList, questionsMap, tempQuestion, qidAndValueMap,
                        varDictObject, noteJsonObject, questionnaire);
            }
        }

        return questionsList;
    }

    /**
     * 获取问题答案
     *
     * @param questionsList
     * @param questionsMap
     * @param question
     * @param qidAndValueMap
     * @param varDictObject
     * @param questionnaire
     */
    public void getAnsweredQuestion(LinkedList<HashMap<String, Object>> questionsList, Map<String, Question> questionsMap,
                                    SurvmlModel.DQuestion question, Map<String, String> qidAndValueMap, JSONObject varDictObject,
                                    JSONObject noteJsonObject, BaseProjectQuestionnaire questionnaire) {
        HashMap<String, Object> questionMap = new HashMap<>();
        //itemList(通用题型)
        List<HashMap<String, Object>> itemList = new ArrayList<>();
        //sortedItemList(排序题)
        LinkedList<HashMap<String, Object>> sortedItemList = new LinkedList<>();
        // id
        String qid = question.getRepeatId();
        String value = qidAndValueMap.get(qid);
        questionMap.put("id", qid);
        List<SurvmlModel.DText> textList = question.texts;
        // 标题
        String title = "";
        for (SurvmlModel.DText dText : textList) {
            title = title + dText.text + " ";
        }
        questionMap.put("title", title.trim());

        // 问题类型
        questionMap.put("questionType", questionsMap.get(question.getAttr("id")).getQuestionType());

        // 如果有备注，加上备注
        if (StringUtils.isNotBlank(questionsMap.get(question.getAttr("id")).getComments())) {
            questionMap.put("remark", questionsMap.get(question.getAttr("id")).getComments());
        }

        // 如果有批注，加上批注
        if (noteJsonObject != null && noteJsonObject.containsKey(qid)) {
            questionMap.put("notes", noteJsonObject.getJSONObject(qid).getString("c"));
        }

        if (StringUtils.isBlank(value)) {
            questionsList.add(questionMap);
            return;
        }
        List<Object> objectList = question.elements;
        JSONObject optionValueObject = null;
        // 级联题
        if (question.type == 23) {
            String cascadeStr = new String();
            String data = question.getAttr("data");
            //JSONArray optionArray = new JSONArray(data);
            JSONArray optionArray = JSONObject.parseArray(data);
            for (int j = 0; j < optionArray.size(); j++) {
                JSONObject jsonObject = optionArray.getJSONObject(j);
                cascadeStr = cascadeStr + jsonObject.getString("title") + ":";
                String optionCode = jsonObject.getString("options");
                String optionId = jsonObject.getString("id");
                // json
                String id = JSONObject.parseObject(value).getString(qid + "." + optionId);
                JSONArray array = varDictObject.getJSONArray(optionCode);
                for (int l = 0; l < array.size(); l++) {
                    if (array.getJSONObject(l).get("id").toString().equals(id)) {
                        String tempTitle = array.getJSONObject(l).getString("title");
                        cascadeStr = cascadeStr + tempTitle + " ";
                        if (l != array.size() - 1) {
                            cascadeStr = cascadeStr + "<br><br>";
                        }
                    }
                }
            }
            questionMap.put("value", cascadeStr);
        }
        // 标签题、整数题、浮点数题、文本题、多行文本题、日期题、赋值题、打分题、打星题、定位题
        if (question.type == 1 || question.type == 4 || question.type == 5 || question.type == 6 || question.type == 7
                || question.type == 8 || question.type == 10 || question.type == 14 || question.type == 15) {
            questionMap.put("haveInput", true);
            questionMap.put("value", value);
        }
        // 单选题、多选题、下拉单选题、下拉搜索单选题
        if (question.type == 2 || question.type == 3 || question.type == 18 || question.type == 19 || question.type == 9) {
            for (int i = 0; i < objectList.size(); i++) {
                HashMap<String, Object> item = new HashMap<>();
                SurvmlModel.DOption dOption = (SurvmlModel.DOption) objectList.get(i);
                item.put("id", dOption.id);
                String text = "";
                for (SurvmlModel.DText t : dOption.texts) {
                    text += t.text + " ";
                }
                item.put("name", text.trim());
                // json 转换
                // optionValueObject = new JSONObject(value);
                optionValueObject = JSONObject.parseObject(value);
                if (null != optionValueObject) {
                    if (optionValueObject.containsKey(qid + "." + dOption.id)) {
                        item.put("value", optionValueObject.get(qid + "." + dOption.id).toString());
                    }
                    //有追加输入的值
                    if (optionValueObject.containsKey(qid + "." + dOption.id + ".input")) {
                        item.put("haveInput", true);
                        item.put("inputValue", optionValueObject.get(qid + "." + dOption.id + ".input").toString());
                    }
                    //选择题是否选中(对输入题目没有影响)
                    if (optionValueObject.containsKey(qid + "." + dOption.id)) {
                        if ("1".equals(optionValueObject.get(qid + "." + dOption.id).toString())) {
                            item.put("selected", true);
                        } else {
                            item.put("selected", false);
                        }
                    } else {
                        item.put("selected", false);
                    }
                }
                itemList.add(item);
            }
        }
        // 排序题
        if (question.type == 11) {
            // json转换问题
            // optionValueObject = new JSONObject(value);
            optionValueObject = JSONObject.parseObject(value);
            for (int j = 0; j < objectList.size(); j++) {
                HashMap<String, Object> item = new HashMap<>();
                for (int k = 0; k < objectList.size(); k++) {
                    SurvmlModel.DOption tempOption = (SurvmlModel.DOption) objectList.get(k);
                    if (optionValueObject.containsKey(qid + "." + (k + 1))) {
                        int flag = Integer.parseInt(optionValueObject.get(qid + "." + (k + 1)).toString());
                        if (flag == j) {
                            item.put("id", j + 1);
                            String tempText = "";
                            for (SurvmlModel.DText t : tempOption.texts) {
                                tempText += t.text + " ";
                            }
                            item.put("name", tempText.trim());
                            sortedItemList.add(item);
                        }
                    }
                }
                itemList.add(item);
            }
        }
        // 拍照题、录音题、上传题
        if (question.type == 13 || question.type == 22 || question.type == 24) {
            // 拍照题
            if (question.type == 13) {
                List<String> list = new ArrayList();
                String path = "/project-file/" + questionnaire.getProjectId() + "/attach/";
                com.alibaba.fastjson.JSONArray arr = com.alibaba.fastjson.JSONArray.parseArray(value);
                for (Object object : arr) {
                    String imageValue = path + object.toString();
                    list.add(imageValue);
                }
                questionMap.put("value", list);
            }
            // 录音题
            if (question.type == 22) {
                String path = "/project-file/" + questionnaire.getProjectId() + "/voice/";
                com.alibaba.fastjson.JSONArray arr = com.alibaba.fastjson.JSONArray.parseArray(value);
                com.alibaba.fastjson.JSONObject obj = arr.getJSONObject(0);
                // TODO 图片位置
                String audioValue = path + obj.getString("file");
                questionMap.put("value", audioValue);
            }
            // 上传题
            if (question.type == 24) {
                // [{"filename":"6f64f6c2-052c-4289-8c0e-8925b7f3e6fe.jpeg","userFilename":"20170625143749_mtSZE.jpeg"}]
                String[] suffixs = {"doc", "docx", "txt", "ppt", "pptx"};
                List<String> fileSuffix = Arrays.asList(suffixs);
                String uploadedPath = "/project-file/" + questionnaire.getProjectId() + "/attach/";
                List<HashMap<String, Object>> fileList = new ArrayList<>();
                if (!value.equals("{}")) {
                    // [{"filename":"6f64f6c2-052c-4289-8c0e-8925b7f3e6fe.jpeg","userFilename":"20170625143749_mtSZE.jpeg"}]
                    com.alibaba.fastjson.JSONArray arr = com.alibaba.fastjson.JSONArray.parseArray(value);
                    for (int i = 0; i < arr.size(); i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        com.alibaba.fastjson.JSONObject obj = arr.getJSONObject(i);
                        String filename = obj.getString("filename");
                        String fileValue = obj.getString("userFilename");
                        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
                        map.put("value", uploadedPath + filename);
                        map.put("fileValue", fileValue);
                        // D:\\ringsurvey\\uploaded\\survey-file\\previewfile
                        String fileName = uploadedPath + "/" + filename;
                        File file = new File(fileName);
//                        if (FileUtil.isImage(file) || fileSuffix.contains(suffix)) {
//                            if (this.filePreview(file)) {
//                                map.put("canPreview", true);
//                            }
//                        }
                        fileList.add(map);
                    }
                    questionMap.put("fileList", fileList);
                }
            }
        }
        // 行政区题
        if (question.type == 17) {
            // json转换问题
            //JSONObject provCityObject = new JSONObject(value);
            JSONObject provCityObject = JSONObject.parseObject(value);
            StringBuffer provCityStr = new StringBuffer();
            provCityStr.append(provCityObject.containsKey(qid + ".prov") ? provCityObject.get(qid + ".prov").toString() + " " : "");
            provCityStr.append(provCityObject.containsKey(qid + ".city") ? provCityObject.get(qid + ".city").toString() + " " : "");
            provCityStr.append(provCityObject.containsKey(qid + ".dist") ? provCityObject.get(qid + ".dist").toString() + " " : "");
            provCityStr.append(provCityObject.containsKey(qid + ".town") ? provCityObject.get(qid + ".town").toString() + " " : "");
            provCityStr.append(provCityObject.containsKey(qid + ".village") ? provCityObject.get(qid + ".village").toString() + " " : "");
            questionMap.put("haveInput", true);
            questionMap.put("value", provCityStr);
        }
        // 表格题
        if (question.type == 12 || question.type == 16) {
            List<Map<String, Object>> answeredQuestions = new ArrayList<>();
            MatrixQuestion matrixQuestion = (MatrixQuestion) questionsMap.get(question.getAttr("id")).clone();
            matrixQuestion.setValueFromJsonStr(value);
            questionMap.put("valueMap", matrixQuestion.getStoredValueMap());
            //key: questionId.rowId.colId或questionId.rowId.colId.listItemId
            // 取行数
            List<MatrixQuestion.MatrixRow> rowQuestions = matrixQuestion.getRows();
            List<MatrixQuestion.MatrixCol> colQuestions = matrixQuestion.getCols();
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> cols = new ArrayList<>();
            for (int i = 0; i < colQuestions.size(); i++) {
                Map<String, Object> res = new HashMap<>();
                Question colQuestion = colQuestions.get(i).getQuestion();
                res.put("id", colQuestions.get(i).getId());
                res.put("title", colQuestion.getTitle());
                res.put("questionType", colQuestion.getQuestionType());
                if (QuestionType.SingleSelect.equals(colQuestion.getQuestionType())
                        || QuestionType.Dropdown.equals(colQuestion.getQuestionType())
                        || QuestionType.MultiSelect.equals(colQuestion.getQuestionType())) {
                    ListQuestion listQuestion = (ListQuestion) colQuestion;
                    List<ListItem> listItems = listQuestion.getListItems();
                    List<Map<String, Object>> listItemsMap = new ArrayList<>();
                    for (int j = 0, jLength = listItems.size(); j < jLength; j++) {
                        Map<String, Object> listMap = new HashMap<>();
                        listMap.put("id", listItems.get(j).getId());
                        listMap.put("name", listItems.get(j).getName());
                        listMap.put("value", listItems.get(j).getValue());
                        listItemsMap.add(listMap);
                    }
                    res.put("listItems", listItemsMap);
                }
                cols.add(res);
            }
            result.put("cols", cols);
            List<Map<String, Object>> rows = new ArrayList<>();
            for (int i = 0; i < rowQuestions.size(); i++) {
                Map<String, Object> res = new HashMap<>();
                res.put("id", rowQuestions.get(i).getId());
                res.put("name", rowQuestions.get(i).getName());
                rows.add(res);
            }
            result.put("rows", rows);
            answeredQuestions.add(result);
            questionMap.put("answeredQuestions", answeredQuestions);
        }
        questionMap.put("listItems", itemList);
        questionMap.put("sortedItemList", sortedItemList);
        questionsList.add(questionMap);
    }

    /**
     * 批量审核
     *
     * @param batchAuditVO
     * @return
     */
    public Integer insertBatchResponseAudit(BatchAuditVO batchAuditVO) {
        Integer countSuccess = 0;
        for (Integer responseId : batchAuditVO.getResponseId()) {
            // 限制批量审核时的答卷状态
            BaseResponse response = this.getResponse(batchAuditVO.getProjectId(), responseId, null);
            if (!(response.getResponseStatus().equals(ResponseConstants.RESPONSE_STATUS_SUCCESS)
                    || response.getResponseStatus().equals(ResponseConstants.RESPONSE_STATUS_AUDIT_FIRST_SUCCESS)
                    || response.getResponseStatus().equals(ResponseConstants.RESPONSE_STATUS_AUDIT_SECOND_SUCCESS)
                    || response.getResponseStatus().equals(ResponseConstants.RESPONSE_STATUS_AUDIT_THIRD_SUCCESS)
                    || response.getResponseStatus().equals(ResponseConstants.RESPONSE_STATUS_AUDIT_FAIL)
                    || response.getResponseStatus().equals(ResponseConstants.RESPONSE_STATUS_AUDIT_BACK))) {
                continue;
            }
            ResponseAuditVO auditVO = new ResponseAuditVO();
            auditVO.setProjectId(batchAuditVO.getProjectId());
            auditVO.setAuditResult(batchAuditVO.getAuditResult());
            auditVO.setAuditComments(batchAuditVO.getAuditComment());
            auditVO.setResponseId(responseId);
            auditVO.setAuditScore("");
            saveResponseAudit(auditVO);
            countSuccess++;
        }
        return countSuccess;
    }

    /**
     * 获取答卷下载历史
     *
     * @param pageParam
     * @return
     */
    public PageList<Page> getResponseDownLoadList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        HistoryListVO listVO = new HistoryListVO();
        listVO.setProjectId(pageParam.getProjectId());
        listVO.setType("RESPONSE");
        List<ExportHistoryDTO> historyList = exportDao.getExportHistory(listVO);
        for (ExportHistoryDTO historyDTO : historyList) {
            historyDTO.setFileSizeStr(FileUtil.byteFormat(historyDTO.getFileSize(), true));
        }
        return new PageList<>(page);
    }

    /**
     * 答卷导出
     *
     * @param exportVO
     * @return
     */
    public int exportResponse(ResponseExportVO exportVO) throws Exception {
        int projectId = exportVO.getProjectId();

        // 项目所有问卷
        List<ProjectModuleQuestionListDTO> moduleQuestionList = moduleQuestionnaireDao.getProjectModuleQuestionList(projectId);
        List<ResponseExportDTO> dataList = new ArrayList<>();

        ResponseExportSearchVO searchVO = new ResponseExportSearchVO();
        for (int i = 0; i < moduleQuestionList.size(); i++) {
            ProjectModuleQuestionListDTO dto = moduleQuestionList.get(i);
            int questionnaireId = dto.getQuestionnaireId();

            ResponseExportDTO tempDTO = new ResponseExportDTO();
            tempDTO.setModuleId(dto.getId());
            tempDTO.setModuleName(dto.getName());
            tempDTO.setVersion(dto.getVersion());
            tempDTO.setQuestionnaireId(questionnaireId);
            tempDTO.setXmlContent(dto.getXmlContent());

            List<ResponseExportSelectDTO> responses = null;
            if ("ALL".equals(exportVO.getOpt())) {
                BeanUtils.copyProperties(exportVO, searchVO);
                searchVO.setProjectId(projectId);
                searchVO.setQuestionnaireId(questionnaireId);
                responses = responseDao.getExportResponseList(searchVO);
            } else if ("SEARCH".equals(exportVO.getOpt())) {
                if (exportVO.getSearchVO().getQuestionnaireId() != null) {
                    if (questionnaireId == exportVO.getSearchVO().getQuestionnaireId()) {
                        BeanUtils.copyProperties(exportVO.getSearchVO(), searchVO);
                        searchVO.setProjectId(projectId);
                        searchVO.setQuestionnaireId(questionnaireId);
                        responses = responseDao.getExportResponseList(searchVO);
                    } else {
                        continue;
                    }
                } else {
                    BeanUtils.copyProperties(exportVO.getSearchVO(), searchVO);
                    searchVO.setProjectId(projectId);
                    searchVO.setQuestionnaireId(questionnaireId);
                    responses = responseDao.getExportResponseList(searchVO);
                }
            } else {
                responses = responseDao.getExportResponseListByIds(projectId, questionnaireId, exportVO.getResponseIds());
            }

            tempDTO.setExportResponseList(responses);
            dataList.add(tempDTO);
        }


        // 处理数据
        ExportChoiceVO exportChoiceVO = new ExportChoiceVO();
        BeanUtils.copyProperties(exportVO, exportChoiceVO);
        Map<Integer, List<LinkedHashMap<String, String>>> exportResponseList = this.handleExportData(dataList, exportVO.getProperties(), exportChoiceVO);

        // 文件路径
        String pre = "/export/" + exportVO.getProjectId() + "/response/";
        String path = filePath + pre;

        // 文件名称
        String resultFileName = exportVO.getFileName();
        if (StringUtils.isBlank(resultFileName)) {
            resultFileName = UUIDUtil.getTimestampUUID();
        } else {
            resultFileName += "_" + UUIDUtil.getTimestampUUID();
        }

        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            ResponseExportDTO tempDTO = dataList.get(i);
            List<LinkedHashMap<String, String>> dataMapList = exportResponseList.get(tempDTO.getQuestionnaireId());
            String fileName = tempDTO.getModuleName() + "(版本" + tempDTO.getVersion() + ")";

            Map<String, Object> res = new HashMap<>();
            if (ResponseConstants.EXPORT_TYPE_EXCEL.equals(exportVO.getFileType())) {
                fileName += ".xlsx";
                res = ExcelUtil.createResponseExcelFile(tempDTO.getTitleMap(), tempDTO.getTitleTypeMap(), dataMapList, path, fileName);
            } else if (ResponseConstants.EXPORT_TYPE_CSV.equals(exportVO.getFileType())) {
                fileName += ".csv";
                res = CsvUtil.createResponseCsvFile(tempDTO.getTitleMap(), dataMapList, path, fileName);
            } else if (ResponseConstants.EXPORT_TYPE_SPSS.equals(exportVO.getFileType())) {
                fileName += ".sav";
                res = SpssUtil.createResponseSpssFile(tempDTO.getTitleMap(), dataMapList, tempDTO.getOptionMap(), exportVO.getOptionType(), path, fileName);
            } else if (ResponseConstants.EXPORT_TYPE_DBF.equals(exportVO.getFileType())) {
                fileName += ".dbf";
                res = DbfUtil.createResponseDbfFile(tempDTO.getTitleMap(), tempDTO.getTitleTypeMap(), dataMapList, path, fileName);
            }

            fileList.add(new File(path + res.get("fileName").toString()));
        }

        String zipName = resultFileName + ".zip";
        String zipPath = path + zipName;
        try {
            FileOutputStream fos1 = new FileOutputStream(new File(zipPath));
            ZipUtil.toZip(fileList, fos1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long size = new File(zipPath).length();

        Date now = new Date();
        BaseProjectExportHistory exportHistory = new BaseProjectExportHistory();
        exportHistory.setName(zipName);
        exportHistory.setFileSize(size);
        exportHistory.setFilePath(pre + zipName);
        exportHistory.setType("RESPONSE");
        exportHistory.setFileType(exportVO.getFileType());
        exportHistory.setProjectId(projectId);
        exportHistory.setDescription(exportVO.getDescription());
        exportHistory.setCreateUser(ThreadLocalManager.getUserId());
        exportHistory.setCreateTime(now);
        int row = exportDao.insertSelective(exportHistory);
        if (row > 0) {
            // 回写信息量总数
            BaseProject p = new BaseProject();
            p.setFileSize(size);
            p.setLastModifyUser(ThreadLocalManager.getUserId());
            p.setLastModifyTime(now);
            p.setId(projectId);
            projectDao.updateProjectAdd(p);
        }

        // 删除文件
        for (int i = 0; i < fileList.size(); i++) {
            File tempFile = fileList.get(i);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }

        return row;
    }

    /**
     * 导出数据处理
     *
     * @param dataList
     * @param properties
     * @param exportChoiceVO
     */
    private Map<Integer, List<LinkedHashMap<String, String>>> handleExportData(List<ResponseExportDTO> dataList, List<String> properties, ExportChoiceVO exportChoiceVO) {
        Map<Integer, List<LinkedHashMap<String, String>>> res = new HashMap<>();

        for (ResponseExportDTO dto : dataList) {
            // 标题
            LinkedHashMap<String, String> titleMap = new LinkedHashMap<>();
            // 标题类型
            Map<String, String> titleTypeMap = new HashMap<>();
            for (String property : properties) {
                titleMap.put(property, CommonProperty.responseHeadMap.get(property));
                titleTypeMap.put(property, "CELL_TYPE_STRING");

                // 批注
                if (property.equals("auditNote")) {
                    exportChoiceVO.setNeedNotes(1);
                }
            }
            // 选项
            Map<String, List<String>> optionMap = new HashMap<>();

            // 问卷内容解析
            QuestionnaireParseDTO parseDTO = SurvmlUtil.getQuestionsInfoByXml(dto.getXmlContent());
            List<DVariable> variableList = parseDTO.getDVariableList();
            List<DQuestion> questionList = parseDTO.getDQuestionList();

            // 答卷
            List<ResponseExportSelectDTO> responses = dto.getExportResponseList();
            //返回所有的数据
            List<LinkedHashMap<String, String>> exportResponseList = new ArrayList<>();
            for (int i = 0; i < responses.size(); i++) {

                LinkedHashMap<String, String> exportHashMap = new LinkedHashMap<>(5000);
                ResponseExportSelectDTO responseDTO = responses.get(i);

                if (!responseDTO.getQuestionnaireId().equals(dto.getQuestionnaireId())) continue;

                // 填充画面所选属性
                for (String property : properties) {
                    Object obj = ReflectUtil.getGetMethod(responseDTO, property);
                    if (obj != null && StringUtils.isNotBlank(obj.toString())) {
                        exportHashMap.put(property, obj.toString());
                    }
                }

                String submitData = responseDTO.getSubmitData();
                // 答卷变量
                if (StringUtils.isNotBlank(submitData)) {
                    if (exportChoiceVO.getQnaireVariable() == 1) {
                        LinkedHashMap<String, Object> submitDataMap = JSON.parseObject(submitData, LinkedHashMap.class, Feature.OrderedField);
                        JSONObject submitDataObj = new JSONObject(true);
                        submitDataObj.putAll(submitDataMap);
                        JSONObject varObj = new JSONObject(true);
                        if (submitDataObj.containsKey("var_dict")) {
                            //转json 顺序固定
                            LinkedHashMap<String, Object> jsonMap = JSON.parseObject(submitDataObj.get("var_dict").toString(), LinkedHashMap.class, Feature.OrderedField);
                            varObj.putAll(jsonMap);
                        }

                        Set keys = varObj.keySet();
                        Object[] array = keys.toArray();
                        for (int j = 0; j < array.length; j++) {
                            titleMap.put(array[j].toString(), array[j].toString());
                            titleTypeMap.put(array[j].toString(), "CELL_TYPE_STRING");
                            exportHashMap.put(array[j].toString(), varObj.get(array[j].toString()).toString());
                        }
                    }
                }

                // 答案
                String responseData = responseDTO.getResponseData();
                // 审核批注
                String notesData = responseDTO.getAuditNote();
                if (StringUtils.isNotBlank(responseData)) {
                    JSONObject responseObject = JSON.parseObject(responseData, Feature.OrderedField);
                    //
                    for (int k = 0;k< questionList.size();k++) {
                        DQuestion question = questionList.get(k);
                        //TODO 林业大学定制修改,第三页是循环题
                        if (dto.getModuleId() == 374) {
                            if (k == 2){
                                // 循环7次
                                for (int p = 1; p <= 7; p++) {
                                    if (question.type == 20) {
                                        List<Object> elementList = question.elements;
                                        for (int j = 0; j < elementList.size(); j++) {
                                            DQuestion dQuestion = (DQuestion) elementList.get(j);
                                            dQuestion.setRepeatCurrent(p);
                                            exportHashMap = this.getExportMapForQuestion(exportHashMap, titleMap, titleTypeMap, optionMap, dQuestion, variableList, responseObject, notesData,
                                                    exportChoiceVO.getOptionType(), exportChoiceVO.getTitleType(), exportChoiceVO.getMultiSelectType(), exportChoiceVO.getNeedNotes(),
                                                    null, false, false);
                                        }
                                    } else {
                                        exportHashMap = this.getExportMapForQuestion(exportHashMap, titleMap, titleTypeMap, optionMap, question, variableList, responseObject, notesData,
                                                exportChoiceVO.getOptionType(), exportChoiceVO.getTitleType(), exportChoiceVO.getMultiSelectType(), exportChoiceVO.getNeedNotes(),
                                                null, false, false);
                                    }
                                }
                            }else {
                                if (question.type == 20) {
                                    List<Object> elementList = question.elements;
                                    for (int j = 0; j < elementList.size(); j++) {
                                        DQuestion dQuestion = (DQuestion) elementList.get(j);
                                        exportHashMap = this.getExportMapForQuestion(exportHashMap, titleMap, titleTypeMap, optionMap, dQuestion, variableList, responseObject, notesData,
                                                exportChoiceVO.getOptionType(), exportChoiceVO.getTitleType(), exportChoiceVO.getMultiSelectType(), exportChoiceVO.getNeedNotes(),
                                                null, false, false);
                                    }
                                } else {
                                    exportHashMap = this.getExportMapForQuestion(exportHashMap, titleMap, titleTypeMap, optionMap, question, variableList, responseObject, notesData,
                                            exportChoiceVO.getOptionType(), exportChoiceVO.getTitleType(), exportChoiceVO.getMultiSelectType(), exportChoiceVO.getNeedNotes(),
                                            null, false, false);
                                }
                            }
                        } else {
                            if (question.type == 20) {
                                List<Object> elementList = question.elements;
                                for (int j = 0; j < elementList.size(); j++) {
                                    DQuestion dQuestion = (DQuestion) elementList.get(j);
                                    exportHashMap = this.getExportMapForQuestion(exportHashMap, titleMap, titleTypeMap, optionMap, dQuestion, variableList, responseObject, notesData,
                                            exportChoiceVO.getOptionType(), exportChoiceVO.getTitleType(), exportChoiceVO.getMultiSelectType(), exportChoiceVO.getNeedNotes(),
                                            null, false, false);
                                }
                            } else {
                                exportHashMap = this.getExportMapForQuestion(exportHashMap, titleMap, titleTypeMap, optionMap, question, variableList, responseObject, notesData,
                                        exportChoiceVO.getOptionType(), exportChoiceVO.getTitleType(), exportChoiceVO.getMultiSelectType(), exportChoiceVO.getNeedNotes(),
                                        null, false, false);
                            }
                        }
                    }
                    exportResponseList.add(exportHashMap);
                }
            }

            // 将标题等set回去
            dto.setTitleMap(titleMap);
            dto.setTitleTypeMap(titleTypeMap);
            dto.setOptionMap(optionMap);
            res.put(dto.getQuestionnaireId(), exportResponseList);
        }
        return res;
    }

    /**
     *
     */
    private LinkedHashMap<String, String> getExportMapForQuestion(LinkedHashMap<String, String> exportHashMap,
                                                                  LinkedHashMap<String, String> titleMap, Map<String, String> titleTypeMap,
                                                                  Map<String, List<String>> optionMap, DQuestion question,
                                                                  List<DVariable> variableList, JSONObject responseObject, String notesData,
                                                                  int optionType, int titleType, int multiSelectType, int needNotes,
                                                                  String imgUrlPrefix, boolean photo, boolean recording) {
        if (null == responseObject) {
            return exportHashMap;
        }

        String titleName = "";
        List<DText> textList = question.texts;
        // 标题
        String title = "";
        for (DText dText : textList) {
            title = title + dText.text + " ";
        }

        // 标签题、整数题、浮点数题、文本题、多行文本题、日期题、打分题、打星题
        if (question.type == SurvmlModel.QTYPE_LABEL || question.type == 4 || question.type == 5 || question.type == 6
                || question.type == 7 || question.type == 8 || question.type == 10 || question.type == 15) {
            if (titleType == ResponseConstants.EXPORT_CONTENT) {
                titleName = question.getRepeatId() + "." + title;
            } else {
                titleName = question.getRepeatId();
            }
            titleMap.put(question.getRepeatId(), titleName);

            if (question.type == 4 || question.type == 5) {
                titleTypeMap.put(question.getRepeatId(), "CELL_TYPE_NUMERIC");
            } else {
                titleTypeMap.put(question.getRepeatId(), "CELL_TYPE_STRING");
            }

            // 值
            String value = "";
            if (responseObject != null && responseObject.get(question.getRepeatId()) != null) {
                value = responseObject.get(question.getRepeatId()).toString();
            }
            exportHashMap.put(question.getRepeatId(), value);
        }

        // 单选题、下拉单选题、下拉搜索单选题
        else if (question.type == 2 || question.type == 18 || question.type == 19) {
            // 选项
            String value = "";
            String option = "";
            boolean inputFlag = false;
            String input = "";
            List<Object> objectList = question.elements;
            List<String> optionList = new ArrayList<>();
            for (int i = 0; i < objectList.size(); i++) {
                DOption dOption = (DOption) objectList.get(i);
                // 标题
                List<DText> optionTextList = dOption.texts;
                for (DText dText : optionTextList) {
                    option = option + dOption.id + "." + dText.text + " ";
                    String optionJson = dOption.id + "=" + dText.text;
                    optionList.add(optionJson);
                }
                if (dOption.input == 1 || dOption.input == 2) {
                    inputFlag = true;
                }

                // 值
                if (optionType == ResponseConstants.EXPORT_ID) {
                    // 选项编号
                    if (responseObject != null && responseObject.getJSONObject(question.getRepeatId()) instanceof JSONObject &&
                            responseObject.getJSONObject(question.getRepeatId()) != null &&
                            responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id) != null) {
                        value = dOption.id + "";
                    }
                    if (dOption.input == 1 || dOption.input == 2) {
                        inputFlag = true;
                        if (responseObject.getJSONObject(question.getRepeatId()) != null
                                && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input") != null) {
                            input = responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input");
                        }
                    }
                } else {
                    // 选项内容
                    if (responseObject != null && responseObject.getJSONObject(question.getRepeatId()) != null &&
                            responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id) != null) {
                        value = dOption.id + ".";
                        //标题
                        List<DText> checkedOptionTextList = dOption.texts;
                        for (DText text : checkedOptionTextList) {
                            value = value + text.text + " ";
                        }
                        if (dOption.input == 1 || dOption.input == 2) {
                            inputFlag = true;
                            if (responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input") != null) {
                                input = responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input");
                            }
                        }
                    }
                }
            }

            if (titleType == ResponseConstants.EXPORT_CONTENT) {
                titleName = question.getRepeatId() + "." + title + " " + option;
            } else {
                titleName = question.getRepeatId();
            }

            titleMap.put(question.getRepeatId(), titleName);
            titleTypeMap.put(question.getRepeatId(), "CELL_TYPE_STRING");
            exportHashMap.put(question.getRepeatId(), value);
            optionMap.put(question.getRepeatId(), optionList);

            // 追加输入框的值
            if (inputFlag) {
                titleMap.put(question.getRepeatId() + "input", titleName + " " + "追加文本输入:");
                titleTypeMap.put(question.getRepeatId() + "input", "CELL_TYPE_STRING");
                exportHashMap.put(question.getRepeatId() + "input", input);
            }
        }

        // 多选题
        else if (question.type == 3) {
            List<Object> objectList = question.elements;// 问题选项

            List<String> optionJsonList = new ArrayList<>();
            List<String> titleNameList = new ArrayList<>();// 逐列导出时的标题列表
            List<String> valueList = new ArrayList<>();// 逐列导出时的值列表
            String v = "";// 单列导出时的选项值
            List<String> singleTitleList = new ArrayList<>();// 单列导出的追加标题
            List<String> singleValueList = new ArrayList();// 单列导出的追加值
            MultiplySelectSingleExportDTO singleExportDTO = new MultiplySelectSingleExportDTO();
            singleExportDTO.setQuestionId(question.getRepeatId());
            singleExportDTO.setTitle(title);

            for (int i = 0; i < objectList.size(); i++) {
                DOption dOption = (DOption) objectList.get(i);
                String value = "";
                String option = "";
                // 选项标题
                List<DText> optionTextList = dOption.texts;
                for (DText dText : optionTextList) {
                    option = option + dOption.id + "." + dText.text + " ";
                    optionJsonList.add(dOption.id + "=" + dText.text);
                }

                // 逐列导出
                if (multiSelectType == ResponseConstants.EXPORT_MULTIPLE) {
                    if (titleType == ResponseConstants.EXPORT_ID) {// 标题编号
                        titleNameList.add(question.getRepeatId() + " " + dOption.id);

                        if (optionType == ResponseConstants.EXPORT_ID) {// 选项编号
                            if (responseObject.getJSONObject(question.getRepeatId()) != null
                                    && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id) != null
                                    && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id).equals("1")) {
                                valueList.add("1");

                                if (dOption.input == 1 || dOption.input == 2) {
                                    titleNameList.add(question.getRepeatId() + " " + dOption.id + " 追加文本输入");
                                    if (responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input") != null) {
                                        value = responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input");
                                    }
                                    valueList.add(value);
                                }
                            } else {
                                valueList.add("0");

                                if (dOption.input == 1 || dOption.input == 2) {
                                    titleNameList.add(question.getRepeatId() + " " + dOption.id + " 追加文本输入");
                                    valueList.add("");
                                }
                            }
                        } else {// 选项内容
                            if (responseObject.getJSONObject(question.getRepeatId()) != null
                                    && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id) != null
                                    && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id).equals("1")) {
                                valueList.add(option);

                                if (dOption.input == 1 || dOption.input == 2) {
                                    titleNameList.add(question.getRepeatId() + " " + dOption.id + " 追加文本输入");
                                    if (responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input") != null) {
                                        value = responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input");
                                    }
                                    valueList.add(value);
                                }
                            } else {
                                valueList.add("");

                                if (dOption.input == 1 || dOption.input == 2) {
                                    titleNameList.add(question.getRepeatId() + " " + dOption.id + " 追加文本输入");
                                    valueList.add("");
                                }
                            }
                        }
                    } else {// 标题内容
                        titleNameList.add(question.getRepeatId() + title + " " + option);

                        if (optionType == ResponseConstants.EXPORT_ID) {// 选项编号
                            if (responseObject.getJSONObject(question.getRepeatId()) != null
                                    && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id) != null
                                    && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id).equals("1")) {
                                valueList.add("1");

                                if (dOption.input == 1 || dOption.input == 2) {
                                    titleNameList.add(question.getRepeatId() + title + " " + option + " 追加文本输入");
                                    if (responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input") != null) {
                                        value = responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input");
                                    }
                                    valueList.add(value);
                                }
                            } else {
                                valueList.add("0");

                                if (dOption.input == 1 || dOption.input == 2) {
                                    titleNameList.add(question.getRepeatId() + title + " " + option + " 追加文本输入");
                                    valueList.add("");
                                }
                            }

                        } else {// 选项内容
                            if (responseObject.getJSONObject(question.getRepeatId()) != null
                                    && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id) != null
                                    && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id).equals("1")) {
                                valueList.add(option);

                                if (dOption.input == 1 || dOption.input == 2) {
                                    titleNameList.add(question.getRepeatId() + title + " " + option + " 追加文本输入");
                                    if (responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input") != null) {
                                        value = responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input");
                                    }
                                    valueList.add(value);
                                }
                            } else {
                                valueList.add("");

                                if (dOption.input == 1 || dOption.input == 2) {
                                    titleNameList.add(question.getRepeatId() + title + " " + option + " 追加文本输入");
                                    valueList.add("");
                                }
                            }
                        }
                    }
                } else {// 单列导出
                    if (optionType == ResponseConstants.EXPORT_ID) {// 选项编号
                        if (responseObject.getJSONObject(question.getRepeatId()) != null
                                && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id) != null
                                && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id).equals("1")) {
                            v += dOption.id + " ";

                            if (dOption.input == 1 || dOption.input == 2) {
                                singleTitleList.add(question.getRepeatId() + " " + dOption.id + " 追加文本输入");
                                if (responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input") != null) {
                                    value = responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input");
                                }
                                singleValueList.add(value);
                            }
                        } else {
                            v += "";

                            if (dOption.input == 1 || dOption.input == 2) {
                                singleTitleList.add(question.getRepeatId() + " " + dOption.id + " 追加文本输入");
                                singleValueList.add("");
                            }
                        }
                    } else {// 选项内容
                        if (responseObject.getJSONObject(question.getRepeatId()) != null
                                && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id) != null
                                && responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id).equals("1")) {
                            v += option + " ";

                            if (dOption.input == 1 || dOption.input == 2) {
                                singleTitleList.add(question.getRepeatId() + " " + dOption.id + " 追加文本输入");
                                if (responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input") != null) {
                                    value = responseObject.getJSONObject(question.getRepeatId()).getString(question.getRepeatId() + "." + dOption.id + ".input");
                                }
                                singleValueList.add(value);
                            }
                        } else {
                            v += "";

                            if (dOption.input == 1 || dOption.input == 2) {
                                singleTitleList.add(question.getRepeatId() + " " + dOption.id + " 追加文本输入");
                                singleValueList.add("");
                            }
                        }
                    }
                    singleExportDTO.setValue(v);
                }
            }
            singleExportDTO.setInputTitleList(singleTitleList);
            singleExportDTO.setInputValueList(singleValueList);

            if (multiSelectType == ResponseConstants.EXPORT_MULTIPLE) {
                for (int i = 0; i < titleNameList.size(); i++) {
                    titleMap.put(titleNameList.get(i), titleNameList.get(i));
                    titleTypeMap.put(titleNameList.get(i), "CELL_TYPE_STRING");
                    exportHashMap.put(titleNameList.get(i), valueList.get(i));
                }
            } else {
                if (titleType == ResponseConstants.EXPORT_ID) {
                    titleMap.put(singleExportDTO.getQuestionId(), singleExportDTO.getQuestionId());
                } else {
                    titleMap.put(singleExportDTO.getQuestionId(), singleExportDTO.getQuestionId() + " " + singleExportDTO.getTitle());
                }
                titleTypeMap.put(singleExportDTO.getQuestionId(), "CELL_TYPE_STRING");
                exportHashMap.put(singleExportDTO.getQuestionId(), singleExportDTO.getValue());

                // 追加输入
                List<String> inputTitleList = singleExportDTO.getInputTitleList();
                List<String> inputValueList = singleExportDTO.getInputValueList();
                if (inputTitleList != null && !inputTitleList.isEmpty()) {
                    for (int i = 0; i < inputTitleList.size(); i++) {
                        titleMap.put(singleExportDTO.getQuestionId() + "&&" + i, inputTitleList.get(i));
                        titleTypeMap.put(singleExportDTO.getQuestionId() + "&&" + i, "CELL_TYPE_STRING");
                        exportHashMap.put(singleExportDTO.getQuestionId() + "&&" + i, inputValueList.get(i));
                    }
                }
            }
        }

        // 赋值题、排序题
        else if (question.type == 9 || question.type == 11) {

            // 选项
            List<Object> objectList = question.elements;
            for (int i = 0; i < objectList.size(); i++) {
                String option = "";
                String value = "";
                if (question.type == 3) {
                    value = "0";
                }
                DOption dOption = (DOption) objectList.get(i);
                // 标题
                List<DText> optionTextList = dOption.texts;
                for (DText dText : optionTextList) {
                    if (titleType == ResponseConstants.EXPORT_CONTENT) {
                        option = option + dOption.id + "." + dText.text + " ";
                    } else {
                        option = option + dOption.id;
                    }
                }
                if (titleType == ResponseConstants.EXPORT_CONTENT) {
                    titleName = question.getRepeatId() + "." + title + " " + option;
                } else {
                    titleName = question.getRepeatId() + "." + option;
                }
                titleMap.put(question.getRepeatId() + "." + dOption.id, titleName);

                // 标题类型
                if (question.type == 9 && (dOption.type == 4 || dOption.type == 5)) {
                    titleTypeMap.put(question.getRepeatId() + "." + dOption.id, "CELL_TYPE_NUMERIC");
                } else {
                    titleTypeMap.put(question.getRepeatId() + "." + dOption.id, "CELL_TYPE_STRING");
                }

                // 值
                String tempValue = responseObject.getString(question.getRepeatId());
                JSONObject object = JSONObject.parseObject(tempValue);
                if (object != null && object.getString(question.getRepeatId() + "." + dOption.id) != null) {
                    value = object.getString(question.getRepeatId() + "." + dOption.id);
                    if (optionType != ResponseConstants.EXPORT_ID && object.getString(question.getRepeatId() + "." + dOption.id + ".input") != null) {
                        value = value + " " + object.getString(question.getRepeatId() + "." + dOption.id + ".input");
                    }
                }
                exportHashMap.put(question.getRepeatId() + "." + dOption.id, value);
            }
        }

        //拍照题、录音题、文件题
        else if (question.type == 13 || question.type == 22 || question.type == 24) {
            if (titleType == ResponseConstants.EXPORT_CONTENT) {
                titleName = question.getRepeatId() + "." + title;
            } else {
                titleName = question.getRepeatId();
            }

            // 值
            if (responseObject.get(question.getRepeatId()) != null && !responseObject.getString(question.getRepeatId()).equals("{}")) {
                JSONArray valueArray = responseObject.getJSONArray(question.getRepeatId());
                for (int i = 0; i < valueArray.size(); i++) {
                    String tempKey = question.getRepeatId();
                    if (question.type == 13 && photo) {
                        String imageValue = imgUrlPrefix + valueArray.getString(i) + ";";
                        tempKey += "_图片_" + (i + 1);
                        exportHashMap.put(question.getRepeatId() + "_图片_" + (i + 1), imageValue);
                    }

                    if (question.type == 22 && recording) {
                        String audioValue = null;
                        if (valueArray.getJSONObject(i) != null
                                && StringUtils.isNotBlank(valueArray.getJSONObject(i).getString("file"))) {
                            audioValue = imgUrlPrefix + valueArray.getJSONObject(i).getString("file") + ";";
                        }
                        tempKey += "_录音_" + (i + 1);
                        exportHashMap.put(question.getRepeatId() + "_录音_" + (i + 1), audioValue);
                    }

                    if (question.type == 24) {
                        String fileValue = null;
                        if (valueArray.getJSONObject(i) != null
                                && StringUtils.isNotBlank(valueArray.getJSONObject(i).getString("filename"))) {
                            fileValue = imgUrlPrefix + valueArray.getJSONObject(i).getString("filename") + ";";
                        }
                        tempKey += "_文件_" + (i + 1);
                        exportHashMap.put(question.getRepeatId() + "_文件_" + (i + 1), fileValue);
                    }

                    titleMap.put(tempKey, tempKey);
                    titleTypeMap.put(tempKey, "CELL_TYPE_STRING");
                }
            } else {
                titleMap.put(question.getRepeatId(), titleName);
                titleTypeMap.put(question.getRepeatId(), "CELL_TYPE_STRING");
                exportHashMap.put(question.getRepeatId(), null);
            }
        }

        // 定位题
        else if (question.type == 14) {
            if (titleType == ResponseConstants.EXPORT_CONTENT) {
                titleName = question.getRepeatId() + "." + title;
            } else {
                titleName = question.getRepeatId();
            }

            titleMap.put(question.getRepeatId() + "." + "lat", titleName + "." + "经度");
            titleMap.put(question.getRepeatId() + "." + "lng", titleName + "." + "纬度");
            titleMap.put(question.getRepeatId() + "." + "poi", titleName + "." + "定位");
            titleTypeMap.put(question.getRepeatId() + "." + "lat", "CELL_TYPE_STRING");
            titleTypeMap.put(question.getRepeatId() + "." + "lng", "CELL_TYPE_STRING");
            titleTypeMap.put(question.getRepeatId() + "." + "poi", "CELL_TYPE_STRING");

            if (responseObject.get(question.getRepeatId()) != null) {
                JSONObject valueObject = responseObject.getJSONObject(question.getRepeatId());
                String lat = valueObject.getString("lat");
                String lng = valueObject.getString("lng");
                String poi = valueObject.getString("poi");

                exportHashMap.put(question.getRepeatId() + "." + "lat", lat);
                exportHashMap.put(question.getRepeatId() + "." + "lng", lng);
                exportHashMap.put(question.getRepeatId() + "." + "poi", poi);
            }
        }

        // 行政区题
        else if (question.type == 17) {
            if (titleType == ResponseConstants.EXPORT_CONTENT) {
                titleName = question.getRepeatId() + "." + title;
            } else {
                titleName = question.getRepeatId();
            }

            titleMap.put(question.getRepeatId() + "." + "prov", titleName + ".省");
            titleMap.put(question.getRepeatId() + "." + "city", titleName + ".市");
            titleMap.put(question.getRepeatId() + "." + "dist", titleName + ".区县");
            titleTypeMap.put(question.getRepeatId() + "." + "prov", "CELL_TYPE_STRING");
            titleTypeMap.put(question.getRepeatId() + "." + "city", "CELL_TYPE_STRING");
            titleTypeMap.put(question.getRepeatId() + "." + "dist", "CELL_TYPE_STRING");

            if (responseObject.get(question.getRepeatId()) != null) {
                JSONObject valueObject = responseObject.getJSONObject(question.getRepeatId());
                String prov = valueObject.getString(question.getRepeatId() + "." + "prov");
                String city = valueObject.getString(question.getRepeatId() + "." + "city");
                String dist = valueObject.getString(question.getRepeatId() + "." + "dist");

                exportHashMap.put(question.getRepeatId() + "." + "prov", prov);
                exportHashMap.put(question.getRepeatId() + "." + "city", city);
                exportHashMap.put(question.getRepeatId() + "." + "dist", dist);

                if (valueObject.containsKey(question.getRepeatId() + "." + "town")) {
                    String town = valueObject.getString(question.getRepeatId() + "." + "town");
                    titleMap.put(question.getRepeatId() + "." + "town", titleName + "." + "乡镇");
                    titleTypeMap.put(question.getRepeatId() + "." + "town", "CELL_TYPE_STRING");
                    exportHashMap.put(question.getRepeatId() + "." + "town", town);
                }
                if (valueObject.containsKey(question.getRepeatId() + "." + "village")) {
                    String village = valueObject.getString(question.getRepeatId() + "." + "village");
                    titleMap.put(question.getRepeatId() + "." + "village", titleName + "." + "村道");
                    titleTypeMap.put(question.getRepeatId() + "." + "village", "CELL_TYPE_STRING");
                    exportHashMap.put(question.getRepeatId() + "." + "village", village);
                }
            }
        }

        // 矩阵单选题
        else if (question.type == 16) {
            JSONObject thisQuestionObject = null;
            if (responseObject.getJSONObject(question.getRepeatId()) != null) {
                thisQuestionObject = responseObject.getJSONObject(question.getRepeatId());
            }

            List<Object> objectList = question.elements;
            List<DOption> dOptionList = (List<DOption>) objectList.get(0);
            List<DQuestion> dQuestionList = (List<DQuestion>) objectList.get(1);

            for (DOption dOption : dOptionList) {
                title = "";// 标题
                for (DText dText : textList) {
                    title = title + dText.text + " ";
                }
                List<DText> optionTitleList = dOption.texts;
                for (DText dText : optionTitleList) {
                    title = title + dText.text + " ";
                }

                String value = "";
                for (DQuestion dQuestion : dQuestionList) {
                    String questionTitle = "";
                    List<DText> questionTitleList = dQuestion.texts;// 列标题列表
                    for (DText dText : questionTitleList) {
                        questionTitle = questionTitle + dText.text + " ";
                    }
                    if (dQuestion.type == 2) {// 所有选项
                        List<String> optionList = new ArrayList<>();
                        List<Object> colQuestionOptionListObject = dQuestion.elements;
                        for (Object object : colQuestionOptionListObject) {
                            DOption colQuestionOption = (DOption) object;
                            List<DText> colQuestionOptionTextList = colQuestionOption.texts;
                            for (DText text : colQuestionOptionTextList) {
                                title = title + colQuestionOption.id + "." + text.text + " ";
                                optionList.add(colQuestionOption.id + "=" + text.text);
                            }
                        }

                        String key = question.getRepeatId() + "." + dOption.id + "." + dQuestion.getRepeatId();
                        if (thisQuestionObject != null) {
                            Set<String> keySet = thisQuestionObject.keySet();
                            for (String optionKey : keySet) {
                                String[] optionKeyArray = optionKey.split("\\.");
                                if (StringUtils.isNotBlank(optionKeyArray[1]) && optionKeyArray[1].equals(dOption.id + "")) {
                                    for (Object object : colQuestionOptionListObject) {
                                        DOption colQuestionOption = (DOption) object;
                                        if (colQuestionOption.id.equals(optionKeyArray[3])) {
                                            if (optionType == ResponseConstants.EXPORT_ID) {// 编号
                                                value = value + colQuestionOption.id;
                                            } else {
                                                List<DText> colQuestionOptionTextList = colQuestionOption.texts;
                                                for (DText text : colQuestionOptionTextList) {
                                                    value = value + text.text + " ";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (titleType == ResponseConstants.EXPORT_CONTENT) {
                            titleName = question.getRepeatId() + "." + title;
                        } else {
                            titleName = question.getRepeatId() + "." + dOption.id + "." + dQuestion.getId();
                        }
                        titleMap.put(key, titleName);
                        titleTypeMap.put(key, "CELL_TYPE_STRING");
                        exportHashMap.put(key, value);
                        optionMap.put(key, optionList);
                    } else if (dQuestion.type == 3) {
                        List<Object> colQuestionOptionListObject = dQuestion.elements;
                        for (Object object : colQuestionOptionListObject) {
                            String optionTitle = title;
                            String optionText = "";
                            DOption colQuestionOption = (DOption) object;
                            List<DText> colQuestionOptionTextList = colQuestionOption.texts;
                            for (DText text : colQuestionOptionTextList) {
                                optionTitle = optionTitle + text.text + " ";
                                optionText = text.text;
                            }

                            value = "0";
                            String optionKey = question.getRepeatId() + "." + dOption.id + ".c1." + colQuestionOption.id;
                            if (responseObject.getJSONObject(question.getRepeatId()) != null &&
                                    responseObject.getJSONObject(question.getRepeatId()).getString(optionKey) != null) {
                                value = responseObject.getJSONObject(question.getRepeatId()).getString(optionKey);
                            }
                            if (titleType == ResponseConstants.EXPORT_CONTENT) {
                                titleName = question.getRepeatId() + "." + optionTitle + " ";
                            } else {
                                titleName = question.getRepeatId() + "." + colQuestionOption.id + "." + optionText + " ";
                            }

                            titleMap.put(optionKey, titleName);
                            titleTypeMap.put(question.getRepeatId(), "CELL_TYPE_STRING");
                            exportHashMap.put(optionKey, value);
                        }
                    } else {

                    }
                }
            }
        }

        // 表格题
        else if (question.type == 12) {
            JSONObject thisQuestionObject = null;
            if (responseObject.getJSONObject(question.getRepeatId()) != null) {
                thisQuestionObject = responseObject.getJSONObject(question.getRepeatId());
            }

            // 选项
            List<Object> objectList = question.elements;
            List<DOption> dOptionList = (List<DOption>) objectList.get(0);
            List<DQuestion> dQuestionList = (List<DQuestion>) objectList.get(1);

            String value = "";
            for (DOption dOption : dOptionList) {
                String optionTitle = "";// 标题
                List<DText> optionTitleList = dOption.texts;
                for (DText dText : optionTitleList) {
                    optionTitle = optionTitle + dText.text + " ";
                }

                for (DQuestion dQuestion : dQuestionList) {
                    String questionTitle = "";
                    List<DText> questionTitleList = dQuestion.texts;// 列标题列表
                    for (DText dText : questionTitleList) {
                        questionTitle = questionTitle + dText.text + " ";
                    }

                    if (dQuestion.type == 2 || dQuestion.type == 3) {// 显示所有的选项
                        List<Object> colQuestionOptionListObject = dQuestion.elements;
                        for (Object object : colQuestionOptionListObject) {
                            DOption colQuestionOption = (DOption) object;
                            String colQuestionOptionText = "";
                            String optionText = "";
                            List<DText> colQuestionOptionTextList = colQuestionOption.texts;
                            for (DText text : colQuestionOptionTextList) {
                                colQuestionOptionText = colQuestionOptionText + text.text + " ";
                                optionText = text.text;
                            }

                            String key = question.getRepeatId() + "." + dOption.id + "." + dQuestion.getRepeatId() + "." + colQuestionOption.id;
                            value = "0";
                            if (thisQuestionObject != null && thisQuestionObject.get(key) != null) {
                                value = thisQuestionObject.getString(key);
                            }
                            if (titleType == ResponseConstants.EXPORT_CONTENT) {
                                titleName = question.getRepeatId() + "." + title + optionTitle + " " + optionTitle + " " + questionTitle + colQuestionOptionText;
                            } else {
                                titleName = question.getRepeatId() + "." + dQuestion.attributes.get("id") + "." + optionText + " ";
                            }
                            titleMap.put(key, titleName);
                            titleTypeMap.put(key, "CELL_TYPE_STRING");
                            exportHashMap.put(key, value);
                        }
                    } else if (dQuestion.type == 19) {// 下拉搜索单选题
                        String option = "";// 选项
                        List<Object> optionList = dQuestion.elements;
                        for (int i = 0; i < optionList.size(); i++) {
                            DOption qOption = (DOption) optionList.get(i);
                            // 标题
                            List<DText> optionTextList = qOption.texts;
                            for (DText dText : optionTextList) {
                                option = option + qOption.id + "." + dText.text + " ";
                            }

                            String key = question.getRepeatId() + "." + dOption.id + "." + dQuestion.getRepeatId() + "." + qOption.id;
                            // 值
                            if (optionType == ResponseConstants.EXPORT_ID) {// 选项编号
                                if (thisQuestionObject != null && thisQuestionObject.getString(key) != null) {
                                    value = qOption.id + "";
                                }
                            } else {
                                // 选项内容
                                if (thisQuestionObject != null && thisQuestionObject.getString(key) != null) {
                                    value = qOption.id + ".";
                                    // 标题
                                    List<DText> checkedOptionTextList = qOption.texts;
                                    for (DText text : checkedOptionTextList) {
                                        value = value + text.text + " ";
                                    }
                                    if (qOption.input == 1 || qOption.input == 2) {
                                        if (thisQuestionObject.getString(key + ".input") != null) {
                                            String input = thisQuestionObject.getString(key + ".input");
                                            value = value + input;
                                        }
                                    }
                                }
                            }
                        }

                        if (titleType == ResponseConstants.EXPORT_CONTENT) {
                            titleName = question.getRepeatId() + "." + title + optionTitle + " " + questionTitle + " " + option;
                        } else {
                            titleName = question.getRepeatId();
                        }

                        titleMap.put(question.getRepeatId() + dOption.id + dQuestion.getId(), titleName);
                        titleTypeMap.put(question.getRepeatId() + dOption.id + dQuestion.getId(), "CELL_TYPE_STRING");
                        exportHashMap.put(question.getRepeatId() + dOption.id + dQuestion.getId(), value);
                    } else {
                        if (titleType == ResponseConstants.EXPORT_CONTENT) {
                            titleName = question.getRepeatId() + "." + title + " " + optionTitle + " " + questionTitle;
                        } else {
                            titleName = question.getRepeatId() + "." + dQuestion.attributes.get("id");
                        }
                        titleMap.put(question.getRepeatId() + "." + dOption.id + "." + dQuestion.getRepeatId(), titleName);

                        if (dQuestion.type == 4 || dQuestion.type == 5) {
                            titleTypeMap.put(question.getRepeatId() + "." + dOption.id + "." + dQuestion.getRepeatId(), "CELL_TYPE_NUMERIC");
                        } else {
                            titleTypeMap.put(question.getRepeatId() + "." + dOption.id + "." + dQuestion.getRepeatId(), "CELL_TYPE_STRING");
                        }

                        if (thisQuestionObject != null) {
                            value = thisQuestionObject.getString(question.getRepeatId() + "." + dOption.id + "." + dQuestion.getRepeatId());
                        }
                        exportHashMap.put(question.getRepeatId() + "." + dOption.id + "." + dQuestion.getRepeatId(), value);
                    }
                }
            }
        }

        // 级联题
        else if (question.type == 23) {
            if (titleType == ResponseConstants.EXPORT_CONTENT) {
                titleName = question.getRepeatId() + "." + title;
            } else {
                titleName = question.getRepeatId();
            }

            String data = question.getAttr("data");
            JSONArray dataArray = JSONArray.parseArray(data);
            for (int i = 0; i < dataArray.size(); i++) {
                List<String> optionJsonList = new ArrayList<>();// 每级拥有的选项列表

                JSONObject optionObject = dataArray.getJSONObject(i);
                String dataOptionId = optionObject.getString("id");
                String dataOptionTitle = optionObject.getString("title");
                List<Object> objectList = JSON.parseArray(variableList.get(i).value);// 级联的变量

                titleMap.put(question.getRepeatId() + "." + dataOptionId, titleName + " " + dataOptionTitle);
                titleTypeMap.put(question.getRepeatId() + "." + dataOptionId, "CELL_TYPE_STRING");

                String value = "";
                if (responseObject.get(question.getRepeatId()) != null) {
                    value = responseObject.get(question.getRepeatId()).toString();
                }
                if (StringUtils.isBlank(value)) {
                    exportHashMap.put(question.getRepeatId() + "." + dataOptionId, "");
                } else {
                    String cascadeStr = "";
                    JSONObject valueObject = JSON.parseObject(value, Feature.OrderedField);
                    Set keys = valueObject.keySet();
                    Object[] keyArray = keys.toArray();
                    for (Object o : objectList) {
                        Map<String, Object> map = (Map<String, Object>) o;
                        String optionId = map.get("id").toString();
                        String optionTitle = map.get("title").toString();
                        optionJsonList.add(optionId + "=" + optionTitle);
                        if (optionId.equals(valueObject.get(keyArray[i].toString()).toString())) {
                            if (optionType == ResponseConstants.EXPORT_ID) {// 选项编号
                                cascadeStr = optionId;
                            } else {
                                cascadeStr = optionId + "." + optionTitle;
                            }
                            exportHashMap.put(question.getRepeatId() + "." + dataOptionId, cascadeStr);
                        }
                    }
                    optionMap.put(question.getRepeatId() + "." + dataOptionId, optionJsonList);
                }
            }
        }

        // 签名题
        else if (question.type == 21) {
            // TODO
        }

        // 批注
        if (needNotes == 1) {
            if (titleType == ResponseConstants.EXPORT_CONTENT) {
                titleName = question.getRepeatId() + "." + title + " 批注:";
            } else {
                titleName = question.getRepeatId() + " 批注:";
            }
            titleMap.put(question.getRepeatId() + ".note", titleName);
            titleTypeMap.put(question.getRepeatId() + ".note", "CELL_TYPE_STRING");

            String noteStr = "";
            if (StringUtils.isNotBlank(notesData)) {
                JSONObject jsonObject = JSONObject.parseObject(notesData);
                if (jsonObject != null && jsonObject.get(question.getRepeatId()) != null) {
                    JSONObject noteJson = jsonObject.getJSONObject(question.getRepeatId());
                    if (noteJson != null && noteJson.get("c") != null) {
                        noteStr = noteJson.get("c").toString();
                    }
                }
            }
            exportHashMap.put(question.getRepeatId() + ".note", noteStr);
        }

        return exportHashMap;
    }

    /**
     * 删除文件
     *
     * @param delVO
     * @return
     */
    public int deleteResponseFile(ResponseExportDelVO delVO) {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(delVO.getId());
        String path = filePath + "/export/" + exportHistory.getProjectId() + "/response/";
        boolean flag = FileUtil.deleteFile(path, exportHistory.getName());
        if (!flag) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "文件删除失败");
        }
        int row = exportDao.deleteByPrimaryKey(delVO.getId());
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "导出记录删除失败");
        }
        // 回写信息量总数
        BaseProject p = new BaseProject();
        p.setFileSize(new File(path + exportHistory.getName()).length());
        p.setLastModifyUser(ThreadLocalManager.getUserId());
        p.setLastModifyTime(new Date());
        p.setId(delVO.getProjectId());
        return projectDao.updateProjectDel(p);
    }


    /**
     * 上传文件
     *
     * @param file
     * @param projectId
     * @return
     */
    public Map uploadFile(MultipartFile file, Integer projectId) {
        Map<String, String> map = new HashMap<>();
        String pre = "/project-file/" + projectId + "/attach/";
        List<Map<String, Object>> res = FileUtil.uploadFiles(new MultipartFile[]{file}, filePath + pre);
        String fileName = res.get(0).get("randomFileName").toString();
        if (res.size() > 0) {
            BaseResponseFile responseFile = new BaseResponseFile();
            responseFile.setDynamicTableName(ProjectConstants.getResponseFileTableName(projectId));
            responseFile.setFileName(fileName);
            responseFile.setFilePath(pre + fileName);
            responseFile.setCreateTime(new Date());
            responseFileDao.insertSelective(responseFile);
        }
        map.put("filePath", pre + fileName);
        map.put("fileName", fileName);
        return map;
    }

    /**
     * 删除文件
     *
     * @param
     * @return
     */
    public Boolean deleteFile(ResponseDelVO delVO) {
        String fileName = delVO.getFileName().substring(delVO.getFileName().lastIndexOf("/") + 1);
        String pre = "/project-file/" + delVO.getProjectId() + "/attach/";
        boolean result = FileUtil.deleteFile(filePath + pre, fileName);
        if (result) {
            Example example = new Example(BaseResponseFile.class);
            example.setTableName(ProjectConstants.getResponseFileTableName(delVO.getProjectId()));
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("fileName", fileName);
            result = responseFileDao.deleteByExample(example) > 0;
        }
        return result;
    }

    /**
     * 保存批注
     *
     * @param noteVO
     * @return
     */
    public Integer saveAuditNotes(ResponseAuditNoteVO noteVO) {
        JSONObject note;
        // JSONObject auditNote = new JSONObject();
        Example example = new Example(BaseResponse.class);
        example.setTableName(ProjectConstants.getResponseTableName(noteVO.getProjectId()));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", noteVO.getResponseId());
        BaseResponse response = responseDao.selectOneByExample(example);
        if (StringUtils.isNotBlank(response.getAuditNotes())) {
            note = JSONObject.parseObject(response.getAuditNotes());
            if (note.containsKey(noteVO.getQId())) {
                JSONObject question = new JSONObject();
                question.put("c", noteVO.getAuditNotes());
                question.put("n", ThreadLocalManager.getUserId());
                question.put("t", new Date());
                note.put(noteVO.getQId(), question);
            } else {
                JSONObject question = new JSONObject();
                question.put("c", noteVO.getAuditNotes());
                question.put("n", ThreadLocalManager.getUserId());
                question.put("t", new Date());
                note.put(noteVO.getQId(), question);
            }
        } else {
            note = new JSONObject();
            JSONObject question = new JSONObject();
            question.put("c", noteVO.getAuditNotes());
            question.put("n", ThreadLocalManager.getUserId());
            question.put("t", new Date());
            note.put(noteVO.getQId(), question);
        }
        response.setAuditNotes(note.toJSONString());
        response.setDynamicTableName(ProjectConstants.getResponseTableName(noteVO.getProjectId()));
        return responseDao.updateByPrimaryKeySelective(response);
    }

    /**
     * 录音/附件/导出文件下载
     *
     * @param id
     * @param projectId
     * @param type
     * @param response
     * @throws Exception
     */
    public void downloadFile(Integer id, Integer projectId, Integer type, HttpServletResponse response) throws Exception {
        String path = "";
        if (type.equals(1)) {
            // 录音
            Example example = new Example(BaseResponseAudio.class);
            example.setTableName(ProjectConstants.getResponseAudioTableName(projectId));
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id", id);
            BaseResponseAudio audio = audioDao.selectOneByExample(example);
            path = filePath + audio.getFilePath();
        } else if (type.equals(2)) {
            // 附件
            Example example = new Example(BaseResponseFile.class);
            example.setTableName(ProjectConstants.getResponseFileTableName(projectId));
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id", id);
            BaseResponseFile file = fileDao.selectByPrimaryKey(example);
            path = filePath + file.getFilePath();
        } else if (type.equals(3)) {
            // 答卷导出记录下载
            Example example = new Example(BaseProjectExportHistory.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id", id);
            BaseProjectExportHistory history = exportDao.selectOneByExample(example);
            path = filePath + history.getFilePath();
        }
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * 导出成压缩包格式
     *
     * @param projectId
     * @param response
     * @throws IOException
     */
    public void exportBatchAudio(Integer projectId, String zipName, HttpServletResponse response) throws Exception {
        // 文件名
        String pre = filePath + "/export/" + projectId + "/response/voice/";
        // 项目成员基本信息
        List<InterviewerDTO> interviewIds = audioDao.getInterviews(projectId);
        Map<Integer, InterviewerDTO> userMap = new HashMap<>();
        for (InterviewerDTO dto : interviewIds) {
            userMap.put(dto.getId(), dto);
        }

        // 项目所有录音
        Example example = new Example(BaseResponseAudio.class);
        example.setTableName(ProjectConstants.getResponseAudioTableName(projectId));
        List<BaseResponseAudio> audioList = audioDao.selectByExample(example);

        // 处理
        Map<Integer, List<BaseResponseAudio>> userAudios = new HashMap<>();
        for (BaseResponseAudio audio : audioList) {
            for (InterviewerDTO dto : interviewIds) {
                if (dto.getId().equals(audio.getInterviewId())) {
                    List<BaseResponseAudio> tempList;
                    if (null == userAudios.get(dto.getId())) {
                        tempList = new ArrayList<>();
                    } else {
                        tempList = userAudios.get(dto.getId());
                    }
                    tempList.add(audio);
                    userAudios.put(dto.getId(), tempList);
                }
            }
        }

        // 生成文件
        for (Integer userId : userMap.keySet()) {
            InterviewerDTO user = userMap.get(userId);
            String subDir = user.getName() != null ? user.getName() : (user.getPhone() != null ? user.getPhone() : user.getEmail());
            File userFile = new File(pre + subDir);
            if (!userFile.exists()) {
                userFile.mkdirs();
            }
            List<BaseResponseAudio> audios = userAudios.get(userId);
            for (BaseResponseAudio audio : audios) {
                String path = filePath + audio.getFilePath();
                File audioFile = new File(path);
                if (!audioFile.exists()) {
                    continue;
                }
                String tempPath = pre + subDir + path.substring(path.lastIndexOf("/"));
                File destFile = new File(tempPath);
                //目标文件
                FileUtils.copyFile(audioFile, destFile);
            }
        }

        String fileName = filePath + "/export/" + projectId + "/response/" + zipName + ".zip";
        File zipFile = new File(fileName);
        FileOutputStream out = new FileOutputStream(zipFile);
        ZipUtil.toZip(pre, out, true);
        FileUtil.downloadFileToClient(fileName, response);

        // 删除文件
        FileUtil.deleteFile(zipFile);
        FileUtil.deleteFile(new File(pre));
    }

    /**
     * 附件下载
     *
     * @param id
     * @param projectId
     * @param response
     * @throws Exception
     */
    public void downloadFile(Integer id, Integer projectId, HttpServletResponse response) throws Exception {
        Example example = new Example(BaseResponseFile.class);
        example.setTableName(ProjectConstants.getResponseFileTableName(projectId));
        BaseResponseFile file = fileDao.selectByPrimaryKey(id);
        String path = filePath + file.getFilePath();
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * 答卷审核编辑
     *
     * @param auditEditVO
     * @return
     */
    public ResponseAuditEditDTO auditEditResponse(ResponseAuditEditVO auditEditVO) {
        Integer projectId = auditEditVO.getProjectId();
        BaseResponse response = this.getResponse(projectId, auditEditVO.getResponseId(), auditEditVO.getResponseGuid());
        BaseProjectQuestionnaire projectQuestionnaire = pQuestionnaireDao.selectByPrimaryKey(response.getQuestionnaireId());
        BaseProjectModule projectModule = moduleDao.selectByPrimaryKey(projectQuestionnaire.getModuleId());
        String jsDir = "/js/" + projectId + "/" + projectModule.getCode() + "-" + projectQuestionnaire.getVersion();

        ResponseAuditEditDTO auditEditDTO = new ResponseAuditEditDTO();
        BeanUtils.copyProperties(response, auditEditDTO);
        auditEditDTO.setName(projectModule.getName());
        auditEditDTO.setJsDir(jsDir);
        return auditEditDTO;
    }

    /**
     * 答卷重置
     *
     * @param resetVO
     * @return
     */
    public int resetResponse(ResponseResetVO resetVO) {
        Integer projectId = resetVO.getProjectId();
        Date now = new Date();
        BaseResponse response = this.getResponse(projectId, null, resetVO.getResponseGuid());
        if (null == response) {
            throw new ServiceException(ErrorCode.RESPONSE_NOT_EXIST);
        }

        // 物理删除历史数据
        Example example = new Example(BaseResponseHistory.class);
        example.setTableName(ProjectConstants.getResponseHistoryTableName(projectId));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("responseGuid", resetVO.getResponseGuid());
        historyDao.deleteByExample(example);
        // 逻辑删除答卷数据
        response.setIsDelete(ProjectConstants.DELETE_YES);
        response.setDeleteUser(ThreadLocalManager.getUserId());
        response.setDeleteTime(now);
        int row = responseDao.updateByPrimaryKeySelective(response);
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "答卷重置失败");
        }
        return row;
    }

    /**
     * 答卷历史新增
     *
     * @param response
     * @param isException
     * @param projectId
     * @return
     */
    public int insertHistory(BaseResponse response, boolean isException, Integer projectId) {
        BaseResponseHistory responseHistory = new BaseResponseHistory();
        BeanUtils.copyProperties(response, responseHistory);
        responseHistory.setDynamicTableName(ProjectConstants.getResponseHistoryTableName(projectId));
        responseHistory.setCreateTime(new Date());
        responseHistory.setIsException(ResponseConstants.EXCEPTION_NO);
        if (isException) {
            responseHistory.setIsException(ResponseConstants.EXCEPTION_YES);
        }
        int row = historyDao.insertSelective(responseHistory);
        if (row < 1) {
            throw new ServiceException(ErrorCode.SAVE_RESPONSE_HISTORY_ERROR);
        }
        return row;
    }

    /**
     * 答卷替换
     *
     * @param replaceVO
     * @return
     */
    public int replaceResponse(ResponseReplaceVO replaceVO) {
        Integer projectId = replaceVO.getProjectId();
        BaseResponseHistory responseHistory = historyDao.selectByPrimaryKey(replaceVO.getHistoryId());
        if (null == responseHistory) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "历史答卷不存在");
        }

        BaseResponse nowResponse = this.getResponse(projectId, null, responseHistory.getResponseGuid());
        if (null == nowResponse || nowResponse.getIsDelete().equals(ProjectConstants.DELETE_YES)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "未找到要替换的目标答卷");
        }

        // 备份到历史
        nowResponse.setId(null);
        int row = this.insertHistory(nowResponse, false, projectId);
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "备份到历史失败");
        }

        // 替换
        nowResponse.setSubmitData(responseHistory.getSubmitData());
        nowResponse.setSubmitState(responseHistory.getSubmitState());
        nowResponse.setResponseData(responseHistory.getResponseData());
        nowResponse.setQuestionData(responseHistory.getQuestionData());
        nowResponse.setAnsweredCurrent(responseHistory.getAnsweredCurrent());
        nowResponse.setAnsweredQuestions(responseHistory.getAnsweredQuestions());
        nowResponse.setLastModifyTime(new Date());
        row = responseDao.updateByPrimaryKeySelective(nowResponse);
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "答卷替换失败");
        }

        return row;
    }

    /**
     * 获取默认导出属性
     *
     * @param projectId
     * @return
     */
    public List<String> getExportDefaultProperty(Integer projectId) {
        List<String> res = new ArrayList<>();
        BaseProject project = projectDao.selectByPrimaryKey(projectId);
        BaseProjectProperty projectProperty = sampleService.getProjectSampleProperty(projectId);
//        if (project.getType().equals(ProjectConstants.PROJECT_TYPE_CATI)) {
//
//        } else if (project.getType().equals(ProjectConstants.PROJECT_TYPE_CAWI)) {
//
//        } else if (project.getType().equals(ProjectConstants.PROJECT_TYPE_CAPI)) {
//
//        } else if (project.getType().equals(ProjectConstants.PROJECT_TYPE_CADI)) {
//
//        } else {
//
//        }
        res.add("userName");
        res.add("responseStatus");
        res.add("startTime");
        res.add("endTime");
        res.add("responseLen");
        if (StringUtils.isNotBlank(projectProperty.getUseProperty())) {
            JSONArray jsonArray = JSONArray.parseArray(projectProperty.getUseProperty());
            for (int i = 0; i < jsonArray.size(); i++) {
                res.add(jsonArray.get(i).toString());
            }
        }
        return res;
    }

    /**
     * 导出线程
     * TODO
     */
    private class ExportThread implements Runnable {

        private Integer moduleId;
        private String moduleName;
        private Integer questionnaireId;
        private String xmlContent;
        private List<LinkedHashMap<String, String>> exportMapList;

        public ExportThread(Integer moduleId, Integer questionnaireId) {

        }

        @Override
        public void run() {

        }
    }
}
