package com.monetware.ringsurvey.business.controller.web;

import com.monetware.ringsurvey.business.pojo.dto.project.ConfigDTO;
import com.monetware.ringsurvey.business.service.project.ProjectService;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Simo
 * @date 2019-06-17
 */
@Api("第三方相关api")
@RestController
@RequestMapping("w1/out")
public class AuthController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("getDataCount")
    @ApiOperation(value = "获取数据")
    public ResultData<Map<String, Object>> getProjectTextCount() {
        return new ResultData<>("获取成功", projectService.getProjectTextCount());
    }

    @PostMapping("getConfig")
    @ApiOperation(value = "获取配置")
    public ResultData<ConfigDTO> getConfig() {
        return new ResultData<>(0, "获取成功", projectService.getConfig());
    }

}
