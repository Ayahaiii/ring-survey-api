package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionSelectedDTO;
import com.monetware.ringsurvey.business.pojo.dto.quota.ProjectQuotaImportDTO;
import com.monetware.ringsurvey.business.pojo.dto.quota.QuotaExportDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectQuota;
import com.monetware.ringsurvey.business.pojo.vo.quota.*;
import com.monetware.ringsurvey.business.service.project.QuestionnaireService;
import com.monetware.ringsurvey.business.service.project.QuotaService;
import com.monetware.ringsurvey.survml.quota.QuotaCompileResult;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("配额")
@RestController
@RequestMapping("/w1/quota")
public class QuotaController {

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @PostMapping("list")
    @ApiOperation(value = "配额列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QUOTA_LIST, cp = AuthorizedConstants.RC_OPEN_QUOTA)
    public ResultData<PageList> getProjectQuotaList(@RequestBody ProjectQuotaListVO quotaListVO) {
        return new ResultData<>(0, "获取成功", quotaService.getProjectQuotaList(quotaListVO));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存/更新配额")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QUOTA_ADD, cp = AuthorizedConstants.RC_OPEN_QUOTA)
    public ResultData<Integer> saveProjectQuota(@RequestBody ProjectQuotaVO quotaVO) {
        return new ResultData<>(0, "保存成功", quotaService.saveProjectQuota(quotaVO));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除配额")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QUOTA_DELETE, cp = AuthorizedConstants.RC_OPEN_QUOTA)
    public ResultData<Integer> deleteProjectQuota(@RequestBody QuotaDeleteVO deleteVO) {
        return new ResultData<>(0, "删除成功", quotaService.deleteProjectQuota(deleteVO));
    }

    @PostMapping("detail")
    @ApiOperation(value = "配额详情")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QUOTA_LIST, cp = AuthorizedConstants.RC_OPEN_QUOTA)
    public ResultData<BaseProjectQuota> getQuotaDetail(@RequestBody QuotaDetailVO detailVO) {
        return new ResultData<>(0, "查询成功", quotaService.getQuotaDetail(detailVO.getQuotaId()));
    }

    @PostMapping("status/update")
    @ApiOperation("更新配额状态")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QUOTA_EDIT, cp = AuthorizedConstants.RC_OPEN_QUOTA)
    public ResultData<Integer> updateQuotaStatus(@RequestBody ProjectQuotaStatusVO statusVO) {
        return new ResultData<>(0, "更新成功", quotaService.updateQuotaStatus(statusVO));
    }

    @PostMapping("export")
    @ApiOperation(value = "导出配额")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QUOTA_EXPORT, cp = AuthorizedConstants.RC_OPEN_QUOTA)
    public ResultData<List<QuotaExportDTO>> exportQuota(@RequestBody QuotaExportVO exportVO) {
        return new ResultData<>(0, "导出成功", quotaService.exportQuota(exportVO));
    }

    @PostMapping("import")
    @ApiOperation(value = "导入配额")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QUOTA_ADD, cp = AuthorizedConstants.RC_OPEN_QUOTA)
    public ResultData<ProjectQuotaImportDTO> importQuota(@RequestBody BatchImportQuotaVO batchImportQuotaVO) {
        return new ResultData<>(0, "导入成功", quotaService.insertQuotaByImport(batchImportQuotaVO));
    }

    @PostMapping("qnaire/{projectId}")
    @ApiOperation(value = "配额页面查询问卷模块")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QUOTA_EDIT, cp = AuthorizedConstants.RC_OPEN_QUOTA)
    public ResultData<List<QuestionSelectedDTO>> getQuotaQuestionSelectedList(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "查询成功", questionnaireService.getQuotaQuestionSelectedList(projectId));
    }

    @PostMapping("validate")
    @ApiOperation(value = "验证配额表达式合法性")
    @MonetwareAuthorize(pm = AuthorizedConstants.RQ_QUOTA_EDIT, cp = AuthorizedConstants.RC_OPEN_QUOTA)
    public ResultData<QuotaCompileResult> validateQuota(@RequestBody ValidateQuotaVO quotaVO) {
        return new ResultData<>(0, "验证结束", quotaService.validateQuotaExpression(quotaVO));
    }

    @PostMapping("property/{projectId}")
    @ApiOperation(value = "获取样本标识转换值")
    public ResultData<HashMap<Object, String>> getMarkProperty(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "获取成功", quotaService.getMarkProperty(projectId));
    }
}
