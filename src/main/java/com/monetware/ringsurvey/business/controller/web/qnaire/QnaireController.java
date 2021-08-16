package com.monetware.ringsurvey.business.controller.web.qnaire;

import com.monetware.ringsurvey.business.pojo.dto.qnaire.MyQnaireLabelListDTO;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QnairePreviewDTO;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QnaireResourceDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseQnaireResource;
import com.monetware.ringsurvey.business.pojo.po.BaseQuestionnaire;
import com.monetware.ringsurvey.business.pojo.vo.qnaire.QnairePreviewVO;
import com.monetware.ringsurvey.business.pojo.vo.questionnaire.*;
import com.monetware.ringsurvey.business.service.qnaire.QnaireService;
import com.monetware.ringsurvey.survml.questions.Page;
import com.monetware.ringsurvey.system.base.PageList;
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
@Api("问卷设计")
@RestController
@RequestMapping("/w1/questionnaire")
public class QnaireController {

    @Autowired
    private QnaireService myQnaireSerive;

    @PostMapping("list")
    @ApiOperation(value = "查询我的问卷列表")
    public ResultData<PageList<Page>> getMyQnaireList(@RequestBody MyQnaireListVO myQnaireListVO) {
        return new ResultData("获取问卷列表成功", myQnaireSerive.getMyQnaireList(myQnaireListVO));
    }

    @PostMapping("label/list")
    @ApiOperation(value = "查询我的问卷标签列表")
    public ResultData<List<MyQnaireLabelListDTO>> getMyQnaireList() {
        return new ResultData("获取标签列表成功", myQnaireSerive.getMyQnaireLabelList());
    }

    @PostMapping("save")
    @ApiOperation(value = "保存我的问卷")
    public ResultData<Map<String, Object>> saveMyQnaire(@RequestBody MyQuestionnaireVO myQuestionnaireVO) {
        return new ResultData<>("保存问卷成功", myQnaireSerive.saveMyQuestionnaire(myQuestionnaireVO));
    }

    @PostMapping("get")
    @ApiOperation(value = "获取问卷XML")
    public ResultData<BaseQuestionnaire> getMyQnaireXmlContent(@RequestBody QuestionnaireEditVO editVO) {
        return new ResultData("获取成功", myQnaireSerive.getMyQnaireXmlContent(editVO));
    }

    @GetMapping("export")
    @ApiOperation(value = "导出问卷")
    public void exportQuestionnaire(String fileType, Integer questionnaireId, Integer projectId, Integer moduleId, HttpServletResponse response) {
        QuestionnaireExportVO exportVO = new QuestionnaireExportVO();
        exportVO.setFileType(fileType);
        exportVO.setQuestionnaireId(questionnaireId);
        exportVO.setProjectId(projectId);
        exportVO.setModuleId(moduleId);
        myQnaireSerive.exportQuestionnaire(exportVO, response);
    }

    @PostMapping("delete/{questionnaireId}")
    @ApiOperation(value = "删除我的问卷")
    public ResultData<Integer> deleteMyQnaire(@PathVariable("questionnaireId") Integer questionnaireId) {
        return new ResultData("删除成功", myQnaireSerive.deleteMyQnaire(questionnaireId));
    }

    @PostMapping("resource/list")
    @ApiOperation(value = "问卷资源库")
    public ResultData<List<QnaireResourceDTO>> getQnaireResourceList() {
        return new ResultData("获取问卷资源成功", myQnaireSerive.getQnaireResourceList());
    }

    @PostMapping("resource/search")
    @ApiOperation(value = "问卷资源库搜索")
    public ResultData<List<BaseQnaireResource>> getResourceSearchList(@RequestBody QnaireResourceSearchVO searchVO) {
        return new ResultData("搜索问卷资源成功", myQnaireSerive.getResourceSearchList(searchVO));
    }

    @PostMapping("resource/upload")
    @ApiOperation(value = "上传问卷资源")
    public ResultData<List<BaseQnaireResource>> uploadResource(@RequestBody MultipartFile file, Integer type,
                                                               String fileName, String tagIds) {
        return new ResultData("上传资源成功", myQnaireSerive.uploadResource(file, type, fileName, tagIds));
    }

    @PostMapping("resource/replace")
    @ApiOperation(value = "替换问卷资源")
    public ResultData<List<BaseQnaireResource>> replaceResource(@RequestBody MultipartFile file, Integer type,
                                                                String fileName, String tagIds, Integer sourceId) {
        return new ResultData("替换资源成功", myQnaireSerive.replaceResource(file, type, fileName, tagIds, sourceId));
    }

    @PostMapping("resource/update")
    @ApiOperation(value = "修改问卷资源")
    public ResultData<Integer> updateResource(@RequestBody BaseQnaireResource resource) {
        return new ResultData("修改成功", myQnaireSerive.updateResource(resource));
    }

    @PostMapping("resource/delete/{id}")
    @ApiOperation(value = "删除问卷资源")
    public ResultData<Integer> getResourceSearchList(@PathVariable("id") Integer id) {
        return new ResultData("删除成功", myQnaireSerive.deleteResource(id));
    }

    @PostMapping("resource/tag/add")
    @ApiOperation(value = "问卷资源标签新增")
    public ResultData<Integer> addQnaireResourceTag(@RequestBody QnaireResourceTagVO tagVO) {
        return new ResultData("新增资源标签成功", myQnaireSerive.addQnaireResourceTag(tagVO));
    }

    @PostMapping("resource/tag/delete/{tagId}")
    @ApiOperation(value = "问卷资源标签删除")
    public ResultData<Integer> deleteQnaireResourceTag(@PathVariable("tagId") Integer tagId) {
        return new ResultData("删除资源标签成功", myQnaireSerive.deleteQnaireResourceTag(tagId));
    }

    @PostMapping("file/upload")
    @ApiOperation(value = "上传logo")
    public ResultData<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return new ResultData("上传成功", myQnaireSerive.uploadFile(file));
    }

    @PostMapping("file/delete")
    @ApiOperation(value = "删除logo")
    public ResultData<Boolean> deleteFile(@RequestBody QnaireFileDelVO delVO) {
        return new ResultData("删除成功", myQnaireSerive.deleteFile(delVO.getFileName()));
    }

    @PostMapping("preview")
    @ApiOperation(value = "问卷预览")
    public ResultData<QnairePreviewDTO> previewQnaire(@RequestBody QnairePreviewVO previewVO) {
        return new ResultData("查询成功", myQnaireSerive.previewQnaire(previewVO));
    }
}
