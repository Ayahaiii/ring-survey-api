package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.monitor.*;
import com.monetware.ringsurvey.business.pojo.dto.sample.InterviewerTravelDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserIdAndNameDTO;
import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.business.pojo.vo.monitor.*;
import com.monetware.ringsurvey.business.pojo.vo.sample.InterviewerTravelVO;
import com.monetware.ringsurvey.business.service.project.MonitorService;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("监控")
@RestController
@RequestMapping("/w1/report")
public class MonitorController {

    @Autowired
    public MonitorService monitorService;

    @PostMapping("total")
    @ApiOperation(value = "获取项目图表信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<ProjectReportDTO> getProjectMonitoringReport(@RequestBody BaseVO baseVO) {
        return new ResultData<>("获取成功", monitorService.getProjectReport(baseVO));
    }

    @PostMapping("process")
    @ApiOperation(value = "获取答卷进度信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<LinkedHashMap<String, Object>> getProgressMonitoring(@RequestBody AnswerProcessVO processVO) {
        return new ResultData<>("获取成功", monitorService.getAnswerProgress(processVO));
    }

    @PostMapping("answer/process")
    @ApiOperation(value = "获取答卷进度数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<LinkedHashMap<String, Object>> getAnswerTimeData(@RequestBody AnswerProcessVO processVO) {
        return new ResultData<>("获取成功", monitorService.getAnswerTimeData(processVO));
    }

    @PostMapping("location/{projectId}")
    @ApiOperation(value = "获取地图坐标信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<List<AnswerLonALatDTO>> getAnswerLonAndLat(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", monitorService.getAnswerLonAndLat(projectId));
    }

    @PostMapping("sample/use")
    @ApiOperation(value = "获取样本使用动态数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<LinkedHashMap<String, Object>> getSampleStatusUseList(@RequestBody SampleStatusUseVO statusUseVO) {
        return new ResultData<>("获取成功", monitorService.getSampleStatusUseList(statusUseVO));
    }

    //============================================ lu Begin =======================================
    @PostMapping("sample/complete")
    @ApiOperation(value = "获取访员绩效动态数据（样本完成数）")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<LinkedHashMap<String, Object>> getSampleCompleteList(@RequestBody SampleCompleteVO param) {
        return new ResultData("获取成功", monitorService.getSampleCompleteList(param));
    }

    @PostMapping("sample/status/distribution/{projectId}")
    @ApiOperation(value = "获取样本状态分布")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<LinkedHashMap<String, Object>> getSampleStatusDistribution(@PathVariable("projectId") Integer projectId) {
        return new ResultData("获取成功", monitorService.getSampleStatusDistribution(projectId));
    }

    @PostMapping("sample/status/statistics")
    @ApiOperation(value = "获取样本状态统计")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<SampleStatisticsDTO> getSampleStatusStatistics(@RequestBody SampleStatusStatisticsVO statisticsVO) {
        return new ResultData("获取成功", monitorService.getSampleStatusStatistics(statisticsVO));
    }

    @PostMapping("ifRegionSelection/{projectId}")
    @ApiOperation(value = "查看样本是否启动省市县")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<Boolean> ifRegionSelection(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", monitorService.ifRegionSelection(projectId));
    }

    @PostMapping("response/location/{projectId}")
    @ApiOperation(value = "获取地图坐标信息(前100)")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<List<ResponseLocationDTO>> getResponseLocation(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", monitorService.getResponseLocation(projectId));
    }

    @PostMapping("sample/address")
    @ApiOperation(value = "通过省市区获取样本数量")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<List<GetSampleCountByAddressDTO>> getSampleCountByAddress(@RequestBody GetSampleCountByAddressVO param) {
        return new ResultData<>("获取成功", monitorService.getSampleCountByAddress(param));
    }

    @PostMapping("index/dynamic")
    @ApiOperation(value = "获取指标动态")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<LinkedHashMap<String, Object>> getIndexDynamic(@RequestBody GetIndexDynamicVO param) {
        return new ResultData<>("获取成功", monitorService.getIndexDynamic(param));
    }

    @PostMapping("sample/dashboard/{projectId}")
    @ApiOperation(value = "获取仪表盘")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<SampleDashboardDTO> getSampleDashboard(@PathVariable("projectId") Integer projectId) {
        return new ResultData("获取成功", monitorService.getSampleDashboard(projectId));
    }

    @PostMapping("browser/param")
    @ApiOperation(value = "获取浏览器参数")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<GetBrowserParamDTO> getBrowserParam(@RequestBody GetBrowserParamVO param) {
        return new ResultData("获取成功", monitorService.getBrowserParam(param));
    }

    @PostMapping("source/report/{projectId}")
    @ApiOperation(value = "获取来源报告")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<LinkedHashMap<String, Object>> getSourceReport(@PathVariable("projectId") Integer projectId) {
        return new ResultData("获取成功", monitorService.getSourceReport(projectId));
    }

    @PostMapping("map/report")
    @ApiOperation(value = "获取地图报告")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<List<GetMapReportDTO>> getMapReport(@RequestBody GetMapReportVO param) {
        return new ResultData("获取成功", monitorService.getMapReport(param));
    }

    @PostMapping("map/report/detail")
    @ApiOperation(value = "获取地图报告详细信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<List<GetMapReportDTO>> getMapReportDetail(@RequestBody GetMapReportDetailVO param) {
        return new ResultData("获取成功", monitorService.getMapReportDetail(param));

    }

    @PostMapping("interviewer/{projectId}")
    @ApiOperation(value = "获取当前项目访问员信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<List<UserIdAndNameDTO>> getInterviewers(@PathVariable("projectId") Integer projectId) {
        return new ResultData("获取成功", monitorService.getInterviewers(projectId));

    }

    @PostMapping("questionnaire/List/{projectId}")
    @ApiOperation(value = "获取当前项目问卷信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<List<UserIdAndNameDTO>> getQuestionnaireList(@PathVariable("projectId") Integer projectId) {
        return new ResultData("获取成功", monitorService.getQuestionnaireList(projectId));

    }

    @PostMapping("status/List/{projectId}")
    @ApiOperation(value = "根据项目类型获取状态列表")
    public ResultData<List<Integer>> getStatusList(@PathVariable("projectId") Integer projectId) {
        return new ResultData("获取成功", monitorService.getStatusList(projectId));
    }

    //============================================ lu End =========================================

    @PostMapping("interviewers/travel")
    @ApiOperation(value = "获取所有访员最新位置")
    public ResultData<List<InterviewerTravelDTO>> getInterviewersLastedTravel(@RequestBody InterviewerTravelVO travelVO) {
        return new ResultData("获取成功", monitorService.getInterviewersLastedTravel(travelVO));
    }

    @PostMapping("interviewer/travel/single")
    @ApiOperation(value = "获取单个访员所有轨迹")
    public ResultData<List<InterviewerTravelDTO>> getInterviewerTravelList(@RequestBody InterviewerTravelVO travelVO) {
        return new ResultData("获取成功", monitorService.getInterviewerTravelList(travelVO));
    }

}
