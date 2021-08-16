package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.redpacket.RedPacketViewDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserBuyVO;
import com.monetware.ringsurvey.business.pojo.po.BaseRedPacketConfig;
import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.business.pojo.vo.redpacket.*;
import com.monetware.ringsurvey.business.service.project.RedPacketService;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("红包")
@RestController
@RequestMapping("/w1/redPacket")
public class RedPacketController {

    @Autowired
    private RedPacketService redPacketService;

    @PostMapping("/get/view")
    @ApiOperation(value = "获取初始值")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST)
    public ResultData<RedPacketViewDTO> getUserBalance(@RequestBody BaseVO baseVO) {
        return new ResultData("获取成功", redPacketService.getUserBalance(baseVO));
    }

    @PostMapping("/get")
    @ApiOperation(value = "获取红包配置")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST)
    public ResultData<BaseRedPacketConfig> getRedPacketConfig(@RequestBody BaseVO baseVO) {
        return new ResultData("获取成功", redPacketService.getRedPacketConfig(baseVO.getProjectId()));
    }

    @PostMapping("/order")
    @ApiOperation(value = "下单")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<Integer> insertOrder(@RequestBody UserBuyVO userBuyVO) {
        return new ResultData("下单成功", redPacketService.insertOrder(userBuyVO));
    }

    @PostMapping("/pay")
    @ApiOperation(value = "支付")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<Integer> insertBuyOrder(@RequestBody RedPacketOrderVO orderVO) {
        return new ResultData("支付成功", redPacketService.insertBuyOrder(orderVO.getId()));
    }

    @PostMapping("/list/buy")
    @ApiOperation(value = "查询红包支付记录")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<PageList> redRacketRecordList(@RequestBody PageParam pageParam) {
        return new ResultData("获取成功", redPacketService.getBuyRecordList(pageParam));
    }

    @PostMapping("/audit")
    @ApiOperation(value = "审核红包")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<Integer> auditRecord(@RequestBody RedPacketRecordAuditVO recordAuditVO) {
        return new ResultData("审核成功", redPacketService.auditRedPacketRecord(recordAuditVO));
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询红包列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<PageList> redRacketRecordList(@RequestBody RedPacketRecordListVO redPacketListVO) {
        return new ResultData("获取成功", redPacketService.getRedPacketResultDTO(redPacketListVO));
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增红包配置")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<Integer> insertConfig(@RequestBody RedPacketConfigVO redPacketConfigVO) {
        return new ResultData("保存成功", redPacketService.insertRedPacketConfig(redPacketConfigVO));
    }

    @PostMapping("/add")
    @ApiOperation(value = "追加红包金额")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<Integer> updateRedPacketConfig(@RequestBody RedPacketConfigVO redPacketConfigVO) {
        return new ResultData("追加成功", redPacketService.updateRedPacketConfig(redPacketConfigVO));
    }

    @PostMapping("/update")
    @ApiOperation(value = "开启/关闭红包")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST)
    public ResultData<Boolean> closeRedPacket(@RequestBody RedPacketStatusVO statusVO) {
        return new ResultData("关闭成功", redPacketService.closeRedPacket(statusVO));
    }

    @PostMapping("/export")
    @ApiOperation(value = "导出红包记录")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<Integer> exportRedRacketRecord(@RequestBody RedPacketRecordListVO redPacketListVO) throws Exception {
        return new ResultData<>("导出成功", redPacketService.exportRedRacketRecord(redPacketListVO));
    }

    @PostMapping("/export/list")
    @ApiOperation(value = "查询导出红包记录列表")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<PageList> getRedPacketDownList(@RequestBody PageParam param) {
        return new ResultData<>("查询成功", redPacketService.getRedPacketDownList(param));
    }

    @PostMapping("/export/delete")
    @ApiOperation(value = "导出红包记录删除")
    @MonetwareAuthorize(pm = AuthorizedConstants.RR_REDPACKET_LIST, cp = AuthorizedConstants.RC_ALLOW_REDPACKET)
    public ResultData<Integer> deleteDownFile(@RequestBody RedPacketDeleteVO delVO) {
        return new ResultData<>("删除成功", redPacketService.deleteDownFile(delVO));
    }

    @GetMapping("download")
    @ApiOperation(value = "下载红包领取记录文件")
    public void downloadRedPacketRecord(Integer id, HttpServletResponse response) throws Exception {
        redPacketService.downloadRedPacketRecord(id, response);
    }

}
