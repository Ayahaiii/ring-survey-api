package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.sample.ProjectSampleDTO;
import com.monetware.ringsurvey.business.pojo.dto.sample.SampleAssignNameDTO;
import com.monetware.ringsurvey.business.pojo.dto.sample.SamplePropertyDTO;
import com.monetware.ringsurvey.business.pojo.dto.sample.TeamMemberDTO;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.business.pojo.vo.sample.*;
import com.monetware.ringsurvey.business.service.project.SampleService;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
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
@Api("样本")
@RestController
@RequestMapping("/w1/sample")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @PostMapping("get/town")
    @ApiOperation(value = "获取镇列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ADD)
    public ResultData<List<String>> getTownNameList(@RequestBody TownVO townVO) {
        return new ResultData<>("获取成功", sampleService.getTownNameList(townVO));
    }

    @PostMapping("save")
    @ApiOperation(value = "添加或编辑研究对象")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ADD)
    public ResultData<Integer> saveProjectSample(@RequestBody ProjectSampleVO projectSampleVO) {
        return new ResultData(0, "保存成功", sampleService.saveProjectSample(projectSampleVO));
    }

    @PostMapping("list")
    @ApiOperation(value = "研究对象列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<PageList> projectSampleList(@RequestBody SampleListVO sampleListVO) {
        return new ResultData<>(0, "查询成功", sampleService.getSampleList(sampleListVO));
    }

    @PostMapping("batch/import")
    @ApiOperation(value = "导入样本")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ADD)
    public ResultData<List<ProjectSampleVO>> insertSampleByImport(@RequestBody SampleImportVO importVO) {
        return new ResultData<>("导入成功", sampleService.insertSampleByImport(importVO));
    }

    @PostMapping("batch/update")
    @ApiOperation(value = "更新样本")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ADD)
    public ResultData<List<ProjectSampleVO>> updateSampleByImport(@RequestBody SampleImportVO importVO) {
        return new ResultData<>("导入成功", sampleService.updateSampleByImport(importVO));
    }

    @PostMapping("batch/export")
    @ApiOperation(value = "导出样本")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_EXPORT)
    public ResultData<Integer> exportTeamUser(@RequestBody SampleExportVO exportVO) throws Exception {
        return new ResultData<>("导出成功", sampleService.exportSample(exportVO));
    }

    @PostMapping("export/list")
    @ApiOperation(value = "导出文件列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_EXPORT)
    public ResultData<PageList> getSampleDownList(@RequestBody PageParam pageParam) {
        return new ResultData<>(0, "查询成功", sampleService.getSampleDownList(pageParam));
    }

    @GetMapping("download")
    @ApiOperation(value = "下载样本文件")
    public void downloadSample(Integer id, HttpServletResponse response) throws Exception {
        sampleService.downloadSample(id, response);
    }

    @PostMapping("file/delete")
    @ApiOperation(value = "删除文件")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_EXPORT)
    public ResultData<Integer> deleteSampleFile(@RequestBody SampleExportDelVO delVO) {
        return new ResultData<>("删除成功", sampleService.deleteSampleFile(delVO));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除和批量删除研究对象")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_DELETE)
    public ResultData<Integer> deleteProjectSample(@RequestBody DeleteSampleVO deleteSampleVO) {
        return new ResultData<>(0, "删除成功", sampleService.deleteProjectSample(deleteSampleVO));
    }

    @PostMapping("detail")
    @ApiOperation(value = "研究对象详情")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_VIEW)
    public ResultData<ProjectSampleDTO> getComments(@RequestBody ProjectSampleDetailVO detailVO) {
        return new ResultData<>(0, "查询成功", sampleService.getSampleDetail(detailVO));
    }

    @PostMapping("get/assign")
    @ApiOperation(value = "获取分派详情")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_VIEW)
    public ResultData<List<SampleAssignNameDTO>> getSampleAssign(@RequestBody ProjectSampleDetailVO detailVO) {
        return new ResultData<>(0, "查询成功", sampleService.getSampleAssign(detailVO));
    }

    @PostMapping("member")
    @ApiOperation(value = "查询所有团队成员")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ASSIGN_ADMIN)
    public ResultData<List<TeamMemberDTO>> getTeamMember(@RequestBody TeamMemberSearchVO teamMemberVO) {
        return new ResultData<>(0, "查询成功", sampleService.getTeamMember(teamMemberVO));
    }

    @PostMapping("check/list")
    @ApiOperation(value = "根据访问员权限查询样本")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ASSIGN_ADMIN)
    public ResultData<List<BaseProjectSample>> getSampleAssignList(@RequestBody SampleAssignVO assignVO) {
        return new ResultData<>(0, "查询成功", sampleService.getSampleAssignList(assignVO));
    }

    @PostMapping("assign")
    @ApiOperation(value = "分派团队成员")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ASSIGN_ADMIN)
    public ResultData<Integer> assignTeam(@RequestBody AssignTeamVO assignTeamVO) {
        return new ResultData<>(0, "分派成功", sampleService.insertAssign(assignTeamVO));
    }

    @PostMapping("assign/update")
    @ApiOperation(value = "修改分派团队成员")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ASSIGN_ADMIN)
    public ResultData<Integer> updateTeam(@RequestBody AssignTeamVO assignTeamVO) {
        return new ResultData<>(0, "更新成功", sampleService.updateAssign(assignTeamVO));
    }

    @PostMapping("assign/import")
    @ApiOperation(value = "批量上传分派")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ASSIGN_ADMIN)
    public ResultData<Integer> insertBatchAssign(@RequestBody SampleImportAssignListVO assignListVO) {
        return new ResultData<>(0, "分派成功", sampleService.insertBatchAssign(assignListVO));
    }

    @PostMapping("/property/project")
    @ApiOperation(value = "添加/修改项目属性")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<Integer> saveProjectProperty(@RequestBody ProjectPropertyVO propertyVO) {
        return new ResultData<>(0, "添加成功", sampleService.saveProjectProperty(propertyVO));
    }

    @PostMapping("/property/save")
    @ApiOperation(value = "项目属性别名")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<Integer> saveProjectPropertyAlisa(@RequestBody SamplePropertySaveVO samplePropertySaveVO) {
        return new ResultData<>(0, "修改成功", sampleService.saveProjectPropertyAlisa(samplePropertySaveVO));
    }

    @PostMapping("/property/get/{projectId}")
    @ApiOperation(value = "获取项目属性")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<BaseProjectProperty> getProjectSampleProperty(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "获取成功", sampleService.getProjectSampleProperty(projectId));
    }

    @PostMapping("/property/check/{projectId}")
    @ApiOperation(value = "获取使用属性")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<SamplePropertyDTO> getSampleProperty(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "保存成功", sampleService.getSampleProperty(projectId));
    }

    @PostMapping("/property/template/save")
    @ApiOperation(value = "保存项目属性模板")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<Integer> savePropertyTemplate(@RequestBody PropertyTemplateVO templateVO) {
        return new ResultData<>(0, "保存成功", sampleService.savePropertyTemplate(templateVO));
    }

    @PostMapping("/property/template/delete")
    @ApiOperation(value = "删除项目属性模板")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<Integer> deletePropertyTemplate(@RequestBody TemplateDeleteVO templateDeleteVO) {
        return new ResultData<>(0, "删除成功", sampleService.deletePropertyTemplate(templateDeleteVO));
    }

    @PostMapping("/property/template/list")
    @ApiOperation(value = "查询项目属性模板")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<List<BaseProjectPropertyTemplate>> getPropertyTemplateList(@RequestBody BaseVO baseVO) {
        return new ResultData<>(0, "查询成功", sampleService.getPropertyTemplate());
    }

    @PostMapping("/extra/get")
    @ApiOperation(value = "获取样本附加数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_OUTDATA_ADMIN, cp = AuthorizedConstants.RC_EXTRA_DATA)
    public ResultData<BaseProjectSampleOutData> getSampleOutData(@RequestBody SampleSearchMoreVO searchVO) {
        return new ResultData<>(0, "获取成功", sampleService.getSampleOutData(searchVO));
    }

    @PostMapping("/extra/save")
    @ApiOperation(value = "保存 样本附加数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_OUTDATA_ADMIN, cp = AuthorizedConstants.RC_EXTRA_DATA)
    public ResultData<Integer> updateSampleOutData(@RequestBody SampleOutDataSaveVO outDataSaveVO) {
        return new ResultData<>(0, "保存成功", sampleService.updateSampleOutData(outDataSaveVO));
    }

    @PostMapping("/extra/import")
    @ApiOperation(value = "导入 样本附加数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_OUTDATA_ADMIN, cp = AuthorizedConstants.RC_EXTRA_DATA)
    public ResultData<Integer> updateSampleOutDataImport(@RequestBody SampleOutDataSaveVO outDataSaveVO) {
        return new ResultData<>(0, "导入成功", sampleService.updateSampleOutDataImport(outDataSaveVO));
    }

    @PostMapping("/address/get")
    @ApiOperation(value = "获取样本地址数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<List<BaseProjectSampleAddress>> getSampleMoreAddress(@RequestBody SampleSearchMoreVO searchVO) {
        return new ResultData<>(0, "获取成功", sampleService.getSampleMoreAddress(searchVO, 0));
    }

    @PostMapping("/address/save")
    @ApiOperation(value = "保存/导入 样本地址数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<Integer> saveSampleMoreAddress(@RequestBody SampleMoreAddressListVO addressListVO) {
        return new ResultData<>(0, "操作成功", sampleService.saveSampleMoreAddress(addressListVO));
    }

    @PostMapping("/address/update")
    @ApiOperation(value = "修改 样本地址数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<Integer> updateSampleMoreAddress(@RequestBody SampleMoreAddressVO addressVO) {
        return new ResultData<>(0, "修改成功", sampleService.updateSampleMoreAddress(addressVO));
    }

    @PostMapping("/address/delete")
    @ApiOperation(value = "删除 样本地址数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<Integer> deleteSampleMoreAddress(@RequestBody SampleDelMoreVO delVO) {
        return new ResultData<>(0, "删除成功", sampleService.deleteSampleMoreAddress(delVO));
    }

    @PostMapping("/contact/get")
    @ApiOperation(value = "获取样本联系数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<List<BaseProjectSampleContact>> getSampleMoreContact(@RequestBody SampleSearchMoreVO searchVO) {
        return new ResultData<>(0, "获取成功", sampleService.getSampleMoreContact(searchVO, 0));
    }

    @PostMapping("/contact/save")
    @ApiOperation(value = "保存/导入 样本联系数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<Integer> saveSampleMoreContact(@RequestBody SampleMoreContactListVO contactListVO) {
        return new ResultData<>(0, "操作成功", sampleService.saveSampleMoreContact(contactListVO));
    }

    @PostMapping("/contact/update")
    @ApiOperation(value = "修改 样本联系数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<Integer> updateSampleMoreContact(@RequestBody SampleMoreContactVO contactVO) {
        return new ResultData<>(0, "修改成功", sampleService.updateSampleMoreContact(contactVO));
    }

    @PostMapping("/contact/delete")
    @ApiOperation(value = "删除 样本联系数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<Integer> deleteSampleMoreContact(@RequestBody SampleDelMoreVO delVO) {
        return new ResultData<>(0, "删除成功", sampleService.deleteSampleMoreContact(delVO));
    }

    @PostMapping("/touch/get")
    @ApiOperation(value = "获取样本接触数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<List<BaseProjectSampleTouch>> getSampleMoreTouch(@RequestBody SampleSearchMoreVO searchVO) {
        return new ResultData<>(0, "获取成功", sampleService.getSampleMoreTouch(searchVO, 0));
    }

    @PostMapping("/touch/save")
    @ApiOperation(value = "保存 样本接触数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<Integer> saveSampleMoreTouch(@RequestBody SampleMoreTouchVO touchVO) {
        return new ResultData<>(0, "操作成功", sampleService.saveSampleMoreTouch(touchVO));
    }

    @PostMapping("/touch/delete")
    @ApiOperation(value = "删除 样本接触数据")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_MOREDATA_ADMIN, cp = AuthorizedConstants.RC_MORE_DATA)
    public ResultData<Integer> deleteSampleMoreTouch(@RequestBody SampleDelMoreVO delVO) {
        return new ResultData<>(0, "删除成功", sampleService.deleteSampleMoreTouch(delVO));
    }

    @PostMapping("/recycle")
    @ApiOperation(value = "回收样本")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<Integer> deleteSampleByRecycle(@RequestBody SampleRecycleVO recycleVO) {
        return new ResultData<>(0, "回收成功", sampleService.deleteSampleByRecycle(recycleVO));
    }

    @PostMapping("/clear")
    @ApiOperation(value = "清空未接触样本")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<Integer> clearInitial(@RequestBody ClearInitialVO clearInitialVO) {
        return new ResultData<>(0, "清除成功", sampleService.deleteSampleByTouch(clearInitialVO));
    }

    @PostMapping("/duplicate")
    @ApiOperation(value = "去除重复样本")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<Integer> deleteSampleByRepeat(@RequestBody SampleRepeatVO repeatVO) {
        return new ResultData<>(0, "去重成功", sampleService.deleteSampleByRepeat(repeatVO));
    }

    @PostMapping("/disable")
    @ApiOperation(value = "禁用样本")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_ADD)
    public ResultData<Integer> disableSample(@RequestBody SampleDisabledVO disabledVO) {
        return new ResultData<>(0, "禁用成功", sampleService.disableSample(disabledVO));
    }

    @PostMapping("/rule/list")
    @ApiOperation(value = "号码组合规则列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<PageList> getNumberRuleList(@RequestBody PageParam pageParam) {
        return new ResultData<>(0, "获取成功", sampleService.getNumberRuleList(pageParam));
    }

    @PostMapping("/rule/get/{ruleId}")
    @ApiOperation(value = "号码组合规则列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<BaseNumberRule> getNumberRule(@PathVariable Integer ruleId) {
        return new ResultData<>(0, "获取成功", sampleService.getNumberRule(ruleId));
    }

    @PostMapping("/rule/save")
    @ApiOperation(value = "保存号码组合规则")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<Integer> saveNumberRule(@RequestBody NumberRuleSaveVO ruleSaveVO) {
        return new ResultData<>(0, "保存成功", sampleService.saveNumberRule(ruleSaveVO));
    }

    @PostMapping("/rule/delete/{ruleId}")
    @ApiOperation(value = "删除号码组合规则")
    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<Integer> deleteNumberRule(@PathVariable Integer ruleId) {
        return new ResultData<>(0, "删除成功", sampleService.deleteNumberRule(ruleId));
    }

    @PostMapping("/status/set")
    @ApiOperation(value = "设置样本状态")
//    @MonetwareAuthorize(pm = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<Integer> setSampleStatus(@RequestBody SampleStatusSetVO statusSetVO) {
        return new ResultData<>(0, "设置成功", sampleService.setSampleStatus(statusSetVO));
    }

}
