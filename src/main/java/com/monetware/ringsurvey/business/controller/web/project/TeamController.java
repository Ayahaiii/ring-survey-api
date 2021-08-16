package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.team.TeamGroupResDTO;
import com.monetware.ringsurvey.business.pojo.dto.team.TeamUserInfoDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseRole;
import com.monetware.ringsurvey.business.pojo.vo.team.*;
import com.monetware.ringsurvey.business.service.project.TeamService;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("团队")
@RestController
@RequestMapping("/w1/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("role/list")
    @ApiOperation(value = "获取所有角色")
    public ResultData<List<BaseRole>> getRoleList() {
        return new ResultData<>("获取成功", teamService.getRoleList());
    }

    @PostMapping("save")
    @ApiOperation(value = "保存邀请成员")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_ADD)
    public ResultData<List<Integer>> saveTeamUser(@RequestBody TeamUserListVO teamUserVO) {
        return new ResultData<>(0, "保存成功", teamService.saveTeamUser(teamUserVO));
    }

    @PostMapping("get")
    @ApiOperation(value = "获取团队用户详情")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_VIEW)
    public ResultData<TeamUserInfoDTO> getTeamUserInfo(@RequestBody TeamUserInfoVO teamUserInfoVO) {
        return new ResultData<>("获取成功", teamService.getTeamUserInfo(teamUserInfoVO));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除团队")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_DELETE)
    public ResultData deleteTeamUser(@RequestBody TeamUserDelVO teamUserDelVO) {
        teamService.deleteTeamUser(teamUserDelVO);
        return new ResultData<>(0,"删除成功");
    }

    @PostMapping("batch/import")
    @ApiOperation(value = "导入成员")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_ADD)
    public ResultData<List<TeamUserImportInfoVO>> insertTeamUserByImport(@RequestBody TeamUserImportVO importVO) {
        return new ResultData<>("导入成功", teamService.insertTeamUserByImport(importVO));
    }

    @PostMapping("batch/export")
    @ApiOperation(value = "导出成员")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_EXPORT)
    public ResultData<Integer> exportTeamUser(@RequestBody TeamUserExportVO exportVO) throws Exception {
        return new ResultData<>("导出成功", teamService.exportTeamUser(exportVO));
    }

    @PostMapping("export/list")
    @ApiOperation(value = "导出文件列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_EXPORT)
    public ResultData<PageList> getTeamDownList(@RequestBody PageParam pageParam) {
        return new ResultData<>(0, "查询成功", teamService.getTeamDownList(pageParam));
    }

    @GetMapping("download")
    @ApiOperation(value = "下载成员文件")
    public void downloadTeamUser(Integer id, HttpServletResponse response) throws Exception {
        teamService.downloadTeamUser(id, response);
    }

    @PostMapping("file/delete")
    @ApiOperation(value = "删除文件")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_EXPORT)
    public ResultData<Integer> deleteTeamUserFile(@RequestBody TeamUserExportDelVO delVO) {
        return new ResultData<>("删除成功", teamService.deleteTeamUserFile(delVO));
    }

    @PostMapping("list/page")
    @ApiOperation(value = "分页获取成员列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_LIST)
    public ResultData<PageList> getTeamUserList(@RequestBody TeamUserSearchVO teamUserSearchVO){
        return new ResultData("获取成功", teamService.getTeamUserList(teamUserSearchVO));
    }

    @PostMapping("agree")
    @ApiOperation(value = "同意申请")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_EDIT)
    public ResultData<Integer> updateTeamUserAgree(@RequestBody TeamUserStatusVO teamUserStatusVO) {
        return new ResultData<>("同意成功", teamService.updateTeamUserAgree(teamUserStatusVO));
    }

    @PostMapping("disable")
    @ApiOperation(value = "禁用")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_EDIT)
    public ResultData<Integer> updateTeamUserDisable(@RequestBody TeamUserStatusVO teamUserStatusVO) {
        return new ResultData<>("禁用成功", teamService.updateTeamUserDisable(teamUserStatusVO));
    }

    @PostMapping("group/save")
    @ApiOperation(value = "保存分组")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_GROUP_ADMIN, cp = AuthorizedConstants.RC_TEAM_GROUP)
    public ResultData<Integer> saveTeamGroup(@RequestBody TeamGroupVO teamGroupVO) {
        return new ResultData<>("保存成功", teamService.saveTeamGroup(teamGroupVO));
    }

    @PostMapping("group/tree/{projectId}")
    @ApiOperation(value = "获取树形分组")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_LIST)
    public ResultData<TeamGroupResDTO> getTeamGroupList(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", teamService.getTeamGroupList(projectId));
    }

    @PostMapping("group/delete")
    @ApiOperation(value = "删除分组")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_GROUP_ADMIN, cp = AuthorizedConstants.RC_TEAM_GROUP)
    public ResultData deleteTeamGroup(@RequestBody TeamGroupDelVO teamGroupDelVO) {
        teamService.deleteTeamGroup(teamGroupDelVO);
        return new ResultData<>("删除成功");
    }

    @PostMapping("group/user/save")
    @ApiOperation(value = "批量添加组成员")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_GROUP_ADMIN, cp = AuthorizedConstants.RC_TEAM_GROUP)
    public ResultData<Integer> insertTeamUserToGroup(@RequestBody TeamUserToGroupVO userToGroupVO) {
        return new ResultData<>("添加成功", teamService.insertTeamUserToGroup(userToGroupVO));
    }

    @PostMapping("group/user/list/page")
    @ApiOperation(value = "分页获取组成员列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_GROUP_ADMIN, cp = AuthorizedConstants.RC_TEAM_GROUP)
    public ResultData<PageList> getTeamGroupUserList(@RequestBody TeamGroupUserSearchVO teamGroupUserSearchVO){
        return new ResultData("获取成功", teamService.getTeamGroupUserList(teamGroupUserSearchVO));
    }

    @PostMapping("group/user/delete")
    @ApiOperation(value = "删除组成员")
    @MonetwareAuthorize(pm = AuthorizedConstants.RT_MEMBER_GROUP_ADMIN, cp = AuthorizedConstants.RC_TEAM_GROUP)
    public ResultData<Integer> deleteTeamUserToGroup(@RequestBody TeamUserToGroupVO userToGroupVO) {
        return new ResultData<>("删除成功", teamService.deleteTeamUserToGroup(userToGroupVO));
    }

}
