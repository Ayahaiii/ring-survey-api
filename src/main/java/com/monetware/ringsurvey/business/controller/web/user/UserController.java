package com.monetware.ringsurvey.business.controller.web.user;

import com.github.pagehelper.Page;
import com.monetware.ringsurvey.business.pojo.dto.user.UserBalanceDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserInfoDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserPermissionDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserSearchDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseUser;
import com.monetware.ringsurvey.business.pojo.vo.user.*;
import com.monetware.ringsurvey.business.service.user.UserService;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("用户管理")
@RestController
@RequestMapping("/w1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("permission")
    @ApiOperation(value = "获取用户权限")
    public ResultData<UserPermissionDTO> getUserPermission() {
        return new ResultData<>("获取成功", userService.getUserPermission());
    }

    @PostMapping("permission/local")
    @ApiOperation(value = "获取用户权限")
    public ResultData<UserPermissionDTO> getUserPermissionLocal() {
        return new ResultData<>("获取成功", userService.getUserPermissionLocal());
    }

    @PostMapping("find")
    @ApiOperation(value = "查询用户")
    public ResultData<UserSearchDTO> searchUser(@RequestBody UserSearchVO userSearchVO) {
        return new ResultData<>("查询成功", userService.searchUser(userSearchVO));
    }

    @PostMapping("save")
    @ApiOperation(value = "修改信息")
    public ResultData<Integer> updateUserInfo(@RequestBody BaseUser user) {
        return new ResultData<>(0, "修改成功", userService.updateUser(user));
    }

    @PostMapping("save/local")
    @ApiOperation(value = "修改信息")
    public ResultData<Integer> updateUserInfoLocal(@RequestBody BaseUser user) {
        return new ResultData<>(0, "修改成功", userService.updateUserLocal(user));
    }

    @PostMapping("list")
    @ApiOperation(value = "获取用户列表")
    public ResultData<PageList<Page>> getUserList(@RequestBody UserSelectVO userSearchVO) {
        return new ResultData("获取成功", userService.getUserList(userSearchVO));
    }

    @PostMapping("record")
    @ApiOperation(value = "用户消费记录")
    public ResultData<PageList> getUserBuyRecord(@RequestBody UserBuyRecordVO buyRecordVO) {
        return new ResultData<>(0, "查询成功", userService.getUserBuyRecord(buyRecordVO));
    }

    @PostMapping("balance")
    @ApiOperation(value = "获取用户余额")
    public ResultData<UserBalanceDTO> getUserBalance() {
        return new ResultData<>(0, "查询成功", userService.getUserBalance());
    }

    @PostMapping("refund")
    @ApiOperation(value = "退款")
    public ResultData refundUserBalance(@RequestBody UserReFundVO reFundVO) {
        userService.refundUserBalance(reFundVO);
        return new ResultData<>(0, "退款成功");
    }

    @PostMapping("update")
    @ApiOperation(value = "修改")
    public ResultData<UserInfoDTO> updateUserInfo(@RequestBody UserUpdateVO updateVO) {
        return new ResultData<>(0, "修改成功", userService.updateUserInfo(updateVO));
    }

    @PostMapping("detail/{userId}")
    @ApiOperation(value = "用户详细")
    public ResultData<BaseUser> getUserDetail(@PathVariable("userId") Integer userId) {
        return new ResultData<>(0, "查询成功", userService.getUserDetail(userId));
    }

    @PostMapping("audit")
    @ApiOperation(value = "审核")
    public ResultData<Integer> auditUser(@RequestBody UserAuditVO userAuditVO) {
        return new ResultData<>(0, "查询成功", userService.auditUser(userAuditVO));
    }

    @PostMapping("auth/edit")
    @ApiOperation(value = "权限编辑")
    public ResultData<Integer> updateUserAuth(@RequestBody UserAuthVO userAuthVO) {
        return new ResultData<>(0, "查询成功", userService.updateUserAuth(userAuthVO));
    }
}
