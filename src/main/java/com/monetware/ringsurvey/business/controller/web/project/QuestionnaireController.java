package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.*;
import com.monetware.ringsurvey.business.pojo.vo.qnaire.*;
import com.monetware.ringsurvey.business.pojo.vo.questionnaire.CheckStructureVO;
import com.monetware.ringsurvey.business.service.project.QuestionnaireService;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("问卷")
@RestController
@RequestMapping("/w1/qnaire")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;// 项目问卷

    @PostMapping("save")
    @ApiOperation(value = "获取导入问卷基本信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_IMPORT)
    public ResultData insertProjectModule(@RequestBody ProjectModuleVO projectModuleVO) {
        questionnaireService.insertProjectModule(projectModuleVO);
        return new ResultData<>(0, "导入成功");
    }

    @PostMapping("get/info")
    @ApiOperation(value = "获取导入问卷基本信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_VIEW)
    public ResultData<ModuleImportDTO> getImportModuleResult(@RequestBody ProjectModuleImportVO importVO) {
        return new ResultData<>("获取成功", questionnaireService.getImportModuleResult(importVO));
    }

    @GetMapping("export")
    @ApiOperation(value = "导出问卷")
    public void exportQuestionnaire(Integer id, HttpServletResponse response) {
        questionnaireService.exportQuestionnaire(id, response);
    }

    @PostMapping("list/{projectId}")
    @ApiOperation(value = "获取模块分组列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_IMPORT)
    public ResultData<List<ProjectModuleListDTO>> getProjectModuleQuestionnaireList(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", questionnaireService.getProjectModuleQuestionnaireList(projectId));
    }

    @PostMapping("list/page")
    @ApiOperation(value = "分页获取问卷列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_LIST)
    public ResultData<PageList> getProjectQnaireList(@RequestBody QnaireSearchVO qnaireSearchVO) {
        return new ResultData("获取成功", questionnaireService.getProjectQnaireList(qnaireSearchVO));
    }

    @PostMapping("group/list/{projectId}")
    @ApiOperation(value = "获取问卷分组列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_GROUP, cp = AuthorizedConstants.RC_QNAIRE_GROUP)
    public ResultData<List<ProjectModuleGroupDTO>> getProjectModuleGroupList(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", questionnaireService.getProjectModuleGroupList(projectId));
    }

    @PostMapping("group/save")
    @ApiOperation(value = "保存问卷分组")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_GROUP, cp = AuthorizedConstants.RC_QNAIRE_GROUP)
    public ResultData<Integer> saveModuleGroup(@RequestBody ProjectModuleGroupVO groupVO) {
        return new ResultData<>("保存成功", questionnaireService.saveModuleGroup(groupVO));
    }

    @PostMapping("group/delete")
    @ApiOperation(value = "删除问卷分组")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_GROUP, cp = AuthorizedConstants.RC_QNAIRE_GROUP)
    public ResultData<Integer> deleteModuleGroup(@RequestBody ProjectModuleGroupVO moduleGroupVO) {
        return new ResultData<>("删除成功", questionnaireService.deleteModuleGroup(moduleGroupVO));
    }

    @PostMapping("update")
    @ApiOperation(value = "更新项目问卷状态")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_EDIT)
    public ResultData<Integer> handleProjectQuestionnaire(@RequestBody QnaireUpdateVO updateVO) {
        return new ResultData("更新成功", questionnaireService.handleProjectQuestionnaire(updateVO.getProjectId(),
                updateVO.getModuleId(), updateVO.getStatus()));
    }

    @PostMapping("cancel")
    @ApiOperation(value = "撤销项目问卷")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_EDIT)
    public ResultData<Integer> cancelProjectModule(@RequestBody QnaireUpdateVO updateVO) {
        return new ResultData("撤销成功", questionnaireService.cancelProjectModule(updateVO.getProjectId(),
                updateVO.getModuleId()));
    }

    @PostMapping("update/qnaire")
    @ApiOperation(value = "更新项目问卷")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_EDIT)
    public ResultData<Integer> updateProjectModule(@RequestBody QnaireUpdateVO updateVO) {
        return new ResultData("更新成功", questionnaireService.updateProjectModule(updateVO));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除项目问卷")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_EDIT)
    public ResultData<Integer> deleteProjectModule(@RequestBody QnaireUpdateVO updateVO) {
        return new ResultData("更新成功", questionnaireService.deleteProjectModule(updateVO));
    }

    @PostMapping("history/list")
    @ApiOperation(value = "历史问卷列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QNAIRE_LIST)
    public ResultData<PageList> historyList(@RequestBody QnaireSearchVO searchVO) {
        return new ResultData<>(0, "查询成功", questionnaireService.getHistoryList(searchVO));
    }

    @PostMapping("module/{projectId}")
    @ApiOperation(value = "获取项目问卷详情")
    public ResultData<ProjectModuleDTO> getProjectModuleInfo(@PathVariable("projectId") Integer projectId) {
        return new ResultData("获取成功", questionnaireService.getProjectModuleInfo(projectId));
    }

    @PostMapping("check/structure")
    @ApiOperation(value = "检查问卷结构")
    public ResultData<Boolean> checkQuestionStructure(@RequestBody CheckStructureVO checkStructureVO) {
        return new ResultData("检查成功", questionnaireService.checkQuestionStructure(checkStructureVO));
    }

}
