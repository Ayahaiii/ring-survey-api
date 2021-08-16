package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.monitor.AnswerLonALatDTO;
import com.monetware.ringsurvey.business.pojo.dto.monitor.ProjectReportDTO;
import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.business.pojo.vo.monitor.AnswerProcessVO;
import com.monetware.ringsurvey.business.pojo.vo.project.ProjectApplyVO;
import com.monetware.ringsurvey.business.pojo.dto.project.ProjectHeadDTO;
import com.monetware.ringsurvey.business.pojo.dto.project.ProjectInfoDTO;
import com.monetware.ringsurvey.business.pojo.vo.project.*;
import com.monetware.ringsurvey.business.service.ScheduleService;
import com.monetware.ringsurvey.business.service.project.MonitorService;
import com.monetware.ringsurvey.business.service.project.ProjectService;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("项目详情")
@RestController
@RequestMapping("/w1/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private ScheduleService scheduleService;

    // ******************** 项目相关 ********************

    @PostMapping("create")
    @ApiOperation(value = "创建项目")
    public ResultData createProject(@RequestBody ProjectVO projectVO) {
        projectService.createProject(projectVO);
        return new ResultData<>("创建成功");
    }

    @PostMapping("delete/{projectId}")
    @ApiOperation(value = "删除项目")
    public ResultData<Integer> deleteProject(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("删除成功", projectService.deleteProject(projectId));
    }

    @PostMapping("update")
    @ApiOperation(value = "修改项目")
    @MonetwareAuthorize(pm = AuthorizedConstants.RP_PROJECT_EDIT)
    public ResultData<Integer> updateProject(@RequestBody ProjectVO projectVO) {
        return new ResultData<>("修改成功", projectService.updateProject(projectVO));
    }

    @PostMapping("update/status")
    @ApiOperation(value = "修改项目状态")
    @MonetwareAuthorize(pm = AuthorizedConstants.RP_PROJECT_EDIT)
    public ResultData<Integer> updateProjectStatus(@RequestBody ProjectStatusVO projectVO) {
        return new ResultData<>("状态变更成功", projectService.updateProjectStatus(projectVO));
    }

    @PostMapping("get/{projectId}")
    @ApiOperation(value = "获取项目基本信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<ProjectInfoDTO> getProjectInfo(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", projectService.getProjectInfo(projectId));
    }

    @PostMapping("get/head/{projectId}")
    @ApiOperation(value = "获取项目基本信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<ProjectHeadDTO> getProjectNameAndRole(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", projectService.getProjectNameAndRole(projectId));
    }

    @PostMapping("get/config/{projectId}")
    @ApiOperation(value = "获取项目配置信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<ProjectConfigDTO> getProjectConfig(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", projectService.getProjectConfig(projectId));
    }

    @PostMapping("get/report/total")
    @ApiOperation(value = "获取项目图表信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<ProjectReportDTO> getProjectMonitoringReport(@RequestBody BaseVO baseVO) {
        return new ResultData<>("获取成功", monitorService.getProjectReport(baseVO));
    }

    @PostMapping("get/report/process")
    @ApiOperation(value = "获取答卷进度信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<LinkedHashMap<String, Object>> getProgressMonitoring(@RequestBody AnswerProcessVO processVO) {
        return new ResultData<>("获取成功", monitorService.getAnswerProgress(processVO));
    }

    @PostMapping("get/report/location/{projectId}")
    @ApiOperation(value = "获取地图坐标信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<List<AnswerLonALatDTO>> getAnswerLonAndLat(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", monitorService.getAnswerLonAndLat(projectId));
    }

    @PostMapping("update/config")
    @ApiOperation(value = "修改项目配置")
    @MonetwareAuthorize(pm = AuthorizedConstants.RP_PROJECT_CONFIG_ADMIN)
    public ResultData<Integer> updateProjectConfig(@RequestBody ProjectConfigVO projectConfigVO) {
        return new ResultData<>("修改成功", projectService.updateProjectConfig(projectConfigVO));
    }

    @PostMapping("list/page")
    @ApiOperation(value = "分页获取项目列表")
    public ResultData<PageList> getProjectList(@RequestBody ProjectListVO projectListVO) {
        return new ResultData("获取成功", projectService.getProjectList(projectListVO));
    }

    @PostMapping("apply")
    @ApiOperation(value = "申请加入项目")
    public ResultData<Integer> insertApplyTeam(@RequestBody ProjectApplyVO projectApplyVO) {
        return new ResultData<>("申请成功", projectService.insertApplyTeam(projectApplyVO));
    }

    @PostMapping("permission/{projectId}")
    @ApiOperation(value = "获取项目权限")
    public ResultData<List<String>> getProjectPermission(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", projectService.getProjectPermission(projectId));
    }

    @PostMapping("update/code/info")
    @ApiOperation(value = "修改邀请码信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_INVITECODE_ADMIN)
    public ResultData<Integer> updateInvitedCodeInfo(@RequestBody ProjectInvitedVO projectApplyVO) {
        return new ResultData<>("修改成功", projectService.updateInvitedCodeInfo(projectApplyVO));
    }

    @PostMapping("update/code/{projectId}")
    @ApiOperation(value = "修改邀请码")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_INVITECODE_ADMIN)
    public ResultData<Integer> updateInvitedCode(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("修改成功", projectService.updateInvitedCode(projectId));
    }

    @PostMapping("get/count")
    @ApiOperation(value = "获取项目个数")
    public ResultData<List<Map<String, Integer>>> getProjectCount() {
        return new ResultData<>("查询成功", projectService.getProjectCountByUserId());
    }

    // ******************** 回收站 ********************

    @PostMapping("list/recycle/page")
    @ApiOperation(value = "分页获取回收站项目列表")
    public ResultData<PageList> getDeleteProject(@RequestBody ProjectDeleteVO deleteVO) {
        return new ResultData("获取成功", projectService.getDeleteProject(deleteVO));
    }

    @PostMapping("release/{projectId}")
    @ApiOperation(value = "释放项目")
    public ResultData releaseProject(@PathVariable("projectId") Integer projectId) {
        scheduleService.deleteProject(projectId);
        return new ResultData<>(0, "释放成功");
    }

}
