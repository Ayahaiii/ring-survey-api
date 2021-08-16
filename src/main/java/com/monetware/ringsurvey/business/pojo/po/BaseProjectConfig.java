package com.monetware.ringsurvey.business.pojo.po;

import com.monetware.ringsurvey.business.pojo.constants.ProjectConstants;
import lombok.Data;

import java.util.Date;

/**
 * 项目配置
 */
@Data
public class BaseProjectConfig {

    /**
     * 项目相关
     */
    private Integer hideLogo;// 支持隐藏云调查LOGO
    private Integer allowCadi;// 支持数据录入答卷
    private Integer allowCapi;// 支持面访调查答卷
    private Integer allowCati;// 支持电话调查答卷
    private Integer allowCawi;// 支持网络调查答卷
    private Integer autoTimeEnd;// 支持项目到期时间自动结束
    private Date autoTimeEndValue; // 结束时间
    private Integer autoNumEnd;// 支持有效答卷满足自动结束
    private Integer autoNumEndValue;// 答卷数量
    private Integer allowSmsAEmail; // 支持短信和邮箱发送链接
    private Integer allowRedPacket;// 支持发放红包/审核红包     0，1-开启 ,1-一审，2-二审

    // 202008010
    private Integer periodLimited;// 时间阶段限制
    private String periodBt;// 开始时间
    private String periodEt;// 结束时间
    private String periodMsg;// 提示内容
    private String endText;// 项目结束语

    /**
     * 问卷相关
     */
    private Integer multipleQuestionnaire;// 支持项目多问卷
    private Integer qnaireGroup; // 支持问卷分组

    /**
     * 样本相关
     */
    private Integer ifOpenSample;// 是否开启样本
    private Integer sampleExtraData;// 支持添加附加数据
    private Integer moreSampleInfo;// 支持添加更多地址/联系方式/接触记录
    private Integer interviewerSaveSample;// 支持访问员添加样本
    private Integer sampleAssignType;// 支持样本分配方式 0，1-动态分配 2-预先分配

    // 针对cati
    private Integer interviewerUpdateSample;// 是否允许访问员修改样本
    private Integer interviewerUpdatePhone;// 是否允许访问员修改电话号码

    /**
     * 团队相关
     */
    private Integer manageTeamGroup;// 支持团队分组
    private Integer trackInterviewer;// 支持监控访问员，0，监控频率 1-5，2-10，3-15，4-30，5-60

    /**
     * 配额相关
     */
    private Integer ifOpenQuota;// 支持开启配额
    private Integer ifRandomQuota;// 支持随机配额

    /**
     * 答卷相关
     */
    private Integer responseAudio;// 支持答卷录音
    private Integer responsePosition;// 支持答卷定位
    private Integer multipleAudit;// 支持答卷审核   0，1-一审，2-二审，3-三审
    private Integer pageTempSubmit;// 支持答卷逐页提交
    private Integer ipOneSubmit; // 支持限制同一IP一次提交
    private Integer browserOneSubmit; // 支持限同一制浏览器一次提交
    private Integer responseHistory;// 支持保存答卷历史
    private Integer responseRecall;// 支持访问员重置/替换答卷
    private Integer responsePassword;// 支持作答密码  0-不需要 1-统一密码 2-样本属性密码 3-手机验证码
    private String  responsePasswordValue; // 1 - "1234" 2 - "name like 'li'" 3 - "phone"
    private Integer multipleSource;// 支持多来源链接
    private Integer takePhoto;// 支持纸质照片存档
    private Integer secondSubmit;// 支持二次提交

    public BaseProjectConfig() {
        this.hideLogo = ProjectConstants.CLOSE;
        this.allowCadi = ProjectConstants.OPEN;
        this.allowCapi = ProjectConstants.OPEN;
        this.allowCati = ProjectConstants.OPEN;
        this.allowCawi = ProjectConstants.OPEN;
        this.autoTimeEnd = ProjectConstants.CLOSE;
        this.autoTimeEndValue = null;
        this.autoNumEnd = ProjectConstants.CLOSE;
        this.autoNumEndValue = null;
        this.allowSmsAEmail = ProjectConstants.CLOSE;
        this.allowRedPacket = ProjectConstants.CLOSE;
        this.periodLimited = ProjectConstants.CLOSE;
        this.periodBt = null;
        this.periodEt = null;
        this.periodMsg = null;
        this.endText = null;
        this.multipleQuestionnaire = ProjectConstants.CLOSE;
        this.qnaireGroup = ProjectConstants.CLOSE;
        this.ifOpenSample = ProjectConstants.OPEN;
        this.sampleExtraData = ProjectConstants.CLOSE;
        this.moreSampleInfo = ProjectConstants.CLOSE;
        this.interviewerSaveSample = ProjectConstants.CLOSE;
        this.sampleAssignType = ProjectConstants.CLOSE;
        this.interviewerUpdateSample = ProjectConstants.CLOSE;
        this.interviewerUpdatePhone = ProjectConstants.CLOSE;
        this.manageTeamGroup = ProjectConstants.CLOSE;
        this.trackInterviewer = ProjectConstants.CLOSE;
        this.ifOpenQuota = ProjectConstants.CLOSE;
        this.ifRandomQuota = ProjectConstants.CLOSE;
        this.responseAudio = ProjectConstants.CLOSE;
        this.responsePosition = ProjectConstants.CLOSE;
        this.multipleAudit = ProjectConstants.OPEN;
        this.pageTempSubmit = ProjectConstants.CLOSE;
        this.ipOneSubmit = ProjectConstants.CLOSE;
        this.browserOneSubmit = ProjectConstants.OPEN;
        this.responseHistory = ProjectConstants.CLOSE;
        this.responseRecall = ProjectConstants.CLOSE;
        this.responsePassword = ProjectConstants.CLOSE;
        this.responsePasswordValue = null;
        this.multipleSource = ProjectConstants.CLOSE;
        this.takePhoto = ProjectConstants.CLOSE;
        this.secondSubmit = ProjectConstants.CLOSE;
    }
}
