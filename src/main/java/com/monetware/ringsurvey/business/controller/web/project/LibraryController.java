package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.dto.library.LibraryDetailDTO;
import com.monetware.ringsurvey.business.pojo.dto.library.PubDetailDTO;
import com.monetware.ringsurvey.business.pojo.dto.library.QnaireCommentsDTO;
import com.monetware.ringsurvey.business.pojo.dto.library.QnaireShareDTO;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionnaireInitDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseQuestionnaire;
import com.monetware.ringsurvey.business.pojo.vo.library.CommentsInfoVO;
import com.monetware.ringsurvey.business.pojo.vo.library.FavoriteVO;
import com.monetware.ringsurvey.business.pojo.vo.library.LibraryListVO;
import com.monetware.ringsurvey.business.pojo.vo.library.QnaireShareVO;
import com.monetware.ringsurvey.business.service.project.LibraryService;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Linked
 * @date 2020/5/27 18:53
 */
@Api("问卷市场")
@RestController
@RequestMapping("/w1/library")
public class LibraryController {


    @Autowired
    private LibraryService libraryService;

    @PostMapping("init/{questionnaireId}")
    @ApiOperation(value = "初始化")
    public ResultData<QuestionnaireInitDTO> getInitInfo(@PathVariable("questionnaireId") Integer questionnaireId) {
        return new ResultData<>(0, "发布成功", libraryService.getInitInfo(questionnaireId));
    }

    @PostMapping("pub")
    @ApiOperation(value = "发布问卷市场")
    public ResultData<Integer> saveLibrary(@RequestBody MultipartFile[] file, Integer questionnaireId, BigDecimal price, String description, Integer ifFree) {
        return new ResultData<>(0, "发布成功", libraryService.saveLibrary(file, questionnaireId, price, description, ifFree));
    }

    @PostMapping("list")
    @ApiOperation(value = "问卷市场列表")
    public ResultData<PageList> getLibraryList(@RequestBody LibraryListVO listVO) {
        return new ResultData<>(0, "获取成功", libraryService.getLibraryList(listVO));
    }

    @PostMapping("detail/{id}")
    @ApiOperation(value = "问卷详情")
    public ResultData<LibraryDetailDTO> getLibraryDetail(@PathVariable("id") Integer id) {
        return new ResultData<>(0, "获取成功", libraryService.getLibraryDetail(id));
    }

    @PostMapping("pub/detail/{questionnaireId}")
    @ApiOperation(value = "发布详情")
    public ResultData<PubDetailDTO> getPubDetail(@PathVariable("questionnaireId") Integer questionnaireId) {
        return new ResultData<>(0, "获取成功", libraryService.getPubDetail(questionnaireId));
    }

    @PostMapping("off/{questionnaireId}")
    @ApiOperation(value = "撤销问卷市场")
    public ResultData<Integer> offMarket(@PathVariable("questionnaireId") Integer questionnaireId) {
        return new ResultData<>(0, "撤销成功", libraryService.deleteLibrary(questionnaireId));
    }

    @PostMapping("free")
    @ApiOperation(value = "免费使用问卷模板余数")
    public ResultData<Integer> getFreeNum() {
        return new ResultData<>(0, "获取成功", libraryService.getFreeNum());
    }

    @PostMapping("copy/{libraryId}")
    @ApiOperation(value = "复制问卷模板")
    public ResultData<Integer> saveTemplate(@PathVariable("libraryId") Integer libraryId) {
        return new ResultData<>(0, "复制成功", libraryService.insertQuestionnaire(libraryId));
    }

    @PostMapping("/comment")
    @ApiOperation(value = "问卷评论")
    public ResultData<Integer> spiderComments(@RequestBody CommentsInfoVO commentsInfoVO) {
        return new ResultData<>(0, "评论成功", libraryService.saveCommentsInfo(commentsInfoVO));
    }

    @PostMapping("/comment/get/{libraryId}")
    @ApiOperation(value = "获取问卷评论")
    public ResultData<List<QnaireCommentsDTO>> getComments(@PathVariable("libraryId") Integer libraryId) {
        return new ResultData<>(0, "获取成功", libraryService.getCommentsInfo(libraryId));
    }

    @PostMapping("star")
    @ApiOperation(value = "收藏/取消收藏")
    public ResultData<Integer> doStar(@RequestBody FavoriteVO favoriteVO) {
        return new ResultData<>(0, "获取成功", libraryService.saveStar(favoriteVO));
    }

    @PostMapping("share")
    @ApiOperation(value = "我分享的问卷")
    public ResultData<List<QnaireShareDTO>> myShare(@RequestBody QnaireShareVO shareVO) {
        return new ResultData<>(0, "获取成功", libraryService.getMyQnaire(shareVO));
    }


}
