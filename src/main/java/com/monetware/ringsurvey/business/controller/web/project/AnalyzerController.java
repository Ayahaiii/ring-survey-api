package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionSelectedDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectAnalysisModule;
import com.monetware.ringsurvey.business.pojo.vo.analyzer.*;
import com.monetware.ringsurvey.business.service.project.AnalyzerService;
import com.monetware.ringsurvey.business.service.project.QuestionnaireService;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("分析")
@RestController
@RequestMapping("/w1/analyst")
public class AnalyzerController {

    @Autowired
    private AnalyzerService analyzerService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @PostMapping("save")
    @ApiOperation(value = "保存统计模型")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Integer> saveAnalysisModule(@RequestBody AnalysisModuleVO moduleVO) {
        return new ResultData<>("保存成功", analyzerService.saveAnalysisModule(moduleVO));
    }

    @PostMapping("get")
    @ApiOperation(value = "获取统计模型")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<BaseProjectAnalysisModule> getAnalysisModule(@RequestBody AnalysisModuleInfoVO infoVO) {
        return new ResultData<>("获取成功", analyzerService.getAnalysisModule(infoVO));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除统计模型")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Integer> deleteAnalysisModule(@RequestBody AnalysisModuleInfoVO infoVO) {
        return new ResultData<>("删除成功", analyzerService.deleteAnalysisModule(infoVO));
    }

    @PostMapping("list/page")
    @ApiOperation(value = "分页获取统计模型列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<PageList> getAnalysisModuleList(@RequestBody AnalysisModuleSearchVO searchVO){
        return new ResultData("获取成功", analyzerService.getAnalysisModuleList(searchVO));
    }

    @PostMapping("qnaire/{projectId}")
    @ApiOperation(value = "查询其他统计问卷模块")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<List<QuestionSelectedDTO>> getQuotaQuestionSelectedList(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "查询成功", questionnaireService.getQuotaQuestionSelectedList(projectId));
    }

    @PostMapping("qnaire/single/{projectId}")
    @ApiOperation(value = "查询单题统计问卷模块")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<List<QuestionSelectedDTO>> getQuotaQuestionSelectedAndMatrixList(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "查询成功", questionnaireService.getQuotaQuestionSelectedAndMatrixList(projectId));
    }

    @PostMapping("single")
    @ApiOperation(value = "单题统计")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<List<HashMap<String, Object>>> getSingleStatistics(@RequestBody AnalysisSingleVO singleVO) {
        return new ResultData<>("统计成功", analyzerService.getSingleStatistics(singleVO));
    }

    @PostMapping("insect")
    @ApiOperation(value = "交叉统计")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<HashMap<String, Object>> getInsectStatistics(@RequestBody AnalysisInsectVO insectVO) {
        return new ResultData<>("统计成功", analyzerService.getInsectStatistics(insectVO));
    }

    @PostMapping("score")
    @ApiOperation(value = "打分统计")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<List<HashMap<String, Object>>> getScaleStatistics(@RequestBody AnalysisMarkVO markVO) {
        return new ResultData<>("统计成功", analyzerService.getScaleStatistics(markVO));
    }

    @PostMapping("save/base")
    @ApiOperation(value = "保存编码")
    public ResultData getBase64(@RequestBody BaseCodeVO codeVO) {
        analyzerService.saveBaseCode(codeVO);
        return new ResultData(0,"编码保存成功");
    }

    @GetMapping("pdf/download")
    @ApiOperation(value = "导出pdf")
    public void downloadPdf(String key, HttpServletResponse response) {
        analyzerService.exportPdf(key, response);
    }


}
