package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.response.InterviewerDTO;
import com.monetware.ringsurvey.business.pojo.dto.response.ResponseAuditEditDTO;
import com.monetware.ringsurvey.business.pojo.dto.response.ResponseAuditInfoDTO;
import com.monetware.ringsurvey.business.pojo.dto.response.SearchInfoDTO;
import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.business.pojo.vo.response.*;
import com.monetware.ringsurvey.business.service.project.AnswerService;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("答卷")
@RestController
@RequestMapping("/w1/answer")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PostMapping("list")
    @ApiOperation(value = "答卷列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_LIST)
    public ResultData<PageList> getResponseList(@RequestBody ResponseListVO responseListVO) {
        return new ResultData<>(0, "查询成功", answerService.getResponseList(responseListVO));
    }

    @PostMapping("search/{projectId}")
    @ApiOperation(value = "搜索条件")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_LIST)
    public ResultData<SearchInfoDTO> getSearchInfo(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "查询成功", answerService.getSearchInfo(projectId));
    }

    @PostMapping("interviewer/list/{projectId}")
    @ApiOperation(value = "访问员列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_LIST)
    public ResultData<List<InterviewerDTO>> getInterviewerList(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "查询成功", answerService.getInterviewerList(projectId));
    }

    @PostMapping("sampling")
    @ApiOperation(value = "抽样审核")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_AUDIT)
    public ResultData<Integer> updateResponseSampling(@RequestBody ResponseSamplingVO samplingVO) {
        return new ResultData<>(0, "查询成功", answerService.updateResponseSampling(samplingVO));
    }

    @PostMapping("init")
    @ApiOperation(value = "重置抽审")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_AUDIT)
    public ResultData<Integer> updateResponseInit(@RequestBody BaseVO baseVO) {
        return new ResultData<>(0, "重置成功", answerService.updateResponseInit(baseVO));
    }

    @PostMapping("audio/list")
    @ApiOperation(value = "录音列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_VOICE)
    public ResultData<PageList> getAudioList(@RequestBody ResponseFileListVO audioListVO) {
        return new ResultData<>(0, "查询成功", answerService.getAudioList(audioListVO));
    }

    @PostMapping("atta/list")
    @ApiOperation(value = "附件列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_FILE)
    public ResultData<PageList> getResponseFileList(@RequestBody ResponseFileListVO fileListVO) {
        return new ResultData<>(0, "查询成功", answerService.getResponseFileList(fileListVO));
    }

//    @PostMapping("file/delete")
//    @ApiOperation(value = "录音/附件删除(批量删除)")
//    public ResultData<Integer> deleteResponseFile(@RequestBody DeleteFileVO deleteFileVO) {
//        return new ResultData<>(0, "删除成功", answerService.deleteResponseFile(deleteFileVO));
//    }

    @PostMapping("audit/info")
    @ApiOperation(value = "审核页面相关信息")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_LIST)
    public ResultData<ResponseAuditInfoDTO> getResponseAuditInfo(@RequestBody ResponseAuditInfoVO infoVO) {
        return new ResultData<>(0, "查询成功", answerService.getResponseAuditInfo(infoVO));
    }

    @PostMapping("batch/export")
    @ApiOperation(value = "导出答卷")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_EXPORT)
    public ResultData<Integer> exportResponse(@RequestBody ResponseExportVO exportVO) throws Exception {
        return new ResultData<>("导出成功", answerService.exportResponse(exportVO));
    }

    @PostMapping("export/list")
    @ApiOperation(value = "导出文件列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_EXPORT)
    public ResultData<PageList> getResponseDownLoadList(@RequestBody PageParam pageParam) {
        return new ResultData<>(0, "查询成功", answerService.getResponseDownLoadList(pageParam));
    }

    @PostMapping("file/delete")
    @ApiOperation(value = "删除文件")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_EXPORT)
    public ResultData<Integer> deleteResponseFile(@RequestBody ResponseExportDelVO delVO) {
        return new ResultData<>("删除成功", answerService.deleteResponseFile(delVO));
    }

    @PostMapping("file/upload/{projectId}")
    @ApiOperation(value = "答卷编辑修改文件")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_AUDIT)
    public ResultData<Map> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("projectId") Integer projectId) {
        return new ResultData("上传成功", answerService.uploadFile(file, projectId));
    }

    @PostMapping("file/edit/delete")
    @ApiOperation(value = "答卷编辑删除文件")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_AUDIT)
    public ResultData<Boolean> deleteFile(@RequestBody ResponseDelVO delVO) {
        return new ResultData("删除成功", answerService.deleteFile(delVO));
    }

    @PostMapping("audit")
    @ApiOperation(value = "答卷审核")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_AUDIT)
    public ResultData<Integer> saveResponseAudit(@RequestBody ResponseAuditVO auditVO) {
        return new ResultData<>(0, "审核成功", answerService.saveResponseAudit(auditVO));
    }

    @PostMapping("history/list")
    @ApiOperation(value = "历史答卷")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_LIST)
    public ResultData<PageList> getResponseHistory(@RequestBody ResponseHistoryListVO historyListVO) {
        return new ResultData<>(0, "获取成功", answerService.getResponseHistory(historyListVO));
    }

    @PostMapping("audit/batch")
    @ApiOperation(value = "批量审核")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_AUDIT)
    public ResultData<Integer> insertBatchResponseAudit(@RequestBody BatchAuditVO auditVO) {
        return new ResultData<>(0, "审核成功", answerService.insertBatchResponseAudit(auditVO));
    }

    @PostMapping("audit/note")
    @ApiOperation(value = "添加/编辑批注")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_AUDIT)
    public ResultData<Integer> saveAuditNotes(@RequestBody ResponseAuditNoteVO noteVO) {
        return new ResultData<>(0, "保存成功", answerService.saveAuditNotes(noteVO));
    }

    @GetMapping("download")
    @ApiOperation(value = "下载录音/附件/导出文件")
    public void downloadFile(Integer id, Integer projectId, Integer type, HttpServletResponse response) throws Exception {
        answerService.downloadFile(id, projectId, type, response);
    }


    @PostMapping("audit/edit")
    @ApiOperation(value = "答卷审核编辑")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_AUDIT)
    public ResultData<ResponseAuditEditDTO> auditEditResponse(@RequestBody ResponseAuditEditVO auditEditVO) {
        return new ResultData<>("查询成功", answerService.auditEditResponse(auditEditVO));
    }

    @PostMapping("reset")
    @ApiOperation(value = "答卷重置")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_EDIT, cp = AuthorizedConstants.RC_ANSWER_RECALL)
    public ResultData<Integer> resetResponse(@RequestBody ResponseResetVO resetVO) {
        return new ResultData<>("重置成功", answerService.resetResponse(resetVO));
    }

    @PostMapping("replace")
    @ApiOperation(value = "答卷替换")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_EDIT, cp = AuthorizedConstants.RC_ANSWER_RECALL)
    public ResultData<Integer> replaceResponse(@RequestBody ResponseReplaceVO replaceVO) {
        return new ResultData<>("替换成功", answerService.replaceResponse(replaceVO));
    }

    @PostMapping("export/default/{projectId}")
    @ApiOperation(value = "导出默认属性")
    @MonetwareAuthorize(pm = AuthorizedConstants.RA_ANSWER_EXPORT)
    public ResultData<List<String>> getExportDefaultProperty(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", answerService.getExportDefaultProperty(projectId));
    }

    @GetMapping("export/audio/download")
    @ApiOperation(value = "批量导出录音")
    public void getExportBatchAudio(Integer projectId, String zipName, HttpServletResponse response) throws Exception {
        answerService.exportBatchAudio(projectId, zipName, response);
    }

}
