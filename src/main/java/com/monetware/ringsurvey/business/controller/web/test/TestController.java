package com.monetware.ringsurvey.business.controller.web.test;

import com.monetware.ringsurvey.business.service.project.ProjectService;
import com.monetware.ringsurvey.business.service.qnaire.QnaireService;
import com.monetware.ringsurvey.business.service.user.UserService;
import com.monetware.ringsurvey.survml.questions.Page;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api("测试运行")
@RestController
@RequestMapping("/w1/test")
public class TestController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private QnaireService myQnaireSerive;

    @Autowired
    private UserService userService;

    @PostMapping("project/list")
    @ApiOperation(value = "查询项目列表")
    public ResultData<PageList> getProjectListTest() {
        return new ResultData("获取成功", projectService.getProjectListTest());
    }

    @PostMapping("project/count")
    @ApiOperation(value = "获取项目个数")
    public ResultData<List<Map<String, Integer>>> getProjectCountByUserIdTest() {
        return new ResultData<>("查询成功", projectService.getProjectCountByUserIdTest());
    }

    @PostMapping("qnaire/list")
    @ApiOperation(value = "查询我的问卷列表")
    public ResultData<PageList<Page>> getMyQnaireListTest() {
        return new ResultData("获取问卷列表成功", myQnaireSerive.getMyQnaireListTest());
    }

    @PostMapping("record/list")
    @ApiOperation(value = "查询用户消费记录")
    public ResultData<PageList> getUserBuyRecordTest() {
        return new ResultData<>(0, "查询成功", userService.getUserBuyRecordTest());
    }

}
