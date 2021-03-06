package com.monetware.ringsurvey.business.service;

import com.alibaba.fastjson.JSONObject;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.ProjectConstants;
import com.monetware.ringsurvey.business.pojo.constants.UserConstants;
import com.monetware.ringsurvey.business.pojo.dto.project.ProjectFinishDTO;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.system.util.file.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Slf4j
@Service
public class ScheduleService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectAnalysisModuleDao analysisModuleDao;

    @Autowired
    private ProjectExportDao exportDao;

    @Autowired
    private ProjectModuleDao moduleDao;

    @Autowired
    private ProjectQuestionnaireDao questionnaireDao;

    @Autowired
    private ProjectModuleGroupDao moduleGroupDao;

    @Autowired
    private ProjectPropertyDao propertyDao;

    @Autowired
    private ProjectQuotaDao quotaDao;

    @Autowired
    private RedPacketConfigDao redPacketConfigDao;

    @Autowired
    private ProjectSampleStatusRecordDao recordDao;

    @Autowired
    private ProjectTeamGroupDao teamGroupDao;

    @Autowired
    private ProjectTeamUserDao teamUserDao;

    @Autowired
    private ProjectTeamUserToGroupDao teamUserToGroupDao;

    @Autowired
    private ProjectTeamUserRoleDao teamUserRoleDao;

    @Autowired
    private ProjectResponseAudioDao responseAudioDao;

    @Autowired
    private ProjectResponseFileDao responseFileDao;

    @Value("${fileUrl.upload}")
    private String filePath;

    @Value("${fileUrl.qnaire}")
    private String questionnaireDir;

    /**
     * ??????????????????
     */
    public void updateProjectRelease() {

        Date now = new Date();
        Calendar cal = Calendar.getInstance();

        // ????????????VIP??????????????????
        List<BaseUser> allUsers = userDao.selectAll();
        if (allUsers != null && allUsers.size() > 0) {
            for (BaseUser user : allUsers) {
                if (null != user.getExpireTime() && (now.after(user.getExpireTime()) || user.getRole().equals(UserConstants.ROLE_COMMON))) {
                    user.setRole(UserConstants.ROLE_COMMON);
                    userDao.updateByPrimaryKey(user);
                }
            }
        }

        // ?????????????????????
        List<ProjectFinishDTO> listFinish = projectDao.getProjectByFinish();
        if (listFinish.size() > 0) {
            for (ProjectFinishDTO project : listFinish) {
                if (UserConstants.ROLE_ADMIN.equals(project.getRole()) || UserConstants.ROLE_CUSTOM.equals(project.getRole())) {
                    continue;
                }
                if (project.getUserExpireTime() != null && project.getCreateTime().before(project.getUserExpireTime())) {
                    cal.setTime(project.getUserExpireTime());
                } else {
                    cal.setTime(project.getCreateTime());
                }
                if (UserConstants.ROLE_COMMON.equals(project.getRole())) {
                    cal.add(Calendar.YEAR, 1);
                    if (now.after(cal.getTime())) {
                        BaseProject baseProject = new BaseProject();
                        baseProject.setId(project.getId());
                        baseProject.setDeleteTime(now);
                        baseProject.setIsDelete(ProjectConstants.DELETE_YES);
                        projectDao.updateByPrimaryKeySelective(baseProject);
                    }
                }
            }
        }

        // ?????????????????????
        Example example = new Example(BaseProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_YES);
        List<BaseProject> list = projectDao.selectByExample(example);
        if (list.size() > 0) {
            for (BaseProject project : list) {
                if (project.getDeleteTime() != null) {
                    cal.setTime(project.getDeleteTime());
                    cal.add(Calendar.DATE, 30);
                    if (now.after(cal.getTime())) {
                        deleteProject(project.getId());
                    }
                }
            }
        }

        // ?????????????????????
        example = new Example(BaseProject.class);
        criteria = example.createCriteria();
        criteria.andEqualTo("status", ProjectConstants.STATUS_RUN);
        criteria.orEqualTo("status", ProjectConstants.STATUS_WAIT);
        List<BaseProject> runList = projectDao.selectByExample(example);
        if (list.size() > 0) {
            BaseProjectConfig config;
            for (BaseProject project : runList) {
                config = JSONObject.parseObject(project.getConfig(), BaseProjectConfig.class);
                if (ProjectConstants.OPEN.equals(config.getAutoTimeEnd())) {
                    cal.setTime(config.getAutoTimeEndValue());
                    if (now.after(cal.getTime())) {
                        project.setStatus(ProjectConstants.STATUS_PAUSE);
                        project.setPauseTime(now);
                        projectDao.updateByPrimaryKeySelective(project);
                    }
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param projectId
     */
    public void deleteProject(Integer projectId) {
        Example delExample;
        Example.Criteria delCriteria;
        // ???????????????
        projectDao.dropCustomTable(ProjectConstants.getSampleTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getSampleAssignmentTableName(projectId));
        // ????????????????????????
        projectDao.dropCustomTable(ProjectConstants.getSampleContactsTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getSampleAddressTableName(projectId));
        // ???????????????????????????
        projectDao.dropCustomTable(ProjectConstants.getSampleTouchTableName(projectId));
        // ??????????????????
        projectDao.dropCustomTable(ProjectConstants.getSamplePoolTableName(projectId));
        // ???????????????
        projectDao.dropCustomTable(ProjectConstants.getResponseTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getResponseAuditTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getResponseHistoryTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getResponseAudioTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getResponsePositionTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getResponseFileTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getRedPackRecordTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getPublishQueueTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getPublishLogTableName(projectId));
        // ?????????????????????
        projectDao.dropCustomTable(ProjectConstants.getInterviewerTravelTableName(projectId));
        // ??????????????????
        delExample = new Example(BaseProjectAnalysisModule.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        analysisModuleDao.deleteByExample(delExample);
        // ??????????????????
        delExample = new Example(BaseProjectExportHistory.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        List<BaseProjectExportHistory> exports = exportDao.selectByExample(delExample);
        if (exports.size() > 0) {
            String path;
            for (BaseProjectExportHistory t : exports) {
                path = filePath + "/export/" + projectId + "/" + t.getType().toLowerCase() + "/";
                FileUtil.deleteFile(path, t.getName());
            }
        }
        exportDao.deleteByExample(delExample);
        // ??????????????????
        delExample = new Example(BaseProjectModule.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        List<BaseProjectModule> modules = moduleDao.selectByExample(delExample);
        moduleDao.deleteByExample(delExample);
        if (modules.size() > 0) {
            for (BaseProjectModule module : modules) {
                delExample = new Example(BaseProjectQuestionnaire.class);
                delCriteria = delExample.createCriteria();
                delCriteria.andEqualTo("projectId", projectId);
                delCriteria.andEqualTo("moduleId", module.getId());
                List<BaseProjectQuestionnaire> questionnaires = questionnaireDao.selectByExample(delExample);
                if (questionnaires.size() > 0) {
                    String path;
                    for (BaseProjectQuestionnaire q : questionnaires) {
                        path = filePath + "/js/" + projectId + "/" + module.getCode() + "-" + q.getVersion() + "/";
                        FileUtil.deleteFile(path, "survey.js");
                    }
                }
            }
        }
        // ??????????????????
        delExample = new Example(BaseProjectModuleGroup.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        moduleGroupDao.deleteByExample(delExample);
        // ????????????????????????
        delExample = new Example(BaseProjectProperty.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        propertyDao.deleteByExample(delExample);
        // ??????????????????
        delExample = new Example(BaseProjectQuota.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        quotaDao.deleteByExample(delExample);
        // ??????????????????
        delExample = new Example(BaseRedPacketConfig.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        redPacketConfigDao.deleteByExample(delExample);
        // ????????????????????????
        delExample = new Example(BaseProjectSampleStatusRecord.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        recordDao.deleteByExample(delExample);
        // ??????????????????
        delExample = new Example(BaseProjectTeamGroup.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        teamGroupDao.deleteByExample(delExample);
        // ??????????????????
        delExample = new Example(BaseProjectTeamUser.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        teamUserDao.deleteByExample(delExample);
        // ????????????????????????
        delExample = new Example(BaseProjectTeamUserRole.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        teamUserRoleDao.deleteByExample(delExample);
        // ????????????????????????
        delExample = new Example(BaseProjectTeamUserToGroup.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        teamUserToGroupDao.deleteByExample(delExample);
        // ?????????????????????
        FileUtil.deleteFile(new File(filePath + "/project-file/" + projectId));
        FileUtil.deleteFile(new File(filePath + "/export/" + projectId));
        FileUtil.deleteFile(new File(questionnaireDir + "/js/" + projectId));
        // ????????????
        projectDao.deleteByPrimaryKey(projectId);
    }


}
