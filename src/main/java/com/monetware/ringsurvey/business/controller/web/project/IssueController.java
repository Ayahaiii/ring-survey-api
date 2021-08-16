package com.monetware.ringsurvey.business.controller.web.project;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.dto.issue.MultiplySourceIssueDTO;
import com.monetware.ringsurvey.business.pojo.dto.issue.SmsEmailViewDTO;
import com.monetware.ringsurvey.business.pojo.dto.issue.WxShareDTO;
import com.monetware.ringsurvey.business.pojo.dto.sample.SamplePropertyDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserBuyVO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectPublishConfig;
import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.business.pojo.vo.issue.*;
import com.monetware.ringsurvey.business.service.project.IssueService;
import com.monetware.ringsurvey.business.service.project.SampleService;
import com.monetware.ringsurvey.system.authorize.MonetwareAuthorize;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Api("发布")
@RestController
@RequestMapping("/w1/issue")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private SampleService sampleService;

    @PostMapping("get/code/{projectId}")
    @ApiOperation(value = "获取发布码")
    @MonetwareAuthorize(pm = AuthorizedConstants.RI_ISSUE_LIST)
    public ResultData<Boolean> linkIssue(@PathVariable("projectId") Integer projectId) {
        return new ResultData("上传成功", issueService.linkIssue(projectId));
    }

    @PostMapping("wx/upload/{projectId}")
    @ApiOperation(value = "上传分享图片")
    public ResultData<String> uploadWxShareImg(@RequestParam("file") MultipartFile file, @PathVariable("projectId") Integer projectId) {
        return new ResultData("上传成功", issueService.uploadWxShareImg(file));
    }

    @PostMapping("wx/delete")
    @ApiOperation(value = "删除分享图片")
    @MonetwareAuthorize(pm = AuthorizedConstants.RI_ISSUE_SMS_ORDER)
    public ResultData deleteWxShareImg(@RequestBody WxShareImgDelVO imgDelVO) {
        issueService.deleteWxShareImg(imgDelVO);
        return new ResultData(0, "删除成功");
    }

    @PostMapping("wx/save")
    @MonetwareAuthorize(pm = AuthorizedConstants.RI_ISSUE_SMS_ORDER)
    @ApiOperation(value = "保存微信分享")
    public ResultData saveWxShare(@RequestBody WxShareVO shareVO) {
        issueService.saveWxShare(shareVO);
        return new ResultData(0, "保存成功");
    }

    @PostMapping("wx/get")
    @MonetwareAuthorize(pm = AuthorizedConstants.RI_ISSUE_SMS_ORDER)
    @ApiOperation(value = "获取微信分享")
    public ResultData<WxShareDTO> getWxShare(@RequestBody WxShareImgDelVO imgDelVO) {
        return new ResultData("删除成功", issueService.getWxShare(imgDelVO));
    }


    @PostMapping("config/save")
    @ApiOperation(value = "保存发布配置")
    public ResultData<BaseProjectPublishConfig> savePublishConfig(@RequestBody PublishConfigVO publishConfigVO) {
        return new ResultData("保存成功", issueService.savePublishConfig(publishConfigVO));
    }

    @PostMapping("config/get")
    @ApiOperation(value = "获取发布配置")
    public ResultData<BaseProjectPublishConfig> getPublishConfig(@RequestBody PublishConfigVO publishConfigVO) {
        return new ResultData("保存成功", issueService.getPublishConfig(publishConfigVO.getProjectId(), publishConfigVO.getType()));
    }

    @PostMapping("config/view")
    @ApiOperation(value = "获取初始值")
    public ResultData<SmsEmailViewDTO> getUserBalance(@RequestBody PublishInitVO initVO) {
        return new ResultData("获取成功", issueService.getUserBalance(initVO));
    }

    @PostMapping("config/test")
    @ApiOperation(value = "发送测试")
    public ResultData<Integer> sendTestExample(@RequestBody TestSendVO testSendVO) {
        return new ResultData("发送成功", issueService.sendTestExample(testSendVO));
    }

    @PostMapping("send")
    @ApiOperation(value = "发送")
    public ResultData doPublish(@RequestBody PublishSendVO publishSendVO) {
        issueService.doPublish(publishSendVO);
        return new ResultData(0, "发送成功");
    }

    @PostMapping("get/property/{projectId}")
    @ApiOperation(value = "获取项目样本使用属性")
    public ResultData<SamplePropertyDTO> getSampleProperty(@PathVariable("projectId") Integer projectId) {
        return new ResultData("查询成功", sampleService.getSampleProperty(projectId));
    }

    @PostMapping("publish/logs")
    @ApiOperation(value = "获取发布日志")
    public ResultData<PageList> getPublishLogs(@RequestBody PublishSearchVO searchVO) {
        return new ResultData("查询成功", issueService.getPublishLogs(searchVO));
    }


    @PostMapping("order")
    @ApiOperation(value = "下单")
    public ResultData<Integer> insertOrder(@RequestBody UserBuyVO userBuyVO) {
        return new ResultData("下单成功", issueService.insertOrder(userBuyVO));
    }

    @PostMapping("pay")
    @ApiOperation(value = "支付")
    public ResultData<Integer> insertBuyOrder(@RequestBody PublishOrderVO orderVO) {
        return new ResultData("支付成功", issueService.insertBuyOrder(orderVO.getId()));
    }

    @PostMapping("buy/list")
    @ApiOperation(value = "查询支付记录")
    public ResultData<PageList> getPayOrderList(@RequestBody PageParam pageParam) {
        return new ResultData("获取成功", issueService.getPayOrderList(pageParam));
    }

    @PostMapping("add")
    @ApiOperation(value = "追加")
    public ResultData<Integer> addSmsAndEmailNum(@RequestBody PublishConfigVO configVO) {
        return new ResultData("追加成功", issueService.addSmsAndEmailNum(configVO));
    }

    @PostMapping("source/list/{projectId}")
    @ApiOperation(value = "多来源链接列表")
    public ResultData<List<MultiplySourceIssueDTO>> getMultiplySourceIssueList(@PathVariable Integer projectId) {
        return new ResultData(0, "获取成功", issueService.getMultiplySourceIssues(projectId));
    }

    @PostMapping("source/insert")
    @ApiOperation(value = "多来源链接添加")
    public ResultData<List<MultiplySourceIssueDTO>> insertMultiplySourceIssue(@RequestBody MultiplySourceIssueVO sourceIssueVO) {
        return new ResultData(0, "添加成功", issueService.insertMultiplySourceIssue(sourceIssueVO));
    }

    @PostMapping("source/delete/{sourceId}")
    @ApiOperation(value = "多来源链接删除")
    public ResultData<List<MultiplySourceIssueDTO>> deleteMultiplySourceIssue(@PathVariable Integer sourceId) {
        return new ResultData(0, "删除成功", issueService.deleteMultiplySourceIssue(sourceId));
    }

}
