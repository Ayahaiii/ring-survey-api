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
     * 定时操作项目
     */
    public void updateProjectRelease() {

        Date now = new Date();
        Calendar cal = Calendar.getInstance();

        // 判断用户VIP是否过期操作
        List<BaseUser> allUsers = userDao.selectAll();
        if (allUsers != null && allUsers.size() > 0) {
            for (BaseUser user : allUsers) {
                if (null != user.getExpireTime() && (now.after(user.getExpireTime()) || user.getRole().equals(UserConstants.ROLE_COMMON))) {
                    user.setRole(UserConstants.ROLE_COMMON);
                    userDao.updateByPrimaryKey(user);
                }
            }
        }

        // 查询已结束项目
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

        // 查询已删除项目
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

        // 查询进行中项目
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
     * 删除项目相关
     *
     * @param projectId
     */
    public void deleteProject(Integer projectId) {
        Example delExample;
        Example.Criteria delCriteria;
        // 删除样本表
        projectDao.dropCustomTable(ProjectConstants.getSampleTableName(projectId));
        // 删除样本分派表
        projectDao.dropCustomTable(ProjectConstants.getSampleAssignmentTableName(projectId));
        // 删除样本通讯录表
        projectDao.dropCustomTable(ProjectConstants.getSampleContactsTableName(projectId));
        // 删除样本地址表
        projectDao.dropCustomTable(ProjectConstants.getSampleAddressTableName(projectId));
        // 删除样本接触记录表
        projectDao.dropCustomTable(ProjectConstants.getSampleTouchTableName(projectId));
        // 删除样本池表
        projectDao.dropCustomTable(ProjectConstants.getSamplePoolTableName(projectId));
        // 删除答卷表
        projectDao.dropCustomTable(ProjectConstants.getResponseTableName(projectId));
        // 删除答卷审核表
        projectDao.dropCustomTable(ProjectConstants.getResponseAuditTableName(projectId));
        // 删除答卷历史表
        projectDao.dropCustomTable(ProjectConstants.getResponseHistoryTableName(projectId));
        // 删除答卷录音表
        projectDao.dropCustomTable(ProjectConstants.getResponseAudioTableName(projectId));
        // 删除答卷定位表
        projectDao.dropCustomTable(ProjectConstants.getResponsePositionTableName(projectId));
        // 删除答卷附件表
        projectDao.dropCustomTable(ProjectConstants.getResponseFileTableName(projectId));
        // 删除红包记录表
        projectDao.dropCustomTable(ProjectConstants.getRedPackRecordTableName(projectId));
        // 删除发布队列表
        projectDao.dropCustomTable(ProjectConstants.getPublishQueueTableName(projectId));
        // 删除发布日志表
        projectDao.dropCustomTable(ProjectConstants.getPublishLogTableName(projectId));
        // 删除访员轨迹表
        projectDao.dropCustomTable(ProjectConstants.getInterviewerTravelTableName(projectId));
        // 删除常用统计
        delExample = new Example(BaseProjectAnalysisModule.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        analysisModuleDao.deleteByExample(delExample);
        // 删除导出历史
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
        // 删除项目问卷
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
        // 删除问卷分组
        delExample = new Example(BaseProjectModuleGroup.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        moduleGroupDao.deleteByExample(delExample);
        // 删除项目属性配置
        delExample = new Example(BaseProjectProperty.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        propertyDao.deleteByExample(delExample);
        // 删除项目配额
        delExample = new Example(BaseProjectQuota.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        quotaDao.deleteByExample(delExample);
        // 删除红包配置
        delExample = new Example(BaseRedPacketConfig.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        redPacketConfigDao.deleteByExample(delExample);
        // 删除样本状态记录
        delExample = new Example(BaseProjectSampleStatusRecord.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        recordDao.deleteByExample(delExample);
        // 删除团队分组
        delExample = new Example(BaseProjectTeamGroup.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        teamGroupDao.deleteByExample(delExample);
        // 删除团队成员
        delExample = new Example(BaseProjectTeamUser.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        teamUserDao.deleteByExample(delExample);
        // 删除团队成员角色
        delExample = new Example(BaseProjectTeamUserRole.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        teamUserRoleDao.deleteByExample(delExample);
        // 删除团队成员分组
        delExample = new Example(BaseProjectTeamUserToGroup.class);
        delCriteria = delExample.createCriteria();
        delCriteria.andEqualTo("projectId", projectId);
        teamUserToGroupDao.deleteByExample(delExample);
        // 删除项目文件夹
        FileUtil.deleteFile(new File(filePath + "/project-file/" + projectId));
        FileUtil.deleteFile(new File(filePath + "/export/" + projectId));
        FileUtil.deleteFile(new File(questionnaireDir + "/js/" + projectId));
        // 删除项目
        projectDao.deleteByPrimaryKey(projectId);
    }


}
