package com.monetware.ringsurvey.business.service.qnaire;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.ProjectConstants;
import com.monetware.ringsurvey.business.pojo.constants.QuestionnaireConstants;
import com.monetware.ringsurvey.business.pojo.constants.UserConstants;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.*;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.vo.qnaire.QnairePreviewVO;
import com.monetware.ringsurvey.business.pojo.vo.questionnaire.*;
import com.monetware.ringsurvey.business.service.project.ProjectService;
import com.monetware.ringsurvey.business.service.project.QuestionnaireService;
import com.monetware.ringsurvey.business.service.user.UserService;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.survml.SurvMLDocument;
import com.monetware.ringsurvey.survml.SurvMLDocumentParser;
import com.monetware.ringsurvey.survml.exceptions.SurvMLParseException;
import com.monetware.ringsurvey.survml.questions.Page;
import com.monetware.ringsurvey.survml.questions.Question;
import com.monetware.ringsurvey.survml.SurvMLDocument;
import com.monetware.ringsurvey.survml.SurvMLDocumentParser;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.survml.exceptions.SurvMLParseException;
import com.monetware.ringsurvey.survml.questions.Page;
import com.monetware.ringsurvey.survml.questions.Question;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.date.DateUtil;
import com.monetware.ringsurvey.system.util.file.FileUtil;
import com.monetware.ringsurvey.system.util.file.PDFUtil;
import com.monetware.ringsurvey.system.util.file.XMLUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class QnaireService {

    @Value("${fileUrl.qnaire}")
    private String questionnaireDir;

    @Autowired
    private QuestionnaireDao questionnaireDao;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private ProjectQuestionnaireDao projectQuestionnaireDao;

    @Autowired
    private ProjectModuleDao moduleDao;

    @Autowired
    private QnaireResourceDao resourceDao;

    @Autowired
    private QnaireResourceTagDao resourceTagDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * ?????? ?????? ??????????????????xml
     *
     * @param editVO
     * @return
     */
    public QnaireGetDTO getMyQnaireXmlContent(QuestionnaireEditVO editVO) {
        QnaireGetDTO res = new QnaireGetDTO();
        if (editVO.getProjectId() == null) {
            Example example = new Example(BaseQuestionnaire.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
            criteria.andEqualTo("id", editVO.getQuestionnaireId());
            BaseQuestionnaire baseQuestionnaire = questionnaireDao.selectOneByExample(example);
            BeanUtils.copyProperties(baseQuestionnaire, res);
        } else {
            BaseProjectModule projectModule = moduleDao.selectByPrimaryKey(editVO.getModuleId());
            BaseQuestionnaire baseQuestionnaire;
            if (null == editVO.getQuestionnaireId()) {
                baseQuestionnaire = JSONObject.parseObject(projectModule.getEditContent(), BaseQuestionnaire.class);
                if (null == baseQuestionnaire) {
                    baseQuestionnaire = new BaseQuestionnaire();
                    baseQuestionnaire.setName(projectModule.getName());
                    BeanUtils.copyProperties(baseQuestionnaire, res);
                    res.setModuleId(projectModule.getId());
                    return res;
                }
            } else {
                baseQuestionnaire = new BaseQuestionnaire();
                BaseProjectQuestionnaire projectQuestionnaire = questionnaireService.getProjectQuestionnaire(projectModule);
                BeanUtils.copyProperties(projectQuestionnaire, baseQuestionnaire);
                if (projectModule.getEditFlag().equals(ProjectConstants.OPEN)) {
                    BaseQuestionnaire editQuestionnaire = JSONObject.parseObject(projectModule.getEditContent(), BaseQuestionnaire.class);
                    baseQuestionnaire.setXmlContent(editQuestionnaire.getXmlContent());
                    baseQuestionnaire.setName(editQuestionnaire.getName());
                }
            }
            BeanUtils.copyProperties(baseQuestionnaire, res);
            res.setModuleId(projectModule.getId());
        }
        return res;
    }

    /**
     * ??????????????????
     *
     * @param myQnaireListVO
     * @return
     */
    public PageList<Page> getMyQnaireList(MyQnaireListVO myQnaireListVO) {
        Date now = new Date();
        myQnaireListVO.setUserId(ThreadLocalManager.getUserId());
        com.github.pagehelper.Page page = PageHelper.startPage(myQnaireListVO.getPageNum(), myQnaireListVO.getPageSize());
        List<MyQnaireListDTO> myQnaireList = questionnaireDao.getMyQnaireList(myQnaireListVO);
        if (null != myQnaireList && !myQnaireList.isEmpty()) {
            for (MyQnaireListDTO myQnaire : myQnaireList) {
                long ct = DateUtil.getDateDuration(myQnaire.getCreateTime(), now);
                if (ct < 7 * 24 * 60 * 60L) {
                    myQnaire.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(ct));
                }
                long ut = DateUtil.getDateDuration(myQnaire.getLastModifyTime(), now);
                if (ut < 7 * 24 * 60 * 60L) {
                    myQnaire.setLastModifyTimeStr(DateUtil.secondToHourChineseStrByLong(ut));
                }
            }
        }
        return new PageList<>(page);
    }

    /**
     * ?????????????????????TestController??????
     *
     * @return
     */
    public PageList<Page> getMyQnaireListTest() {
        Date now = new Date();
        BaseUser testUser = userService.getTestUser();
        com.github.pagehelper.Page page = PageHelper.startPage(1, 10);
        List<MyQnaireListDTO> myQnaireList = questionnaireDao.getMyQnaireListTest(testUser.getId());
        if (null != myQnaireList && !myQnaireList.isEmpty()) {
            for (MyQnaireListDTO myQnaire : myQnaireList) {
                long ct = DateUtil.getDateDuration(myQnaire.getCreateTime(), now);
                if (ct < 7 * 24 * 60 * 60L) {
                    myQnaire.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(ct));
                }
                long ut = DateUtil.getDateDuration(myQnaire.getLastModifyTime(), now);
                if (ut < 7 * 24 * 60 * 60L) {
                    myQnaire.setLastModifyTimeStr(DateUtil.secondToHourChineseStrByLong(ut));
                }
            }
        }
        return new PageList<>(page);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public List<MyQnaireLabelListDTO> getMyQnaireLabelList() {
        return questionnaireDao.getMyQnaireLabelList(ThreadLocalManager.getUserId());
    }

    /**
     * ??????????????????
     *
     * @param myQuestionnaireVO
     * @return
     */
    public Map<String, Object> saveMyQuestionnaire(MyQuestionnaireVO myQuestionnaireVO) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> res = new HashMap<>();
        Date now = new Date();
        // ??????????????????
        try {
            SurvMLDocument survMLDocument = new SurvMLDocumentParser(myQuestionnaireVO.getXmlContent()).parse();
        } catch (SurvMLParseException e) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, e.getMessage());
        }
        long endTime1 = System.currentTimeMillis();
        log.info("???????????????????????????" + (endTime1 - startTime) + "s");
        if (null == myQuestionnaireVO.getPageNum() || myQuestionnaireVO.getPageNum() == 0) {
            myQuestionnaireVO.setPageNum(1);
        }
        BaseQuestionnaire questionnaire = new BaseQuestionnaire();
        BeanUtils.copyProperties(myQuestionnaireVO, questionnaire);
        // ??????????????????
        checkUserRole(myQuestionnaireVO);
        if (null == myQuestionnaireVO.getProjectId()) {
            questionnaire.setLastModifyUser(ThreadLocalManager.getUserId());
            questionnaire.setLastModifyTime(now);
            int row;
            if (null == questionnaire.getId()) {
                questionnaire.setCreateUser(ThreadLocalManager.getUserId());
                questionnaire.setCreateTime(now);
                questionnaire.setIsDelete(ProjectConstants.DELETE_NO);
                row = questionnaireDao.insertSelective(questionnaire);
            } else {
                row = questionnaireDao.updateByPrimaryKeySelective(questionnaire);
            }
            if (row < 1) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????");
            }

            // ??????????????????survey.js
            // C:/Monetware/Common/ringsurvey/questionnaires/js/design/??????ID/survey.js
            String filePathStr = questionnaireDir + "/js/design/" + questionnaire.getId();
            questionnaireService.createSurveyJS(filePathStr, questionnaire.getXmlContent());
            res.put("questionnaireId", questionnaire.getId());
        } else {
            Integer projectId = myQuestionnaireVO.getProjectId();
            ProjectConfigDTO configDTO = projectService.getProjectConfig(projectId);
            int row = 0;
            int resId = 0;
            if (configDTO.getMultipleQuestionnaire().equals(ProjectConstants.CLOSE)) {
                BaseProjectModule projectModule;
                if (null == myQuestionnaireVO.getModuleId()) {
                    // ??????????????????
                    projectModule = new BaseProjectModule();
                    projectModule.setProjectId(projectId);
                    projectModule.setCode("A");
                    projectModule.setName(myQuestionnaireVO.getName());
                    projectModule.setType(1);
                    projectModule.setMaxVersion(1);
                    projectModule.setEditFlag(ProjectConstants.OPEN);
                    projectModule.setEditContent(JSON.toJSONString(questionnaire));
                    projectModule.setCreateUser(ThreadLocalManager.getUserId());
                    projectModule.setCreateTime(now);
                    projectModule.setLastModifyUser(ThreadLocalManager.getUserId());
                    projectModule.setLastModifyTime(now);
                    projectModule.setStatus(ProjectConstants.QUESTIONNAIRE_STATUS_DRAFT);
                    projectModule.setIsDelete(ProjectConstants.DELETE_NO);
                    row = moduleDao.insertSelective(projectModule);
                } else {
                    projectModule = moduleDao.selectByPrimaryKey(myQuestionnaireVO.getModuleId());
                    projectModule.setEditFlag(ProjectConstants.OPEN);
                    projectModule.setEditContent(JSON.toJSONString(questionnaire));
                    projectModule.setLastModifyUser(ThreadLocalManager.getUserId());
                    projectModule.setLastModifyTime(now);
                    row = moduleDao.updateByPrimaryKeySelective(projectModule);
                    if (null != myQuestionnaireVO.getId()) {
                        resId = myQuestionnaireVO.getId();
                        res.put("questionnaireId", resId);
                    }
                }
                if (row < 1) {
                    throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????????????????");
                }
                String filePathStr = questionnaireDir + "/js/" + projectModule.getProjectId() + "/"
                        + projectModule.getCode() + "-" + projectModule.getMaxVersion();
                questionnaireService.createSurveyJS(filePathStr, myQuestionnaireVO.getXmlContent());
                res.put("moduleId", projectModule.getId());
            } else {
                // ?????????
                BaseProjectModule module = moduleDao.selectByPrimaryKey(myQuestionnaireVO.getModuleId());
                BaseProjectQuestionnaire projectQuestionnaire = projectQuestionnaireDao.selectByPrimaryKey(myQuestionnaireVO.getId());
                if (null != projectQuestionnaire) {
                    resId = projectQuestionnaire.getId();
                }
                module.setEditContent(JSON.toJSONString(questionnaire));
                module.setEditFlag(ProjectConstants.OPEN);
                module.setLastModifyUser(ThreadLocalManager.getUserId());
                module.setLastModifyTime(now);
                questionnaireService.updateProjectModuleEdit(module);
                String filePathStr = questionnaireDir + "/js/" + myQuestionnaireVO.getProjectId() + "/"
                        + module.getCode() + "-" + module.getMaxVersion();
                questionnaireService.createSurveyJS(filePathStr, questionnaire.getXmlContent());
                res.put("moduleId", module.getId());
                res.put("questionnaireId", resId);
            }
        }
        long endTime2 = System.currentTimeMillis();
        log.info("?????????????????????????????????" + (endTime2 - startTime) + "s");
        return res;
    }

    /**
     * ????????????
     *
     * @param exportVO
     */
    public void exportQuestionnaire(QuestionnaireExportVO exportVO, HttpServletResponse response) {
        BaseQuestionnaire baseQuestionnaire = new BaseQuestionnaire();
        String xmlContent = null;
        if (null == exportVO.getProjectId()) {
            baseQuestionnaire = questionnaireDao.selectByPrimaryKey(exportVO.getQuestionnaireId());
        } else {
            BaseProjectModule module = moduleDao.selectByPrimaryKey(exportVO.getModuleId());
            BaseProjectQuestionnaire projectQuestionnaire = questionnaireService.getProjectQuestionnaire(module);
            if (null != projectQuestionnaire) {
                BeanUtils.copyProperties(projectQuestionnaire, baseQuestionnaire);
            } else {
                baseQuestionnaire = JSONObject.parseObject(module.getEditContent(), BaseQuestionnaire.class);
                if (null == baseQuestionnaire) {
                    baseQuestionnaire = new BaseQuestionnaire();
                    baseQuestionnaire.setName(module.getName());
                }
            }
        }

        String fileName = baseQuestionnaire.getName();
        xmlContent = baseQuestionnaire.getXmlContent();
        if (null == xmlContent) {
            xmlContent = "";
        }
        String fileType = exportVO.getFileType();
        String logoUrl = baseQuestionnaire.getLogoUrl();
        try {
            if (fileType.equalsIgnoreCase(QuestionnaireConstants.EXPORT_XML)) {// xml
                XMLUtil.createQuestionnaireXML(fileName, xmlContent, response);
            } else if (fileType.equalsIgnoreCase(QuestionnaireConstants.EXPORT_WORD)) {// word
                // TODO word
            } else {// pdf
                SurvMLDocumentParser parse = new SurvMLDocumentParser(xmlContent);
                SurvMLDocument qDocument = parse.parse();
                List<Question> questionList = qDocument.getQuestions();
                List<Page> pages = qDocument.getPages();
                if ("JPDF".equals(fileType)) {
                    PDFUtil.createQuestionnairePDF(fileName, questionList, pages, 1, logoUrl, response);
                } else {
                    PDFUtil.createQuestionnairePDF(fileName, questionList, pages, 2, logoUrl, response);
                }

            }
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????");
        }
    }

    /**
     * ????????????
     *
     * @param questionnaireId
     * @return
     */
    public int deleteMyQnaire(Integer questionnaireId) {
        BaseQuestionnaire baseQuestionnaire = questionnaireDao.selectByPrimaryKey(questionnaireId);
        if (null == baseQuestionnaire || baseQuestionnaire.getIsDelete().equals(ProjectConstants.DELETE_YES)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????");
        }
        // ????????????
        baseQuestionnaire.setIsDelete(ProjectConstants.DELETE_YES);
        baseQuestionnaire.setDeleteUser(ThreadLocalManager.getUserId());
        baseQuestionnaire.setDeleteTime(new Date());
        int row = questionnaireDao.updateByPrimaryKeySelective(baseQuestionnaire);
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????");
        }
        return row;
    }

    /**
     * ???????????????
     *
     * @return
     */
    public List<QnaireResourceDTO> getQnaireResourceList() {
        // ???????????????????????????
        List<BaseQnaireResourceTag> resourceTags = this.getResourceTags();
        // ???????????????????????????
        List<BaseQnaireResource> resources = this.getResourceSearchList(new QnaireResourceSearchVO());

        List<QnaireResourceDTO> res = new ArrayList<>();
        QnaireResourceDTO resourceAll = new QnaireResourceDTO();
        resourceAll.setTagId(0);
        resourceAll.setTagName("??????");
        resourceAll.setResourceCount(resources.size());
        resourceAll.setResourceList(resources);
        res.add(resourceAll);
        for (BaseQnaireResourceTag resourceTag : resourceTags) {
            QnaireResourceDTO resourceDTO = new QnaireResourceDTO();
            resourceDTO.setTagId(resourceTag.getId());
            resourceDTO.setTagName(resourceTag.getName());

            int resourceCount = 0;
            List<BaseQnaireResource> tempList = new ArrayList<>();
            for (BaseQnaireResource resource : resources) {
                String tagId = resource.getTagId();
                JSONArray tagArray = JSONArray.parseArray(tagId);
                if (tagArray.contains(resourceTag.getId())) {
                    resourceCount++;
                    tempList.add(resource);
                }
            }
            resourceDTO.setResourceCount(resourceCount);
            resourceDTO.setResourceList(tempList);
            res.add(resourceDTO);
        }

        return res;
    }

    /**
     * ????????????
     *
     * @param searchVO
     * @return
     */
    public List<BaseQnaireResource> getResourceSearchList(QnaireResourceSearchVO searchVO) {
        Example example = new Example(BaseQnaireResource.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", ThreadLocalManager.getUserId());
        if (StringUtils.isNotBlank(searchVO.getKeyword())) {
            criteria.andLike("name", "%" + searchVO.getKeyword() + "%");
        }
        return resourceDao.selectByExample(example);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public List<BaseQnaireResourceTag> getResourceTags() {
        Example example = new Example(BaseQnaireResourceTag.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", ThreadLocalManager.getUserId());
        return resourceTagDao.selectByExample(example);
    }

    /**
     * ????????????
     *
     * @param file
     * @param type
     * @param fileName
     * @param tagIds
     * @return
     */
    public List<BaseQnaireResource> uploadResource(MultipartFile file, Integer type, String fileName, String tagIds) {
        // ??????check
        checkFile(file, type, fileName, tagIds, null);

        String fileTypeStr = this.getFileTypeStr(type);
        String filePathStr = "/resource/" + ThreadLocalManager.getUserId() + "/" + fileTypeStr;

        // ??????
        List<Map<String, Object>> resList = FileUtil.uploadFiles(new MultipartFile[]{file}, questionnaireDir + filePathStr);
        List<BaseQnaireResource> list = new ArrayList<>();
        if (resList.size() > 0) {
            // ??????????????????
            for (int i = 0; i < resList.size(); i++) {
                Map<String, Object> map = resList.get(i);
                BaseQnaireResource qnaireResource = new BaseQnaireResource();
                qnaireResource.setName(fileName);
                qnaireResource.setType(type);
                qnaireResource.setTagId(tagIds);// ????????????
                qnaireResource.setUserId(ThreadLocalManager.getUserId());
                // C:/Monetware/Common/ringsurvey/questionnaires/resource/??????ID/????????????/?????????
                String url = filePathStr + "/" + map.get("randomFileName").toString();
                qnaireResource.setUrl(url);
                int row = resourceDao.insertSelective(qnaireResource);
                if (row < 1) {
                    throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????");
                }
                list.add(qnaireResource);
            }
        } else {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????");
        }

        return list;
    }

    /**
     * ????????????
     *
     * @param file
     * @param type
     * @param fileName
     * @param tagIds
     * @return
     */
    public List<BaseQnaireResource> replaceResource(MultipartFile file, Integer type, String fileName, String tagIds, Integer sourceId) {
        BaseQnaireResource qnaireResource = resourceDao.selectByPrimaryKey(sourceId);
        if (null == qnaireResource) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????");
        }
        // ??????check
        checkFile(file, type, fileName, tagIds, sourceId);


        String fileTypeStr = this.getFileTypeStr(qnaireResource.getType());
        String filePathStr = "/resource/" + ThreadLocalManager.getUserId() + "/" + fileTypeStr;

        List<Map<String, Object>> resList = FileUtil.uploadFiles(new MultipartFile[]{file}, questionnaireDir + filePathStr);
        List<BaseQnaireResource> list = new ArrayList<>();
        if (resList.size() > 0) {
            // ??????????????????
            for (int i = 0; i < resList.size(); i++) {
                Map<String, Object> map = resList.get(i);
                BaseQnaireResource baseQnaireResource = new BaseQnaireResource();
                baseQnaireResource.setId(sourceId);
                baseQnaireResource.setName(fileName);
                baseQnaireResource.setType(type);
                baseQnaireResource.setTagId(tagIds);// ????????????
                baseQnaireResource.setUserId(ThreadLocalManager.getUserId());
                // C:/Monetware/Common/ringsurvey/questionnaires/resource/??????ID/????????????/?????????
                String url = filePathStr + "/" + map.get("randomFileName").toString();
                baseQnaireResource.setUrl(url);
                int row = resourceDao.updateByPrimaryKeySelective(baseQnaireResource);
                if (row < 1) {
                    throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????");
                }
                list.add(qnaireResource);
            }
        } else {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????");
        }
        return list;
    }

    /**
     * ????????????
     *
     * @param resource
     * @return
     */
    public int updateResource(BaseQnaireResource resource) {
        return resourceDao.updateByPrimaryKeySelective(resource);
    }

    /**
     * ????????????
     *
     * @param id
     * @return
     */
    public int deleteResource(Integer id) {
        BaseQnaireResource resource = resourceDao.selectByPrimaryKey(id);
        String fileTypeStr = this.getFileTypeStr(resource.getType());
        String filePathStr = questionnaireDir + "/resource/" + ThreadLocalManager.getUserId() + "/" + fileTypeStr;
        FileUtil.deleteFile(filePathStr, resource.getUrl().substring(resource.getUrl().lastIndexOf("/") + 1));
        return resourceDao.deleteByPrimaryKey(id);
    }

    /**
     * ??????????????????check
     *
     * @param file
     * @param type
     * @param fileName
     * @param tagIds
     * @param sourceId
     */
    private void checkFile(MultipartFile file, Integer type, String fileName, String tagIds, Integer sourceId) {
        if (null == file || file.isEmpty()) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????");
        }
        if (StringUtils.isBlank(file.getOriginalFilename()) || StringUtils.isBlank(fileName)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????");
        }
        if (StringUtils.isNotBlank(tagIds)) {
            JSONArray jsonArray = JSONArray.parseArray(tagIds);
            if (null == jsonArray) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????");
            }
        }
        // ????????????
        boolean isRepeat = this.checkFileNameRepeat(fileName, type, sourceId);
        if (isRepeat) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????");
        }
    }

    /**
     * ?????????????????????check
     *
     * @param fileName
     * @param type
     * @return
     */
    public boolean checkFileNameRepeat(String fileName, Integer type, Integer sourceId) {
        Example example = new Example(BaseQnaireResource.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", type);
        criteria.andEqualTo("name", fileName);
        criteria.andEqualTo("userId", ThreadLocalManager.getUserId());
        if (sourceId != null) {
            criteria.andNotEqualTo("id", sourceId);
        }
        return resourceDao.selectCountByExample(example) > 0;
    }

    /**
     * ?????????????????????check
     *
     * @param tagName
     * @return
     */
    public boolean checkTagNameRepeat(String tagName) {
        Example example = new Example(BaseQnaireResourceTag.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", tagName);
        criteria.andEqualTo("userId", ThreadLocalManager.getUserId());
        return resourceTagDao.selectCountByExample(example) > 0;
    }

    private String getFileTypeStr(Integer type) {
        String res;
        switch (type) {
            case 1:
                res = "image";
                break;
            case 2:
                res = "audio";
                break;
            case 3:
                res = "video";
                break;
            default:
                res = "others";
                break;
        }
        return res;
    }

    /**
     * ??????????????????
     *
     * @param tagVO
     * @return
     */
    public int addQnaireResourceTag(QnaireResourceTagVO tagVO) {
        boolean tagRepeat = this.checkTagNameRepeat(tagVO.getName());
        if (tagRepeat) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "?????????????????????");
        }
        BaseQnaireResourceTag qnaireResourceTag = new BaseQnaireResourceTag();
        qnaireResourceTag.setName(tagVO.getName());
        qnaireResourceTag.setUserId(ThreadLocalManager.getUserId());
        int row = resourceTagDao.insertSelective(qnaireResourceTag);
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????");
        }
        return row;
    }

    /**
     * ??????????????????
     *
     * @param tagId
     * @return
     */
    public int deleteQnaireResourceTag(Integer tagId) {
        // ????????????????????????????????????
        List<BaseQnaireResource> resources = this.getResourceSearchList(new QnaireResourceSearchVO());
        for (BaseQnaireResource resource : resources) {
            JSONArray tagIdArray = JSONArray.parseArray(resource.getTagId());
            if (tagIdArray.contains(tagId)) {
                tagIdArray.remove(tagId);
            }
            resource.setTagId(tagIdArray.toString());
            resourceDao.updateByPrimaryKeySelective(resource);
        }

        // ????????????
        Example example = new Example(BaseQnaireResourceTag.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", tagId);
        criteria.andEqualTo("userId", ThreadLocalManager.getUserId());
        int row = resourceTagDao.deleteByExample(example);
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????");
        }
        return row;
    }

    /**
     * ????????????
     *
     * @param file
     * @param
     * @return
     */
    public String uploadFile(MultipartFile file) {
        String pre = "/resource/" + ThreadLocalManager.getUserId() + "/image/";
        List<Map<String, Object>> res = FileUtil.uploadFiles(new MultipartFile[]{file}, questionnaireDir + pre);
        return pre + res.get(0).get("randomFileName");
    }

    /**
     * ????????????
     *
     * @param
     * @return
     */
    public Boolean deleteFile(String fileName) {
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        String pre = "/resource/" + ThreadLocalManager.getUserId() + "/image/";
        return FileUtil.deleteFile(questionnaireDir + pre, fileName);
    }

    /**
     * ????????????
     *
     * @param previewVO
     * @return
     */
    public QnairePreviewDTO previewQnaire(QnairePreviewVO previewVO) {
        QnairePreviewDTO previewDTO = new QnairePreviewDTO();
        String jsDir = null;
        if (previewVO.getFrom().equals(1)) {// ????????????
            BaseQuestionnaire baseQuestionnaire = questionnaireDao.selectByPrimaryKey(previewVO.getQuestionnaireId());
            previewDTO.setName(baseQuestionnaire.getName());
            previewDTO.setLogoUrl(baseQuestionnaire.getLogoUrl());
            previewDTO.setBgUrl(baseQuestionnaire.getBgUrl());
            jsDir = "/js/design/" + baseQuestionnaire.getId();
        } else {
            BaseProjectModule projectModule = moduleDao.selectByPrimaryKey(previewVO.getModuleId());
            previewDTO.setName(projectModule.getName());
            BaseProjectQuestionnaire projectQuestionnaire = questionnaireService.getProjectQuestionnaire(projectModule);
            if (null != projectQuestionnaire) {
                previewDTO.setLogoUrl(projectQuestionnaire.getLogoUrl());
                previewDTO.setBgUrl(projectQuestionnaire.getBgUrl());
            } else {
                BaseQuestionnaire baseQuestionnaire = JSONObject.parseObject(projectModule.getEditContent(), BaseQuestionnaire.class);
                if (null != baseQuestionnaire) {
                    previewDTO.setLogoUrl(baseQuestionnaire.getLogoUrl());
                    previewDTO.setBgUrl(baseQuestionnaire.getBgUrl());
                }
            }
            jsDir = "/js/" + projectModule.getProjectId() + "/" + projectModule.getCode() + "-" + projectModule.getMaxVersion();
        }

        previewDTO.setJsDir(jsDir);
        return previewDTO;
    }

    /**
     * ??????????????????
     * ??????????????????
     * ???????????????LOGO?????????
     *
     * @param myQuestionnaireVO
     */
    private void checkUserRole(MyQuestionnaireVO myQuestionnaireVO) {
        Integer userRole = ThreadLocalManager.getTokenContext().getNlpRole();
        int questionNum = myQuestionnaireVO.getQuestionNum();
        if (userRole.equals(UserConstants.ROLE_COMMON)) {
            if (questionNum > UserConstants.QUESTION_NUM_COMMON) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????????????????????????????");
            }
            if (StringUtils.isNotBlank(myQuestionnaireVO.getBgUrl())) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????????????????LOGO");
            }
            if (StringUtils.isNotBlank(myQuestionnaireVO.getLogoUrl())) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????????????????????????????????????????");
            }
        } else if (userRole.equals(UserConstants.ROLE_VIP)) {
            if (questionNum > UserConstants.QUESTION_NUM_VIP) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????????????????????????????");
            }
        }
    }

}
