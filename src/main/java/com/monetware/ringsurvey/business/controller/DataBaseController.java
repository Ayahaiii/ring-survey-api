package com.monetware.ringsurvey.business.controller;

import com.monetware.ringsurvey.business.service.DataBaseService;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("数据库")
@RestController
@RequestMapping("/w1/db")
public class DataBaseController {

    @Autowired
    private DataBaseService dataBaseService;

    @PostMapping("exist")
    @ApiOperation(value = "判断数据表是否存在")
    public ResultData<Boolean> validateTableExist(@RequestParam("tableName") String tableName) {
        return new ResultData<>(0, "查询成功", dataBaseService.validateTableExist(tableName));
    }
}
