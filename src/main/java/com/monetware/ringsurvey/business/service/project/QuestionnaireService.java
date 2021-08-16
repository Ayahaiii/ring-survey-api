package com.monetware.ringsurvey.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.ProjectConstants;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.*;
import com.monetware.ringsurvey.business.pojo.dto.response.ResponseListDTO;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.vo.qnaire.*;
import com.monetware.ringsurvey.business.pojo.vo.questionnaire.CheckStructureVO;
import com.monetware.ringsurvey.business.pojo.vo.response.ResponseListVO;
import com.monetware.ringsurvey.survml.SurvMLDocument;
import com.monetware.ringsurvey.survml.SurvMLDocumentParser;
import com.monetware.ringsurvey.survml.SurvMLUtils;
import com.monetware.ringsurvey.survml.compiler.SurvmlCompilerV2;
import com.monetware.ringsurvey.survml.exceptions.SurvMLParseException;
import com.monetware.ringsurvey.survml.questions.ListQuestion;
import com.monetware.ringsurvey.survml.questions.Question;
import com.monetware.ringsurvey.survml.questions.QuestionType;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.file.PDFUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
public class QuestionnaireService {

    @Value("${fileUrl.qnaire}")
    private String questionnaireDir;

    @Autowired
    private ProjectModuleDao moduleDao;

    @Autowired
    private ProjectQuestionnaireDao projectQuestionnaireDao;

    @Autowired
    private ProjectModuleGroupDao moduleGroupDao;

    @Autowired
    private ProjectModuleQuestionnaireDao moduleQuestionnaireDao;// 三表联合查询用

    @Autowired
    private QuestionnaireDao questionnaireDao;

    @Autowired
    private ProjectResponseDao responseDao;

    /**
     * 获取问卷编号 以及 主问卷模块
     *
     * @param importVO
     * @return
     */
    public ModuleImportDTO getImportModuleResult(ProjectModuleImportVO importVO) {
        List<String> moduleCodes = moduleQuestionnaireDao.getUsedModuleCode(importVO.getProjectId());
        BaseProjectModule module = new BaseProjectModule();
        module.setIsDelete(ProjectConstants.DELETE_NO);
        module.setProjectId(importVO.getProjectId());
        module.setType(1);
        List<BaseProjectModule> modules = moduleDao.select(module);
        ModuleImportDTO res = new ModuleImportDTO();
        res.setModuleCodes(moduleCodes);
        res.setModules(modules);
        if (importVO.getId() != null) {
            res.setModule(moduleDao.selectByPrimaryKey(importVO.getId()));
        }
        return res;
    }

