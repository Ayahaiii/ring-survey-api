package com.monetware.ringsurvey.business.controller;

import com.monetware.ringsurvey.business.service.LoginService;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("登录管理")
@RestController
@RequestMapping("/w1/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("scan/set")
    @ApiOperation(value = "设置二维码KEY")
    public ResultData<String> setScanKey(){
        return new ResultData<>("设置成功", loginService.setScanKey());
    }

    @PostMapping("scan/{key}")
    @ApiOperation(value = "获取二维码扫描结果")
    public ResultData<String> getScanResult(@PathVariable("key") String key){
        return new ResultData<>("获取成功", loginService.getScanResult(key));
    }

    @PostMapping("delete/{key}")
    @ApiOperation(value = "删除二维码扫描KEY")
    public ResultData deleteScanKey(@PathVariable("key") String key){
        loginService.deleteScanKey(key);
        return new ResultData<>(0,"获取成功");
    }

}
