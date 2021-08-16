package com.monetware.ringsurvey.business.controller;

import com.monetware.ringsurvey.business.pojo.po.BaseAppVersion;
import com.monetware.ringsurvey.business.pojo.vo.app.AppVersionVO;
import com.monetware.ringsurvey.business.service.AppService;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("APP管理")
@RestController
@RequestMapping("/w1/app")
public class AppController {

    @Autowired
    private AppService appService;

    @PostMapping("get")
    @ApiOperation(value = "获取APK的URL")
    public ResultData<String> getAppVersion(){
        return new ResultData<>("获取成功", appService.getAppVersion());
    }

    @PostMapping("upload")
    @ApiOperation(value = "上传APK")
    public ResultData<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return new ResultData("上传成功", appService.uploadFile(file));
    }

    @PostMapping("insert")
    @ApiOperation(value = "保存安卓APK")
    public ResultData<Integer> insertAppVersion(@RequestBody AppVersionVO appVersionVO){
        return new ResultData<>("保存成功", appService.insertAppVersion(appVersionVO));
    }

    @PostMapping("list")
    @ApiOperation(value = "获取安卓APK列表")
    public ResultData<PageList> getAppVersionList(@RequestBody PageParam pageParam){
        return new ResultData<>("获取成功", appService.getAppVersionList(pageParam));
    }

    @PostMapping("update")
    @ApiOperation(value = "修改状态")
    public ResultData<Integer> updateAppVersion(@RequestBody BaseAppVersion appVersion){
        return new ResultData<>("修改成功", appService.updateAppVersion(appVersion));
    }

    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除APK")
    public ResultData<Integer> deleteAppVersion(@PathVariable("id") Integer id){
        return new ResultData<>("删除成功", appService.deleteAppVersion(id));
    }

}