    /**
     * 导出问卷
     *
     * @param id
     */
    public void exportQuestionnaire(Integer id, HttpServletResponse response) {
        BaseProjectQuestionnaire baseQuestionnaire = projectQuestionnaireDao.selectByPrimaryKey(id);
        try {
            SurvMLDocumentParser parse = new SurvMLDocumentParser(baseQuestionnaire.getXmlContent());
            SurvMLDocument qDocument = parse.parse();
            List<Question> questionList = qDocument.getQuestions();
            List<com.monetware.ringsurvey.survml.questions.Page> pages = qDocument.getPages();
            String logoUrl = baseQuestionnaire.getLogoUrl();
            PDFUtil.createQuestionnairePDF(qDocument.getTitle(), questionList, pages, 1, logoUrl, response);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "问卷导出失败");
        }
    }

    /**
     * 获取问卷列表
     *
     * @param qnaireSearchVO
     * @return
     */
    public PageList<Page> getProjectQnaireList(QnaireSearchVO qnaireSearchVO) {
        Page page = PageHelper.startPage(qnaireSearchVO.getPageNum(), qnaireSearchVO.getPageSize());
        questionnaireDao.getProjectQnaireList(qnaireSearchVO);
        return new PageList<>(page);
    }

    /**
     * 多问卷模块列表
     *
     * @param projectId
     * @return
     */
    public List<ProjectModuleListDTO> getProjectModuleQuestionnaireList(Integer projectId) {
        List<ProjectModuleListDTO> res = new ArrayList<>();
        // 处理更新者 我
        List<ProjectModuleQuestionnaireListDTO> list = moduleQuestionnaireDao.getProjectModuleQuestionnaireList(projectId);
        Map<String, List<ProjectModuleQuestionnaireListDTO>> tempMap = new HashMap<>();
        if (list.size() > 0) {
            for (ProjectModuleQuestionnaireListDTO t : list) {
                if (t.getUserId().equals(ThreadLocalManager.getUserId())) {
                    t.setUpdateUser("我");
                }
                if (tempMap.containsKey(t.getGroupName())) {
                    tempMap.get(t.getGroupName()).add(t);
                } else {
                    List<ProjectModuleQuestionnaireListDTO> temp = new ArrayList<>();
                    temp.add(t);
                    tempMap.put(t.getGroupName(), temp);
                }
                boolean hasQuestions = false;
                BaseQuestionnaire editQuestionnaire = JSONObject.parseObject(t.getEditContent(), BaseQuestionnaire.class);
                if (null != editQuestionnaire) {
                    if (null != editQuestionnaire.getQuestionNum() && editQuestionnaire.getQuestionNum() != 0) {
                        hasQuestions = true;
                    }
                } else {
                    if (null != t.getQnaireId()) {
                        if (null != t.getQuestionNum() && t.getQuestionNum() != 0) {
                            hasQuestions = true;
                        }
                    }
                }
                t.setHasQuestions(hasQuestions);
            }
            for (Map.Entry<String, List<ProjectModuleQuestionnaireListDTO>> m : tempMap.entrySet()) {
                ProjectModuleListDTO moduleListDTO = new ProjectModuleListDTO();
                moduleListDTO.setName(m.getKey());
                moduleListDTO.setModules(m.getValue());
                res.add(moduleListDTO);
            }
        }
        return res;
    }

    /**
     * 问卷配额题目选择下拉列表
     * 单选、多选、下拉单选
     */
    public List<QuestionSelectedDTO> getQuotaQuestionSelectedList(Integer projectId) {
        List<QuestionSelectedDTO> res = new ArrayList<>();
        List<ProjectModuleQuestionListDTO> questionList = moduleQuestionnaireDao.getProjectModuleQuestionList(projectId);
        for (ProjectModuleQuestionListDTO dto : questionList) {
            if (StringUtils.isBlank(dto.getXmlContent())) {
                continue;
            }
            QuestionSelectedDTO selectedDTO = new QuestionSelectedDTO();
            selectedDTO.setQid(dto.getQuestionnaireId());
            selectedDTO.setTitle(dto.getName() + "(版本" + dto.getVersion() + ")");
            // 问卷解析
            SurvMLDocument survMLDocument = null;
            try {
                survMLDocument = new SurvMLDocumentParser(dto.getXmlContent()).parse();
            } catch (SurvMLParseException e) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, e.getMessage());
            }
            // 题目
            List<Question> questions = survMLDocument.getAllSelectQuestions();

            List<HashMap> questionNews = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                ListQuestion listQuestion = (ListQuestion) questions.get(i);
                HashMap map = new HashMap();
                listQuestion.setDocument(null);
                map.put("name", listQuestion.getTitle());
                map.put("id", listQuestion.getId());
                List<HashMap> listItemMap = new ArrayList<>();
                if (listQuestion.getListItems() != null) {
                    for (int j = 0; j < listQuestion.getListItems().size(); j++) {
                        HashMap itemMap = new HashMap();
                        itemMap.put("id", listQuestion.getListItems().get(j).getId());
                        itemMap.put("name", listQuestion.getListItems().get(j).getName());
                        listItemMap.add(itemMap);
                    }
                }
                map.put("listItem", listItemMap);
                questionNews.add(map);
            }
            selectedDTO.setQuestions(questionNews);
            res.add(selectedDTO);
        }
        return res;
    }

    /**
     * 单选 多选 下拉 表格
     *
     * @param projectId
     * @return
     */
    public List<QuestionSelectedDTO> getQuotaQuestionSelectedAndMatrixList(Integer projectId) {
        List<QuestionSelectedDTO> res = new ArrayList<>();
        List<ProjectModuleQuestionListDTO> questionList = moduleQuestionnaireDao.getProjectModuleQuestionList(projectId);
        for (ProjectModuleQuestionListDTO dto : questionList) {
            QuestionSelectedDTO selectedDTO = new QuestionSelectedDTO();
            selectedDTO.setQid(dto.getQuestionnaireId());
            selectedDTO.setTitle(dto.getName() + "(版本" + dto.getVersion() + ")");
            // 问卷解析
            SurvMLDocument survMLDocument = null;
            try {
                survMLDocument = new SurvMLDocumentParser(dto.getXmlContent()).parse();
            } catch (SurvMLParseException e) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, e.getMessage());
            }
            // 题目
            List<Question> questions = new ArrayList<>();
            questions.addAll(survMLDocument.getAllSelectQuestions());
            questions.addAll(survMLDocument.getMatrixQuestions());
            List<HashMap> questionNews = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                HashMap map = new HashMap();
                question.setDocument(null);
                map.put("name", question.getTitle());
                map.put("id", question.getId());
                if (question.getQuestionType() != QuestionType.Matrix) {
                    ListQuestion listQuestion = (ListQuestion) question;
                    List<HashMap> listItemMap = new ArrayList<>();
                    if (listQuestion.getListItems() != null) {
                        for (int j = 0; j < listQuestion.getListItems().size(); j++) {
                            HashMap itemMap = new HashMap();
                            itemMap.put("id", listQuestion.getListItems().get(j).getId());
                            itemMap.put("name", listQuestion.getListItems().get(j).getName());
                            listItemMap.add(itemMap);
                        }
                    }
                    map.put("listItem", listItemMap);
                }
                questionNews.add(map);
            }
            selectedDTO.setQuestions(questionNews);
            res.add(selectedDTO);
        }
        return res;
    }


    /**
     * 项目问卷模块新增
     *
     * @param projectModuleVO
     */
    public void insertProjectModule(ProjectModuleVO projectModuleVO) {
        Date now = new Date();
        BaseProjectModule projectModule = new BaseProjectModule();
        BeanUtils.copyProperties(projectModuleVO, projectModule);
        BaseQuestionnaire baseQuestionnaire = null;
        if (projectModuleVO.getQuestionnaireId() != null) {
            baseQuestionnaire = questionnaireDao.selectByPrimaryKey(projectModuleVO.getQuestionnaireId());
        }
        int row = 0;
        if (projectModuleVO.getId() == null) {
            projectModule.setMaxVersion(1);
            projectModule.setLastModifyTime(now);
            projectModule.setStatus(ProjectConstants.QUESTIONNAIRE_STATUS_DRAFT);
            projectModule.setEditFlag(ProjectConstants.OPEN);
            projectModule.setLastModifyUser(ThreadLocalManager.getUserId());
            projectModule.setCreateUser(ThreadLocalManager.getUserId());
            projectModule.setCreateTime(now);
            projectModule.setIsDelete(ProjectConstants.DELETE_NO);
            row = moduleDao.insertSelective(projectModule);
            if (projectModuleVO.getQuestionnaireId() != null) {
                projectModule.setEditFlag(ProjectConstants.CLOSE);
                this.updateProjectModuleEdit(projectModule);
                this.insertProjectQuestionnaire(projectModule, baseQuestionnaire, "首次版本1生成");
            }
        } else {
            projectModule = moduleDao.selectByPrimaryKey(projectModuleVO.getId());
            projectModule.setCode(projectModuleVO.getCode());
            projectModule.setName(projectModuleVO.getName());
            projectModule.setType(projectModuleVO.getType());
            projectModule.setModuleDependency(projectModuleVO.getModuleDependency());
            projectModule.setSampleDependency(projectModuleVO.getSampleDependency());
            projectModule.setQuotaMin(projectModuleVO.getQuotaMin());
            projectModule.setQuotaMax(projectModuleVO.getQuotaMax());
            projectModule.setIsAllowedManualAdd(projectModuleVO.getIsAllowedManualAdd());
            projectModule.setGroupId(projectModuleVO.getGroupId());
            BaseProjectQuestionnaire projectQuestionnaire = this.getProjectQuestionnaire(projectModule);
            if (null == baseQuestionnaire) {
                baseQuestionnaire = new BaseQuestionnaire();
                BeanUtils.copyProperties(projectQuestionnaire, baseQuestionnaire);
            }
            projectModule.setEditContent(JSON.toJSONString(baseQuestionnaire));
            projectModule.setEditFlag(ProjectConstants.OPEN);
            projectModule.setLastModifyTime(now);
            projectModule.setLastModifyUser(ThreadLocalManager.getUserId());
            row = moduleDao.updateByPrimaryKeySelective(projectModule);
        }

        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目问卷保存失败");
        }
    }

    /**
     * 获取项目问卷信息
     *
     * @param id
     * @return
     */
    public BaseProjectQuestionnaire getProjectQuestionnaire(Integer id) {
        return projectQuestionnaireDao.selectByPrimaryKey(id);
    }

    public BaseProjectQuestionnaire getProjectQuestionnaire(BaseProjectModule projectModule) {
        Example e = new Example(BaseProjectQuestionnaire.class);
        Example.Criteria c = e.createCriteria();
        c.andEqualTo("moduleId", projectModule.getId());
        c.andEqualTo("projectId", projectModule.getProjectId());
        c.andEqualTo("version", projectModule.getMaxVersion());
        c.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
        return projectQuestionnaireDao.selectOneByExample(e);
    }

    /**
     * 修改问卷保存
     *
     * @param projectModule
     * @return
     */
    public int updateProjectModuleEdit(BaseProjectModule projectModule) {
        return moduleDao.updateByPrimaryKeySelective(projectModule);
    }

    /**
     * 更新
     *
     * @param updateVO
     * @return
     */
    public int updateProjectModule(QnaireUpdateVO updateVO) {
        Date now = new Date();
        BaseProjectModule module = moduleDao.selectByPrimaryKey(updateVO.getModuleId());
        // 编辑待更新的问卷
        BaseQuestionnaire editQuestionnaire = JSONObject.parseObject(module.getEditContent(), BaseQuestionnaire.class);
        // 当前项目问卷
        BaseProjectQuestionnaire oldProjectQnaire = this.getProjectQuestionnaire(module);
        if (null == oldProjectQnaire) {
            module.setName(editQuestionnaire.getName());
            this.insertProjectQuestionnaire(module, editQuestionnaire, updateVO.getRemark());
        } else {
            if (null != editQuestionnaire) {
                // 选择升级版本
                if (updateVO.getIfNewVersion() == 1) {
                    if (StringUtils.isBlank(oldProjectQnaire.getXmlContent())) {
                        this.updateProjectQuestionnaire(module, oldProjectQnaire, editQuestionnaire);
                    }
                    module.setName(editQuestionnaire.getName());
                    module.setMaxVersion(module.getMaxVersion() + 1);
                    this.insertProjectQuestionnaire(module, editQuestionnaire, updateVO.getRemark());
                } else {// 选择保留当前版本
                    module.setName(editQuestionnaire.getName());
                    this.updateProjectQuestionnaire(module, oldProjectQnaire, editQuestionnaire);
                }
            }
        }
        module.setEditContent("");
        module.setEditFlag(ProjectConstants.CLOSE);
        module.setLastModifyTime(now);
        module.setLastModifyUser(ThreadLocalManager.getUserId());
        return moduleDao.updateByPrimaryKeySelective(module);
    }

    // 项目问卷
    public int insertProjectQuestionnaire(BaseProjectModule projectModule, BaseQuestionnaire baseQuestionnaire, String remark) {
        BaseProjectQuestionnaire projectQuestionnaire = new BaseProjectQuestionnaire();
        projectQuestionnaire.setProjectId(projectModule.getProjectId());
        projectQuestionnaire.setModuleId(projectModule.getId());
        projectQuestionnaire.setVersion(projectModule.getMaxVersion());
        projectQuestionnaire.setRemark(remark);
        if (null != baseQuestionnaire) {
            projectQuestionnaire.setName(baseQuestionnaire.getName());
            projectQuestionnaire.setQuestionnaireId(baseQuestionnaire.getId());
            projectQuestionnaire.setXmlContent(baseQuestionnaire.getXmlContent());
            projectQuestionnaire.setQuestionNum(baseQuestionnaire.getQuestionNum());// 问题数量
            projectQuestionnaire.setPageNum(baseQuestionnaire.getPageNum());// 分页数量
            projectQuestionnaire.setWelcomeText(baseQuestionnaire.getWelcomeText());// 欢迎语
            projectQuestionnaire.setEndText(baseQuestionnaire.getEndText());// 结束语
            projectQuestionnaire.setLogoUrl(baseQuestionnaire.getLogoUrl());// 问卷logo
            projectQuestionnaire.setBgUrl(baseQuestionnaire.getBgUrl());// 背景图片
        }
        projectQuestionnaire.setCreateUser(ThreadLocalManager.getUserId());
        projectQuestionnaire.setCreateTime(new Date());
        projectQuestionnaire.setIsDelete(ProjectConstants.DELETE_NO);
        projectQuestionnaire.setLastModifyTime(new Date());
        projectQuestionnaire.setLastModifyUser(ThreadLocalManager.getUserId());
        boolean success = projectQuestionnaireDao.insertSelective(projectQuestionnaire) > 0;
        if (success && StringUtils.isNotBlank(projectQuestionnaire.getXmlContent())) {
            // 生成问卷js文件
            // C:/Monetware/Common/ringsurvey/questionnaires/js/项目ID/moduleCode-version/survey.js
            String filePathStr = questionnaireDir + "/js/" + projectModule.getProjectId()
                    + "/" + projectModule.getCode() + "-" + projectQuestionnaire.getVersion();
            this.createSurveyJS(filePathStr, projectQuestionnaire.getXmlContent());
        }
        return projectQuestionnaire.getId();
    }

    public void updateProjectQuestionnaire(BaseProjectModule projectModule, BaseProjectQuestionnaire nowProjectQnaire, BaseQuestionnaire baseQuestionnaire) {
        // 直接覆盖
        nowProjectQnaire.setQuestionnaireId(baseQuestionnaire.getId());
        nowProjectQnaire.setName(baseQuestionnaire.getName());
        nowProjectQnaire.setVersion(projectModule.getMaxVersion());
        nowProjectQnaire.setXmlContent(baseQuestionnaire.getXmlContent());
        nowProjectQnaire.setQuestionNum(baseQuestionnaire.getQuestionNum());// 问题数量
        nowProjectQnaire.setPageNum(baseQuestionnaire.getPageNum());// 分页数量
        nowProjectQnaire.setWelcomeText(baseQuestionnaire.getWelcomeText());// 欢迎语
        nowProjectQnaire.setEndText(baseQuestionnaire.getEndText());// 结束语
        nowProjectQnaire.setLogoUrl(baseQuestionnaire.getLogoUrl());// 问卷logo
        nowProjectQnaire.setBgUrl(baseQuestionnaire.getBgUrl());// 背景图片
        nowProjectQnaire.setLastModifyUser(ThreadLocalManager.getUserId());
        nowProjectQnaire.setLastModifyTime(new Date());
        boolean success = projectQuestionnaireDao.updateByPrimaryKeySelective(nowProjectQnaire) > 0;
        if (success) {
            String filePathStr = questionnaireDir + "/js/" + projectModule.getProjectId()
                    + "/" + projectModule.getCode() + "-" + nowProjectQnaire.getVersion();
            this.createSurveyJS(filePathStr, nowProjectQnaire.getXmlContent());
        }
    }

    /**
     * 项目问卷发布、取消发布
     *
     * @param projectId
     * @param moduleId
     * @param status
     * @return
     */
    public int handleProjectQuestionnaire(Integer projectId, Integer moduleId, Integer status) {
        BaseProjectModule projectModule = new BaseProjectModule();
        projectModule.setId(moduleId);
        projectModule.setProjectId(projectId);
        projectModule.setStatus(status);
        projectModule.setLastModifyUser(ThreadLocalManager.getUserId());
        projectModule.setLastModifyTime(new Date());
        return moduleDao.updateByPrimaryKeySelective(projectModule);
    }

    /**
     * 撤销问卷模块
     *
     * @param projectId
     * @param moduleId
     * @return
     */
    public int cancelProjectModule(Integer projectId, Integer moduleId) {
        BaseProjectModule projectModule = moduleDao.selectByPrimaryKey(moduleId);
        BaseProjectQuestionnaire projectQuestionnaire = this.getProjectQuestionnaire(projectModule);
        if (null == projectQuestionnaire) {
            return moduleDao.delete(projectModule);
        } else {
            BaseQuestionnaire editQuestionnaire = JSONObject.parseObject(projectModule.getEditContent(), BaseQuestionnaire.class);
            if (null == editQuestionnaire) {
                return moduleDao.delete(projectModule);
            } else {
                projectModule.setName(projectQuestionnaire.getName());
                projectModule.setEditContent("");
                projectModule.setEditFlag(ProjectConstants.CLOSE);
                projectModule.setLastModifyUser(ThreadLocalManager.getUserId());
                projectModule.setLastModifyTime(new Date());
                // 修改survey.js
                String filePathStr = questionnaireDir + "/js/" + projectModule.getProjectId()
                        + "/" + projectModule.getCode() + "-" + projectModule.getMaxVersion();
                this.createSurveyJS(filePathStr, projectQuestionnaire.getXmlContent());
                return moduleDao.updateByPrimaryKeySelective(projectModule);
            }
        }
    }

    /**
     * 项目问卷模块删除
     *
     * @return
     */
    public int deleteProjectModule(QnaireUpdateVO updateVO) {

        // 问卷模块
        Example example = new Example(BaseProjectModule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", updateVO.getModuleId());
        criteria.andEqualTo("projectId", updateVO.getProjectId());
        BaseProjectModule projectModule = moduleDao.selectOneByExample(example);

        // 当前模块是否存在答卷 若有答卷，不能删除
        Example e = new Example(BaseProjectQuestionnaire.class);
        Example.Criteria c = e.createCriteria();
        c.andEqualTo("moduleId", projectModule.getId());
        c.andEqualTo("projectId", projectModule.getProjectId());
        c.andEqualTo("version", projectModule.getMaxVersion());
        BaseProjectQuestionnaire projectQuestionnaire = projectQuestionnaireDao.selectOneByExample(e);

        ResponseListVO responseListVO = new ResponseListVO();
        if (null != projectQuestionnaire) {
            responseListVO.setQuestionnaireId(projectQuestionnaire.getId());
        }
        responseListVO.setProjectId(updateVO.getProjectId());
        List<ResponseListDTO> responses = responseDao.getResponseList(responseListVO);
        if (responses != null && !responses.isEmpty()) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "当前问卷已有答卷");
        }

        // 项目问卷模块删除(逻辑删除)
        projectModule.setIsDelete(ProjectConstants.DELETE_YES);
        projectModule.setDeleteUser(ThreadLocalManager.getUserId());
        projectModule.setDeleteTime(new Date());
        return moduleDao.updateByPrimaryKeySelective(projectModule);
    }

    /**
     * 获取项目问卷模块分组
     *
     * @param projectId
     * @return
     */
    public List<ProjectModuleGroupDTO> getProjectModuleGroupList(Integer projectId) {
        return moduleGroupDao.getProjectModuleGroupList(projectId);
    }

    /**
     * 保存项目问卷模块的分组
     *
     * @param moduleGroupVO
     */
    public int saveModuleGroup(ProjectModuleGroupVO moduleGroupVO) {
        Date now = new Date();
        BaseProjectModuleGroup projectModuleGroup = new BaseProjectModuleGroup();
        BeanUtils.copyProperties(moduleGroupVO, projectModuleGroup);
        int row;
        if (null == projectModuleGroup.getId()) {
            projectModuleGroup.setCreateUser(ThreadLocalManager.getUserId());
            projectModuleGroup.setCreateTime(now);
            row = moduleGroupDao.insertSelective(projectModuleGroup);
        } else {
            projectModuleGroup.setLastModifyTime(now);
            projectModuleGroup.setLastModifyUser(ThreadLocalManager.getUserId());
            row = moduleGroupDao.updateByPrimaryKeySelective(projectModuleGroup);
        }
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "问卷分组保存失败");
        }
        return row;
    }

    /**
     * 删除问卷分组
     *
     * @param moduleGroupVO
     * @return
     */
    public int deleteModuleGroup(ProjectModuleGroupVO moduleGroupVO) {
        // 先删除问卷与分组的关联
        moduleGroupDao.deleteModule2Group(moduleGroupVO.getProjectId(), moduleGroupVO.getId());

        // 再删除(物理删除)
        Example example = new Example(BaseProjectModuleGroup.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", moduleGroupVO.getId());
        criteria.andEqualTo("projectId", moduleGroupVO.getProjectId());
        return moduleGroupDao.deleteByExample(example);
    }

    public void createSurveyJS(String filePathStr, String xmlContent) {
        File filePath = new File(filePathStr);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        try {
            SurvmlCompilerV2.compile(IOUtils.toInputStream(xmlContent, "UTF-8"), filePathStr + "/", "survey", filePathStr);
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "问卷保存失败");
        }
    }

    /**
     * 历史版本列表
     *
     * @param searchVO
     * @return
     */
    public PageList<Page> getHistoryList(QnaireSearchVO searchVO) {
        Page page = PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<ProjectModuleHistoryDTO> list = moduleQuestionnaireDao.getProjectModuleHistoryList(searchVO);
        if (list.size() > 0) {
            for (ProjectModuleHistoryDTO historyDTO : list) {
                if (historyDTO.getCreateUser().equals(ThreadLocalManager.getUserId())) {
                    historyDTO.setCreateUserStr("我");
                }
            }
        }
        return new PageList<>(page);
    }

    /**
     * 获取项目问卷详情
     *
     * @param projectId
     * @return
     */
    public ProjectModuleDTO getProjectModuleInfo(Integer projectId) {
        ProjectModuleDTO res = moduleQuestionnaireDao.getProjectModuleInfo(projectId);
        res.setJsDir("js/" + projectId + "/" + res.getCode() + "-" + res.getVersion());
        return res;
    }

    public BaseProjectModule getProjectModule(Integer projectId, Integer moduleId) {
        Example example = new Example(BaseProjectModule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", moduleId);
        criteria.andEqualTo("projectId", projectId);
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
        return moduleDao.selectOneByExample(example);
    }

    public List<BaseProjectModule> getProjectModuleList(Integer projectId) {
        Example example = new Example(BaseProjectModule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
        return moduleDao.selectByExample(example);
    }

    /**
     * 检查问卷结构
     *
     * @param checkStructureVO
     * @return
     */
    public boolean checkQuestionStructure(CheckStructureVO checkStructureVO) {
        Integer projectId = checkStructureVO.getProjectId();
        BaseProjectModule projectModule = this.getProjectModule(projectId, checkStructureVO.getModuleId());
        BaseProjectQuestionnaire projectQuestionnaire = this.getProjectQuestionnaire(projectModule);
        if (null == projectQuestionnaire) {
            return false;
        }
        BaseQuestionnaire questionnaire = JSONObject.parseObject(projectModule.getEditContent(), BaseQuestionnaire.class);
        if (null == questionnaire) {
            return false;
        }
        boolean flag = true;
        try {
            SurvMLDocument oldDocument = new SurvMLDocumentParser(projectQuestionnaire.getXmlContent()).parse();
            SurvMLDocument newDocument = new SurvMLDocumentParser(questionnaire.getXmlContent()).parse();
            flag = SurvMLUtils.compareSurvML(oldDocument, newDocument);
        } catch (SurvMLParseException e) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, e.getMessage());
        }

        return flag;
    }

}
