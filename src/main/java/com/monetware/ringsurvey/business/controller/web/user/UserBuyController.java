package com.monetware.ringsurvey.business.controller.web.user;


import com.monetware.ringsurvey.business.pojo.dto.user.UserBuyVO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserOrderDTO;
import com.monetware.ringsurvey.business.service.user.UserBuyService;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Simo
 * @date 2019-04-17
 */
@RestController
@RequestMapping("/order")
public class UserBuyController {

    @Autowired
    private UserBuyService userBuyService;

    @PostMapping(value = "create")
    @ApiOperation(value = "下单")
    public ResultData<Integer> insertOrder(@RequestBody UserBuyVO userBuyVO) {
        return new ResultData<>(0,"下单成功", userBuyService.insertOrder(userBuyVO));
    }

    @GetMapping(value = "get/{id}")
    @ApiOperation(value = "获取订单")
    public ResultData<List<UserOrderDTO>> insertOrder(@PathVariable("id") Integer id) {
        return new ResultData<>(0,"获取成功", userBuyService.getOrder(id));
    }

    @GetMapping(value = "execute/{id}")
    @ApiOperation(value = "支付订单")
    public ResultData<Integer> insertRecharge(@PathVariable("id") Integer id) {
        return new ResultData<>(0,"支付成功",userBuyService.insertBuyOrder(id));
    }

    @PostMapping("amount")
    @ApiOperation(value = "获取用户余额")
    public ResultData<BigDecimal> getUserBalance(){
        return new ResultData<>(0,"获取成功", userBuyService.getUserBalance());
    }


}
