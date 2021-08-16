package com.monetware.ringsurvey.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.*;
import com.monetware.ringsurvey.business.pojo.dto.project.*;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionnaireParseDTO;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.vo.project.*;
import com.monetware.ringsurvey.business.pojo.vo.sample.SampleUpdateVO;
import com.monetware.ringsurvey.business.service.auth.AuthService;
import com.monetware.ringsurvey.business.service.user.UserService;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.codec.UrlUtil;
import com.monetware.ringsurvey.system.util.date.DateUtil;
import com.monetware.ringsurvey.system.util.redis.RedisUtil;
import com.monetware.ringsurvey.system.util.survml.SurvmlUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2020-03-04
 */
@Slf4j
@Service
public class ProjectService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectSampleDao sampleDao;

    @Autowired
    private ProjectSampleAssignDao assignDao;

    @Autowired
    private ProjectResponseDao responseDao;

    @Autowired
    private ProjectResponseFileDao responseFileDao;

    @Autowired
    private ProjectResponseAudioDao responseAudioDao;

    @Autowired
    private ProjectResponseHistoryDao responseHistoryDao;

    @Autowired
    private RedPacketRecordDao redPacketRecordDao;

    @Autowired
    private ProjectPublishLogDao publishLogDao;

    @Autowired
    private ProjectPublishQueueDao publishQueueDao;

    @Autowired
    private ProjectResponseAuditDao auditDao;

    @Autowired
    private ProjectResponsePositionDao responsePositionDao;

    @Autowired
    private ProjectSampleAddressDao addressDao;

    @Autowired
    private ProjectSampleContactDao contactDao;

    @Autowired
    private ProjectSampleTouchDao touchDao;

    @Autowired
    private ProjectTeamUserRoleDao teamUserRoleDao;

    @Autowired
    private ProjectTeamUserDao teamUserDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ProjectPermissionDao permissionDao;

    @Autowired
    private ProjectPropertyDao propertyDao;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private InterviewerTravelDao travelDao;

    @Autowired
    private AuthService authService;

    @Value("${outurl.authGetConfigUrl}")
    private String authGetConfigUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectSamplePoolDao samplePoolDao;

    /**
     * 创建项目
     *
     * @param projectVO
     */
    public void createProject(ProjectVO projectVO) {
        // 判断用户角色
        checkUserRole(projectVO);

        Date now = new Date();
        BaseProject project = new BaseProject();
        BeanUtils.copyProperties(projectVO, project);
        // 添加项目初始参数
        BaseProjectConfig config = new BaseProjectConfig();
        if (projectVO.getType().equals(ProjectConstants.PROJECT_TYPE_CAPI) || projectVO.getType().equals(ProjectConstants.PROJECT_TYPE_CAXI)) {
            config.setInterviewerSaveSample(ProjectConstants.OPEN);
            config.setResponseAudio(ProjectConstants.OPEN);
            config.setResponsePosition(ProjectConstants.OPEN);
        } else if (projectVO.getType().equals(ProjectConstants.PROJECT_TYPE_CATI) || projectVO.getType().equals(ProjectConstants.PROJECT_TYPE_CAXI)) {
            config.setResponseAudio(ProjectConstants.OPEN);
            config.setSampleAssignType(2);// 动态分配
            config.setMoreSampleInfo(ProjectConstants.OPEN);// 更多信息(更多地址/联系方式/接触记录)
            config.setIfOpenQuota(ProjectConstants.OPEN);
            config.setIfRandomQuota(ProjectConstants.OPEN);
        }
        if (projectVO.getType().equals(ProjectConstants.PROJECT_TYPE_CAWI)) {
            config.setIfOpenSample(ProjectConstants.CLOSE);
        }
        config.setMultipleQuestionnaire(projectVO.getQnaireType());
        // 项目结束语
        config.setEndText("感谢您的参与！由于问卷数量已达到设计需求，本次问卷调查活动已截止。再次谢谢您的理解与支持，祝您工作顺利，生活愉快！");
        project.setConfig(JSON.toJSONString(config));
        // 添加邀请码
        project.setInviteCode(getCode());
        project.setCodeAutoAudit(ProjectConstants.CODE_AUDIT_AUTO);
        // 邀请码默认7天过期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        project.setCodeExpireTime(calendar.getTime());
        // 发布码
        project.setPublishCode(UrlUtil.getShortUrl());
        // 文件存储空间
        project.setNumOfSample(ProjectConstants.INIT_VALUE);
        project.setNumOfTeam(1);
        project.setFileSize(ProjectConstants.INIT_VALUE.longValue());
        project.setNumOfAnswer(ProjectConstants.INIT_VALUE);
        project.setAnswerTimeLen(ProjectConstants.INIT_VALUE.longValue());
        project.setCreateUser(ThreadLocalManager.getUserId());
        project.setCreateTime(now);
        project.setLastModifyUser(ThreadLocalManager.getUserId());
        project.setLastModifyTime(now);
        project.setIsDelete(ProjectConstants.DELETE_NO);
        project.setStatus(ProjectConstants.STATUS_WAIT);
        int row = projectDao.insertSelective(project);
        if (row > 0) {
            // 缓存项目相关信息
            ProjectConfigDTO configDTO = new ProjectConfigDTO();
            BeanUtils.copyProperties(config, configDTO);
            configDTO.setType(project.getType());
            String role = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId()).getRole().toString();
            configDTO.setRole(role);
            redisUtil.hset(RedisKeyConstants.projectKey(project.getId()), RedisKeyConstants.projectConfigKey(project.getId()), configDTO);
            redisUtil.hset(RedisKeyConstants.projectKey(project.getId()),
                    RedisKeyConstants.projectRoleKey(project.getId()), role);
            // 样本表
            sampleDao.createCustomTable(ProjectConstants.getSampleTableName(project.getId()));
            // 分派表
            assignDao.createCustomTable(ProjectConstants.getSampleAssignmentTableName(project.getId()));
            // 添加系统默认样本属性
            BaseProjectProperty baseSampleProperty = new BaseProjectProperty();
            baseSampleProperty.setProjectId(project.getId());
            BaseProjectAllProperty allProperty = new BaseProjectAllProperty();
            baseSampleProperty.setAllProperty(JSON.toJSONString(allProperty));
            // 默认样本属性
            String useProps = null;
            String listProps = null;
            String markProps = null;
            if (project.getType().equals(ProjectConstants.PROJECT_TYPE_CADI) || project.getType().equals(ProjectConstants.PROJECT_TYPE_CATI)) {
                useProps = "[\"name\", \"code\", \"phone\", \"address\"]";
                listProps = "[\"name\", \"code\", \"phone\", \"address\"]";
                markProps = "[\"name\", \"code\", \"phone\", \"address\"]";
            } else if (project.getType().equals(ProjectConstants.PROJECT_TYPE_CAWI) || project.getType().equals(ProjectConstants.PROJECT_TYPE_CAXI)) {
                useProps = "[\"name\", \"code\", \"phone\", \"address\", \"email\"]";
                listProps = "[\"name\", \"code\", \"phone\", \"address\", \"email\"]";
                markProps = "[\"name\", \"code\", \"phone\", \"address\", \"email\"]";
            } else {
                useProps = "[\"name\", \"code\"]";
                listProps = "[\"name\", \"code\"]";
                markProps = "[\"name\", \"code\"]";
            }
            baseSampleProperty.setUseProperty(useProps);
            baseSampleProperty.setListProperty(listProps);
            baseSampleProperty.setMarkProperty(markProps);
            baseSampleProperty.setCreateUser(ThreadLocalManager.getUserId());
            baseSampleProperty.setCreateTime(now);
            propertyDao.insertSelective(baseSampleProperty);
            // 答卷表
            responseDao.createCustomTable(ProjectConstants.getResponseTableName(project.getId()));
            // 答卷文件表
            responseFileDao.createCustomTable(ProjectConstants.getResponseFileTableName(project.getId()));
            // 答卷审核表
            auditDao.createCustomTable(ProjectConstants.getResponseAuditTableName(project.getId()));
            // 将自己添加到团队
            BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
            BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
            teamUser.setProjectId(project.getId());
            teamUser.setUserId(ThreadLocalManager.getUserId());
            teamUser.setUserName(user.getName());
            teamUser.setName(user.getName());
            teamUser.setEmail(user.getEmail());
            teamUser.setTelephone(user.getTelephone());
            teamUser.setSampleAuth(ProjectConstants.SAMPLE_TYPE_ALL);
            teamUser.setAuthType(ProjectConstants.AUTH_TYPE_FOREVER);
            teamUser.setApproveUser(ThreadLocalManager.getUserId());
            teamUser.setApproveTime(now);
            teamUser.setApplyTime(now);
            teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
            teamUserDao.insert(teamUser);
            BaseProjectTeamUserRole userRole = new BaseProjectTeamUserRole();
            userRole.setProjectId(project.getId());
            userRole.setUserId(ThreadLocalManager.getUserId());
            userRole.setRoleId(AuthorizedConstants.ROLE_ADMIN);
            teamUserRoleDao.insert(userRole);
            // 面访
            if (project.getType().equals("CAPI") || project.getType().equals("CAXI")) {
                // 答卷录音表
                responseAudioDao.createCustomTable(ProjectConstants.getResponseAudioTableName(project.getId()));
                // 答卷定位表
                responsePositionDao.createCustomTable(ProjectConstants.getResponsePositionTableName(project.getId()));
            }
            // 电访
            if (project.getType().equals("CATI") || project.getType().equals("CAXI")) {
                // 答卷录音表
                responseAudioDao.createCustomTable(ProjectConstants.getResponseAudioTableName(project.getId()));
                // 地址表 接触记录表 联系表
                addressDao.createCustomTable(ProjectConstants.getSampleAddressTableName(project.getId()));
                contactDao.createCustomTable(ProjectConstants.getSampleContactsTableName(project.getId()));
                touchDao.createCustomTable(ProjectConstants.getSampleTouchTableName(project.getId()));
                redisUtil.hset(RedisKeyConstants.projectKey(project.getId()), "dial_type", ProjectConstants.DIAL_MANUAL);// 拨号方式
                redisUtil.hset(RedisKeyConstants.projectKey(project.getId()), "get_sample_type", ProjectConstants.GET_SAMPLE_AUTO);// 获取样本方式
            }
        } else {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目创建失败");
        }
    }

    /**
     * 获取项目基本数据
     *
     * @param projectId
     * @return
     */
    public ProjectInfoDTO getProjectInfo(Integer projectId) {
        Integer currentUserId = ThreadLocalManager.getUserId();
        ProjectInfoDTO projectInfoDTO = projectDao.getProjectInfo(projectId, currentUserId);
        if (projectInfoDTO != null) {
            if (projectInfoDTO.getCreateUser().equals(currentUserId)) {
                projectInfoDTO.setCreateUserStr("我");
            }
        }
        return projectInfoDTO;
    }

    /**
     * 获取项目基本信息
     *
     * @param projectId
     * @return
     */
    public ProjectHeadDTO getProjectNameAndRole(Integer projectId) {
        ProjectHeadDTO projectHeadDTO = projectDao.getProjectNameAndRole(projectId, ThreadLocalManager.getUserId());
        projectHeadDTO.setConfig(this.getProjectConfig(projectId));
        return projectHeadDTO;
    }

    /**
     * 修改数据
     *
     * @param projectVO
     * @return
     */
    public int updateProject(ProjectVO projectVO) {
        Date now = new Date();
        BaseProject project = new BaseProject();
        BeanUtils.copyProperties(projectVO, project);
        project.setLastModifyUser(ThreadLocalManager.getUserId());
        project.setLastModifyTime(now);
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 更改项目状态
     *
     * @param statusVO
     * @return
     */
    public int updateProjectStatus(ProjectStatusVO statusVO) {
        BaseProject project = new BaseProject();
        BeanUtils.copyProperties(statusVO, project);
        Date now = new Date();
        if (ProjectConstants.STATUS_WAIT.equals(statusVO.getOldStatus()) && ProjectConstants.STATUS_RUN.equals(statusVO.getStatus())) {
            project.setBeginDate(now);
        } else if (ProjectConstants.STATUS_PAUSE.equals(statusVO.getStatus())) {
            project.setPauseTime(now);
        } else if (ProjectConstants.STATUS_FINISH.equals(statusVO.getStatus())) {
            project.setEndDate(now);
        }
        project.setLastModifyTime(now);
        project.setLastModifyUser(ThreadLocalManager.getUserId());
        if (ProjectConstants.STATUS_RUN.equals(statusVO.getStatus())) {
            // 检查项目是否有问卷
            List<BaseProjectModule> moduleList = questionnaireService.getProjectModuleList(statusVO.getId());
            if (null == moduleList || moduleList.isEmpty()) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "没有问卷不能启动项目");
            } else {
                // 检查
                for (int i = 0; i < moduleList.size(); i++) {
                    BaseProjectModule module = moduleList.get(i);
                    BaseProjectQuestionnaire pq = questionnaireService.getProjectQuestionnaire(module);
                    if (null == pq || StringUtils.isBlank(pq.getXmlContent())) {
                        throw new ServiceException(ErrorCode.CUSTOM_MSG, "[" + module.getName() + "]问卷尚未添加任何题目");
                    } else {
                        // 解析pq的xml
                        QuestionnaireParseDTO parseDTO = SurvmlUtil.getQuestionsInfoByXml(pq.getXmlContent());
                        if (null == parseDTO.getQuestions() || parseDTO.getQuestions().isEmpty()) {
                            throw new ServiceException(ErrorCode.CUSTOM_MSG, "[" + module.getName() + "]问卷尚未添加任何题目");
                        }
                    }
                }
            }
        }
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 删除项目
     *
     * @param projectId
     */
    public Integer deleteProject(Integer projectId) {
        BaseProject project = new BaseProject();
        project.setIsDelete(ProjectConstants.DELETE_YES);
        project.setDeleteTime(new Date());
        Example example = new Example(BaseProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        criteria.andEqualTo("id", projectId);
        return projectDao.updateByExampleSelective(project, example);
    }

    /**
     * 项目列表
     *
     * @param projectListVO
     * @return
     */
    public PageList<Page> getProjectList(ProjectListVO projectListVO) {
        Date now = new Date();
        Page page = PageHelper.startPage(projectListVO.getPageNum(), projectListVO.getPageSize());
        projectListVO.setUserId(ThreadLocalManager.getUserId());
        List<ProjectListDTO> dtoList = projectDao.getProjectList(projectListVO);
        if (null != dtoList && dtoList.size() != 0) {
            for (ProjectListDTO projectListDTO : dtoList) {
                if (projectListDTO.getCreateUser().equals(ThreadLocalManager.getUserId())) {
                    projectListDTO.setRole("ALL");
                    projectListDTO.setUserName("我");
                }
                Long ct = DateUtil.getDateDuration(projectListDTO.getCreateTime(), now);
                if (ct < 7 * 24 * 60 * 60L) {
                    projectListDTO.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(ct));
                }
                Long ut = DateUtil.getDateDuration(projectListDTO.getUpdateTime(), now);
                if (ut < 7 * 24 * 60 * 60L) {
                    projectListDTO.setUpdateTimeStr(DateUtil.secondToHourChineseStrByLong(ut));
                }
                if (null == projectListDTO.getIfOpenSample() && !projectListDTO.getType().equals(ProjectConstants.PROJECT_TYPE_CAWI)) {
                    projectListDTO.setIfOpenSample(ProjectConstants.OPEN);
                }
            }
        }
        return new PageList<>(page);
    }

    /**
     * 项目列表（TestController用）
     *
     * @return
     */
    public PageList<Page> getProjectListTest() {
        Date now = new Date();
        BaseUser testUser = userService.getTestUser();
        Page page = PageHelper.startPage(1, 10);
        List<ProjectListDTO> dtoList = projectDao.getProjectListTest(testUser.getId());
        if (null != dtoList && dtoList.size() != 0) {
            for (ProjectListDTO projectListDTO : dtoList) {
                if (projectListDTO.getCreateUser().equals(testUser.getId())) {
                    projectListDTO.setRole("ALL");
                    projectListDTO.setUserName("我");
                }
                Long ct = DateUtil.getDateDuration(projectListDTO.getCreateTime(), now);
                if (ct < 7 * 24 * 60 * 60L) {
                    projectListDTO.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(ct));
                }
                Long ut = DateUtil.getDateDuration(projectListDTO.getUpdateTime(), now);
                if (ut < 7 * 24 * 60 * 60L) {
                    projectListDTO.setUpdateTimeStr(DateUtil.secondToHourChineseStrByLong(ut));
                }
                if (null == projectListDTO.getIfOpenSample() && !projectListDTO.getType().equals(ProjectConstants.PROJECT_TYPE_CAWI)) {
                    projectListDTO.setIfOpenSample(ProjectConstants.OPEN);
                }
            }
        }
        return new PageList<>(page);
    }

    /**
     * 获取用户下所有未删除项目
     *
     * @param userId
     * @return
     */
    public List<BaseProject> getProjectListByUserId(Integer userId) {
        Example example = new Example(BaseProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createUser", userId);
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
        return projectDao.selectByExample(example);
    }

    /**
     * 获取回收站项目
     *
     * @param deleteVO
     * @return
     */
    public PageList<Page> getDeleteProject(ProjectDeleteVO deleteVO) {
        Page page = PageHelper.startPage(deleteVO.getPageNum(), deleteVO.getPageSize());
        Example example = new Example(BaseProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_YES);
        if (StringUtils.isNotBlank(deleteVO.getKeyword())) {
            criteria.andLike("name", "%" + deleteVO.getKeyword() + "%");
        }
        projectDao.selectByExample(example);
        return new PageList<>(page);
    }

    ;

    /**
     * 获取六位邀请码
     *
     * @return
     */
    private String getCode() {
        String code = "";
        for (int i = 0; i < 6; i++) {
            boolean boo = (int) (Math.random() * 2) == 0;
            if (boo) {
                code += String.valueOf((int) (Math.random() * 10));
            } else {
                int temp = (int) (Math.random() * 2) == 0 ? 65 : 97;
                char ch = (char) (Math.random() * 26 + temp);
                code += String.valueOf(ch);
            }
        }
        BaseProject project = new BaseProject();
        project.setInviteCode(code);
        if (projectDao.selectOne(project) != null) {
            getCode();
        }
        return code;
    }

    /**
     * 申请加入项目
     *
     * @param projectApplyVO
     * @return
     */
    public int insertApplyTeam(ProjectApplyVO projectApplyVO) {
        Date now = new Date();
        // 判断code码是否正确并且是否有效
        BaseProject checkProject = new BaseProject();
        checkProject.setInviteCode(projectApplyVO.getInviteCode());
        BaseProject project = projectDao.selectOne(checkProject);
        if (project == null) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目不存在，邀请码有误");
        }
        if (project.getCreateUser().equals(ThreadLocalManager.getUserId())) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "不能申请自己的项目");
        }
        if (now.after(project.getCodeExpireTime())) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "邀请码已过期");
        }
        // TODO 判断项目团队成员数量是否已达上限

        // 判断用户是否已加入
        Example example = new Example(BaseProjectTeamUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", ThreadLocalManager.getUserId());
        criteria.andEqualTo("projectId", project.getId());
        BaseProjectTeamUser checkTeamUser = teamUserDao.selectOneByExample(example);
        if (checkTeamUser != null) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "您已加入项目");
        }
        // 添加团队用户信息
        BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
        BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
        teamUser.setUserId(ThreadLocalManager.getUserId());
        teamUser.setUserName(user.getName());
        teamUser.setName(user.getName());
        teamUser.setEmail(user.getEmail());
        teamUser.setTelephone(user.getTelephone());
        teamUser.setSampleAuth(ProjectConstants.SAMPLE_TYPE_ALL);
        teamUser.setApplyTime(now);
        teamUser.setProjectId(project.getId());
        teamUser.setAuthType(ProjectConstants.AUTH_TYPE_FOREVER);
        // 是否自动审核
        if (project.getCodeAutoAudit() == ProjectConstants.CODE_AUDIT_AUTO) {
            teamUser.setApproveTime(now);
            teamUser.setApproveUser(project.getCreateUser());
            teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
        } else {
            teamUser.setStatus(ProjectConstants.USER_STATUS_WAIT);
        }
        // 初始化角色
        BaseProjectTeamUserRole teamUserRole = new BaseProjectTeamUserRole();
        teamUserRole.setProjectId(project.getId());
        teamUserRole.setUserId(ThreadLocalManager.getUserId());
        teamUserRole.setRoleId(AuthorizedConstants.ROLE_INTERVIEWER);
        teamUserRoleDao.insert(teamUserRole);
        int row = teamUserDao.insertSelective(teamUser);
        if (row > 0) {
            // 回写团队总数
            BaseProject p = new BaseProject();
            p.setNumOfTeam(row);
            p.setLastModifyUser(ThreadLocalManager.getUserId());
            p.setLastModifyTime(now);
            p.setId(project.getId());
            projectDao.updateProjectAdd(p);
        }
        return row;
    }

    /**
     * 获取项目配置
     *
     * @param projectId
     * @return
     */
    public ProjectConfigsDTO getProjectConfig(Integer projectId) {
        // 从redis中读取
        ProjectConfigsDTO configDTO = new ProjectConfigsDTO();
        BaseProject project = projectDao.selectByPrimaryKey(projectId);
        ProjectConfigDTO configDto = new ProjectConfigDTO();
        if (redisUtil.hexist(RedisKeyConstants.projectKey(projectId), RedisKeyConstants.projectConfigKey(projectId))) {
            configDto = (ProjectConfigDTO) redisUtil.hget(RedisKeyConstants.projectKey(projectId), RedisKeyConstants.projectConfigKey(projectId));
        } else {
            BaseProjectConfig config = JSONObject.parseObject(project.getConfig(), BaseProjectConfig.class);
            BeanUtils.copyProperties(config, configDto);
            configDto.setType(project.getType());
            String role = userDao.selectByPrimaryKey(project.getCreateUser()).getRole().toString();
            configDto.setRole(role);
            // 更新redis缓存
            redisUtil.hset(RedisKeyConstants.projectKey(projectId), RedisKeyConstants.projectConfigKey(projectId), configDto);
        }
        BeanUtils.copyProperties(configDto, configDTO);
        configDTO.setStatus(project.getStatus());
        if (null == configDTO.getIfOpenSample()) {
            if (project.getType().equals(ProjectConstants.PROJECT_TYPE_CAWI)) {
                configDTO.setIfOpenSample(ProjectConstants.CLOSE);
            } else {
                configDTO.setIfOpenSample(ProjectConstants.OPEN);
            }
        }
        return configDTO;
    }

    /**
     * 修改项目配置
     *
     * @param projectConfigVO
     * @return
     */
    public int updateProjectConfig(ProjectConfigVO projectConfigVO) {
        BaseProjectConfig config = new BaseProjectConfig();
        BeanUtils.copyProperties(projectConfigVO, config);
        String authKey = "RS_AUTH_" + projectConfigVO.getProjectId() + "_*";
        // 当前项目配置
        BaseProject p = projectDao.selectByPrimaryKey(projectConfigVO.getProjectId());
        BaseProjectConfig projectConfig = JSONObject.parseObject(p.getConfig(), BaseProjectConfig.class);

        redisUtil.deleteByPrex(authKey);
        if (projectConfigVO.getAllowRedPacket().equals(ProjectConstants.OPEN)) {
            // 红包发送记录表
            redPacketRecordDao.createCustomTable(ProjectConstants.getRedPackRecordTableName(projectConfigVO.getProjectId()));
        }
        if (projectConfigVO.getAllowSmsAEmail().equals(ProjectConstants.OPEN)) {// 允许短信和邮件
            projectConfigVO.setIfOpenSample(ProjectConstants.OPEN);
            config.setIfOpenSample(ProjectConstants.OPEN);
            // 发送任务表
            publishQueueDao.createCustomTable(ProjectConstants.getPublishQueueTableName(projectConfigVO.getProjectId()));
            // 发送日志表
            publishLogDao.createCustomTable(ProjectConstants.getPublishLogTableName(projectConfigVO.getProjectId()));
        }
        if (projectConfigVO.getMoreSampleInfo().equals(ProjectConstants.OPEN)) {// 更多信息
            projectConfigVO.setIfOpenSample(ProjectConstants.OPEN);
            config.setIfOpenSample(ProjectConstants.OPEN);
            // 地址表 接触记录表 联系表
            addressDao.createCustomTable(ProjectConstants.getSampleAddressTableName(projectConfigVO.getProjectId()));
            contactDao.createCustomTable(ProjectConstants.getSampleContactsTableName(projectConfigVO.getProjectId()));
            touchDao.createCustomTable(ProjectConstants.getSampleTouchTableName(projectConfigVO.getProjectId()));
        }
        if (projectConfigVO.getInterviewerSaveSample().equals(ProjectConstants.OPEN)) {// 允许访员添加样本
            projectConfigVO.setIfOpenSample(ProjectConstants.OPEN);
            config.setIfOpenSample(ProjectConstants.OPEN);
        }
        if (projectConfigVO.getSampleExtraData().equals(ProjectConstants.OPEN)) {// 附加数据
            projectConfigVO.setIfOpenSample(ProjectConstants.OPEN);
            config.setIfOpenSample(ProjectConstants.OPEN);
        }
        if (projectConfigVO.getResponseHistory().equals(ProjectConstants.OPEN)) {
            // 答卷历史表
            responseHistoryDao.createCustomTable(ProjectConstants.getResponseHistoryTableName(projectConfigVO.getProjectId()));
        }
        if (projectConfigVO.getResponseAudio().equals(ProjectConstants.OPEN)) {
            // 答卷录音表
            responseAudioDao.createCustomTable(ProjectConstants.getResponseAudioTableName(projectConfigVO.getProjectId()));
        }
        if (projectConfigVO.getResponsePosition().equals(ProjectConstants.OPEN)) {
            // 答卷定位表
            responsePositionDao.createCustomTable(ProjectConstants.getResponsePositionTableName(projectConfigVO.getProjectId()));
        }
        if (!projectConfigVO.getTrackInterviewer().equals(ProjectConstants.CLOSE)) {
            // 访员轨迹表
            travelDao.createCustomTable(ProjectConstants.getInterviewerTravelTableName(projectConfigVO.getProjectId()));
        }
        // 关闭二次提交 则将有效答卷全部置为答题成功 样本状态置为已完成
        if (projectConfig.getSecondSubmit().equals(ProjectConstants.OPEN) && projectConfigVO.getSecondSubmit().equals(ProjectConstants.CLOSE)) {
            Example example = new Example(BaseResponse.class);
            example.setTableName(ProjectConstants.getResponseTableName(projectConfigVO.getProjectId()));
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("responseType", ResponseConstants.RESPONSE_TYPE_VALID);
            criteria.andEqualTo("responseStatus", ResponseConstants.RESPONSE_STATUS_START);
            List<BaseResponse> responseList = responseDao.selectByExample(example);
            for (BaseResponse response : responseList) {
                response.setResponseStatus(ResponseConstants.RESPONSE_STATUS_SUCCESS);
                response.setDynamicTableName(ProjectConstants.getResponseTableName(projectConfigVO.getProjectId()));
                responseDao.updateByPrimaryKeySelective(response);
            }
            //样本状态更新已完成
            // TODO 多问卷是否有问题
            List<String> sampleGuids = responseList.stream().map(BaseResponse::getSampleGuid).collect(Collectors.toList());
            SampleUpdateVO updateVO = new SampleUpdateVO();
            updateVO.setProjectId(projectConfigVO.getProjectId());
            updateVO.setStatus(SampleConstants.STATUS_FINISH);
            updateVO.setSampleGuids(sampleGuids);
            if (sampleGuids.size() > 0) {
                sampleDao.updateSamplesStatus(updateVO);
            }
        }
        // 修改成功更新redis缓存
        redisUtil.hdel(RedisKeyConstants.projectKey(projectConfigVO.getProjectId()),
                RedisKeyConstants.projectConfigKey(projectConfigVO.getProjectId()));
        ProjectConfigsDTO configDto = getProjectConfig(projectConfigVO.getProjectId());
        BeanUtils.copyProperties(projectConfigVO, configDto);
        ProjectConfigDTO configDTO = new ProjectConfigDTO();
        BeanUtils.copyProperties(configDto, configDTO);
        redisUtil.hset(RedisKeyConstants.projectKey(projectConfigVO.getProjectId()),
                RedisKeyConstants.projectConfigKey(projectConfigVO.getProjectId()), configDTO);
        BaseProject project = new BaseProject();
        project.setId(projectConfigVO.getProjectId());
        project.setLastModifyUser(ThreadLocalManager.getUserId());
        project.setLastModifyTime(new Date());
        project.setConfig(JSON.toJSONString(config));
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 获取项目用户权限
     *
     * @param projectId
     * @return
     */
    public List<String> getProjectPermission(Integer projectId) {
        List<String> res = new ArrayList<>();
        ProjectHeadDTO project = projectDao.getProjectNameAndRole(projectId, ThreadLocalManager.getUserId());
        ProjectConfigDTO config = this.getProjectConfig(projectId);
        String roleKey = RedisKeyConstants.pmRoleKey(projectId);
        if (project.getCreateUser().equals(ThreadLocalManager.getUserId())) {
            res.addAll(AuthorizedConstants.R_ALL);
            redisUtil.set(roleKey, AuthorizedConstants.ROLE_ADMIN);
        } else {
            res = permissionDao.getProjectPermissionForUser(projectId, ThreadLocalManager.getUserId());
            if (project.getRoleId().contains(AuthorizedConstants.ROLE_INTERVIEWER.toString())
                    && config.getInterviewerSaveSample().equals(ProjectConstants.OPEN)) {
                res.add(AuthorizedConstants.RS_SAMPLE_ADD);
                res.add(AuthorizedConstants.RS_SAMPLE_EDIT);
                res.add(AuthorizedConstants.RS_SAMPLE_DELETE);
                res.removeIf(s -> (ProjectConstants.CLOSE.equals(config.getResponseRecall()) && AuthorizedConstants.RA_ANSWER_EDIT.equals(s)));
            }
            redisUtil.set(roleKey, project.getRoleId());
        }
//        if (ProjectConstants.CLOSE.equals(config.getSampleExtraData())) {
        res.removeIf(s -> (ProjectConstants.CLOSE.equals(config.getSampleExtraData()) && AuthorizedConstants.RS_SAMPLE_OUTDATA_ADMIN.equals(s))
                || (ProjectConstants.CLOSE.equals(config.getMoreSampleInfo()) && AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN.equals(s))
                || (ProjectConstants.CLOSE.equals(config.getManageTeamGroup()) && AuthorizedConstants.RT_MEMBER_GROUP_ADMIN.equals(s))
                || (ProjectConstants.CLOSE.equals(config.getQnaireGroup()) && AuthorizedConstants.RQ_QNAIRE_GROUP.equals(s))
                || (ProjectConstants.CLOSE.equals(config.getResponseAudio()) && AuthorizedConstants.RA_ANSWER_VOICE.equals(s)));
//        }
        return res;
    }

    /**
     * 更新邀请码信息
     *
     * @param invitedVO
     * @return
     */
    public Integer updateInvitedCodeInfo(ProjectInvitedVO invitedVO) {
        BaseProject project = new BaseProject();
        BeanUtils.copyProperties(invitedVO, project);
        project.setLastModifyTime(new Date());
        project.setLastModifyUser(ThreadLocalManager.getUserId());
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 更新邀请码信息
     *
     * @param projectId
     * @return
     */
    public Integer updateInvitedCode(Integer projectId) {
        BaseProject project = new BaseProject();
        project.setId(projectId);
        project.setInviteCode(getCode());
        project.setLastModifyTime(new Date());
        project.setLastModifyUser(ThreadLocalManager.getUserId());
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 获取项目统计
     *
     * @return
     */
    public Map<String, Object> getProjectTextCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 6);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Map<String, Integer> project = projectDao.getProjectCount();
        Map<String, Object> res = new HashMap<>();
        res.put("responseCount", project.get("answerCount"));
        res.put("projectCount", project.get("projectCount"));
        List<Map<String, Object>> dateCount = projectDao.getLast7DayCount(calendar.getTime());
        List<String> dateList = this.getLastDays(6);
        List<Integer> resCount = new ArrayList<>();
        for (int i = 0; i < dateList.size(); i++) {
            resCount.add(0);
        }
        for (int i = 0; i < dateCount.size(); i++) {
            Map<String, Object> tempMap = dateCount.get(i);
            resCount.set(dateList.indexOf(tempMap.get("dayDate")), Integer.parseInt(tempMap.get("count").toString()));
        }
        res.put("projectList", resCount);
        // 总数量走势
        List<Map<String, Object>> totalTemp = projectDao.getProjectTotalResult(dateList.get(0));
        List<Map<String, Object>> newTemp = projectDao.getProjectNewResult(dateList.get(0));
        Map<String, Integer> totalMap = new TreeMap<>();
        for (int i = 0; i < totalTemp.size(); i++) {
            totalMap.put(totalTemp.get(i).get("yearDay").toString(), Integer.parseInt(totalTemp.get(i).get("totalCount").toString()));
        }
        List<Integer> totalResult = new ArrayList<>();
        int total = totalTemp == null || totalTemp.size() == 0 ? project.get("projectCount")
                : Integer.parseInt(totalTemp.get(0).get("totalCount").toString()) - Integer.parseInt(newTemp.get(0).get("totalCount").toString());
        for (int i = 0; i < dateList.size(); i++) {
            if (totalMap.containsKey(dateList.get(i))) {
                total = totalMap.get(dateList.get(i));
            }
            totalResult.add(total);
        }
        res.put("totalProjectList", totalResult);
        return res;
    }

    private List<String> getLastDays(int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - day);
        Date today = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = dateFormat.format(today);

        // 返回的日期集合
        List<String> days = new ArrayList<String>();

        try {
            Date start = dateFormat.parse(endTime);
            Date end = dateFormat.parse(dateFormat.format(new Date()));

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

    /**
     * 判断用户角色
     * 是否支持创建当前类型项目
     * 问卷类型
     * 项目数量是否到达数量上限
     *
     * @param projectVO
     */
    private void checkUserRole(ProjectVO projectVO) {
        Integer userRole = ThreadLocalManager.getTokenContext().getNlpRole();
        String surveyType = projectVO.getType();
        Example example = new Example(BaseProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        int projectNum = projectDao.selectCountByExample(example);
        if (userRole.equals(UserConstants.ROLE_COMMON)) {
            if (!ProjectConstants.PROJECT_TYPE_CAWI.equals(surveyType)) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "当前登录用户权限不支持创建该调查类型的项目");
            }
            if (projectVO.getQnaireType().equals(ProjectConstants.OPEN)) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "当前登录用户权限不支持创建多问卷的项目");
            }
            if (projectNum >= ProjectConstants.TOP_LIMIT) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目创建数量已达上限");
            }
        } else if (userRole.equals(UserConstants.ROLE_VIP)) {
            if (ProjectConstants.PROJECT_TYPE_CATI.equals(surveyType) || ProjectConstants.PROJECT_TYPE_CAXI.equals(surveyType)) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "当前登录用户权限不支持创建该调查类型的项目");
            }
            if (projectVO.getQnaireType().equals(ProjectConstants.OPEN)) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "当前登录用户权限不支持创建多问卷的项目");
            }
            if (projectNum >= ProjectConstants.TOP_LIMIT) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目创建数量已达上限");
            }
        } else if (userRole.equals(UserConstants.ROLE_CONPANY)) {
            if (ProjectConstants.PROJECT_TYPE_CATI.equals(surveyType) || ProjectConstants.PROJECT_TYPE_CAXI.equals(surveyType)) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "当前登录用户权限不支持创建该调查类型的项目");
            }
            if (projectNum >= ProjectConstants.TOP_LIMIT) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目创建数量已达上限");
            }
        }
    }

    /**
     * 项目创建人权限
     *
     * @param projectId
     * @return
     */
    public int getProjectOwnerRole(Integer projectId) {
        BaseProject project = projectDao.selectByPrimaryKey(projectId);
        BaseUser user = userDao.selectByPrimaryKey(project.getCreateUser());
        return user.getRole();
    }

    /**
     * 获取后台配置（用于本地部署）
     *
     * @return
     */
    public ConfigDTO getConfig() {
        ConfigDTO configDTO = new ConfigDTO();
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> res = (Map<String, Object>) authService.getConfigResponseResult(params, authGetConfigUrl);
        configDTO.setNeedRegister(Integer.parseInt(res.get("needRegister").toString()));
        configDTO.setNeedAudit(Integer.parseInt(res.get("needAudit").toString()));
        JSONArray jsonArray = JSONArray.parseArray(res.get("projectAuth").toString());
        if (null != jsonArray && !jsonArray.isEmpty()) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                list.add(jsonArray.getString(i));
            }
            configDTO.setProjectAuth(list);
        }
        return configDTO;
    }

    /**
     * 查询项目个数
     *
     * @return
     */
    public List<Map<String, Integer>> getProjectCountByUserId() {
        return projectDao.getProjectCountByUserId(ThreadLocalManager.getUserId());
    }

    /**
     * 查询项目个数（TestController用）
     *
     * @return
     */
    public List<Map<String, Integer>> getProjectCountByUserIdTest() {
        BaseUser testUser = userService.getTestUser();
        return projectDao.getProjectCountByUserId(testUser.getId());
    }

}
