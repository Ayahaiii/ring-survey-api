package com.monetware.ringsurvey.business.pojo.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simo
 * @date 2020-02-20
 */
public class AuthorizedConstants {

    /**
     * 管理员
     */
    public static final Integer ROLE_OWNER = 1;

    /**
     * 管理员
     */
    public static final Integer ROLE_ADMIN = 2;

    /**
     * 观察员
     */
    public static final Integer ROLE_OBSERVER = 3;

    /**
     * 督导员
     */
    public static final Integer ROLE_SUPERVISOR = 4;

    /**
     * 访问员
     */
    public static final Integer ROLE_INTERVIEWER = 5;

    // 项目拥有者
    public static List<String> R_ALL = new ArrayList<>();

    // 项目详情模块
    public static final String RP_PROJECT_VIEW = "PROJECT_VIEW"; // 项目基本信息详情
    public static final String RP_PROJECT_EDIT = "PROJECT_EDIT"; // 项目基本信息编辑
    public static final String RP_PROJECT_CONFIG_ADMIN = "PROJECT_CONFIG_ADMIN"; // 项目配置管理

    // 样本
    public static final String RS_SAMPLE_LIST = "SAMPLE_LIST"; // 样本列表
    public static final String RS_SAMPLE_ADD = "SAMPLE_ADD"; // 样本添加
    public static final String RS_SAMPLE_VIEW = "SAMPLE_VIEW"; // 样本详情
    public static final String RS_SAMPLE_EDIT = "SAMPLE_EDIT"; // 样本编辑
    public static final String RS_SAMPLE_DELETE = "SAMPLE_DELETE"; // 样本删除
    public static final String RS_SAMPLE_EXPORT = "SAMPLE_EXPORT"; // 样本导出
    public static final String RS_SAMPLE_PROPERTY_ADMIN = "SAMPLE_PROPERTY_ADMIN"; // 样本属性
    public static final String RS_SAMPLE_ASSIGN_ADMIN = "SAMPLE_ASSIGN_ADMIN"; // 样本分派
    public static final String RS_SAMPLE_OUTDATA_ADMIN = "SAMPLE_OUTDATA_ADMIN"; // 样本外部数据
    public static final String RS_SAMPLE_MOREDATA_ADMIN = "SAMPLE_MOREDATA_ADMIN"; // 样本更多数据

    // 团队
    public static final String RT_MEMBER_LIST = "MEMBER_LIST"; // 团队列表
    public static final String RT_MEMBER_ADD = "MEMBER_ADD"; // 团队添加
    public static final String RT_MEMBER_VIEW = "MEMBER_VIEW"; // 团队详情
    public static final String RT_MEMBER_EDIT = "MEMBER_EDIT"; // 团队编辑
    public static final String RT_MEMBER_DELETE = "MEMBER_DELETE"; // 团队删除
    public static final String RT_MEMBER_EXPORT = "MEMBER_EXPORT"; // 团队导出
    public static final String RT_MEMBER_GROUP_ADMIN = "MEMBER_GROUP_ADMIN"; // 团队分组管理
    public static final String RT_MEMBER_INVITECODE_ADMIN = "MEMBER_INVITECODE_ADMIN"; // 团队邀请码

    // 问卷模块
    public static final String RQ_QNAIRE_LIST = "QNAIRE_LIST"; // 问卷列表
    public static final String RQ_QNAIRE_IMPORT = "QNAIRE_IMPORT";// 问卷导入
    public static final String RQ_QNAIRE_VIEW = "QNAIRE_VIEW";// 问卷预览
    public static final String RQ_QNAIRE_EDIT = "QNAIRE_EDIT";// 问卷编辑
    public static final String RQ_QNAIRE_ISSUE = "QNAIRE_ISSUE";// 问卷删除
    public static final String RQ_QNAIRE_EXPORT = "QNAIRE_EXPORT";// 问卷导出
    public static final String RQ_QNAIRE_GROUP = "QNAIRE_GROUP";// 问卷分组管理

    // 答卷模块
    public static final String RA_ANSWER_LIST = "ANSWER_LIST"; // 答卷列表
    public static final String RA_ANSWER_EDIT = "ANSWER_EDIT";// 答卷重置 替换
    public static final String RA_ANSWER_AUDIT = "ANSWER_AUDIT";// 答卷审核
    public static final String RA_ANSWER_EXPORT = "ANSWER_EXPORT";// 答卷导出
    public static final String RA_ANSWER_VOICE = "ANSWER_VOICE";// 答卷录音
    public static final String RA_ANSWER_FILE = "ANSWER_FILE";// 答卷文件

    // 配额模块
    public static final String RQ_QUOTA_LIST = "QUOTA_LIST"; // 配额列表
    public static final String RQ_QUOTA_ADD = "QUOTA_ADD";// 配额新增
    public static final String RQ_QUOTA_EDIT = "QUOTA_EDIT";// 配额编辑
    public static final String RQ_QUOTA_DELETE = "QUOTA_DELETE";// 配额删除
    public static final String RQ_QUOTA_EXPORT = "QUOTA_EXPORT";// 配额导出

    // 发布模块
    public static final String RI_ISSUE_LIST = "ISSUE_LIST"; // 发布
    public static final String RI_ISSUE_SMS_ORDER = "ISSUE_SMS_ORDER"; // 短信充值

    // 红包模块
    public static final String RR_REDPACKET_LIST = "REDPACKET_LIST"; // 红包

    // 分析模块
    public static final String RA_STAT_LIST = "STAT_LIST"; // 分析

    // 监控模块
    public static final String RM_MONITOR_LIST = "MONITOR_LIST"; // 监控

    // 配置是否开启相关
    public static final String RC_ALLOW_REDPACKET = "allowRedPacket"; // 短信邮箱发送开启
    public static final String RC_SMS_EMAIL = "allowSmsAEmail"; // 短信邮箱发送开启
    public static final String RC_SAVE_SAMPLE = "interviewerSaveSample"; // 样本外部数据开启
    public static final String RC_EXTRA_DATA = "sampleExtraData"; // 样本外部数据开启
    public static final String RC_MORE_DATA = "moreSampleInfo"; // 样本更多数据开启
    public static final String RC_TEAM_GROUP = "manageTeamGroup"; // 团队组开启
    public static final String RC_QNAIRE_GROUP = "qnaireGroup"; // 问卷组开启
    public static final String RC_ANSWER_RECALL = "responseRecall"; // 答卷重置 替换
    public static final String RC_OPEN_QUOTA = "ifOpenQuota"; // 开启配额

    static {
        R_ALL.add(RP_PROJECT_VIEW);
        R_ALL.add(RP_PROJECT_EDIT);
        R_ALL.add(RP_PROJECT_CONFIG_ADMIN);
        R_ALL.add(RS_SAMPLE_LIST);
        R_ALL.add(RS_SAMPLE_ADD);
        R_ALL.add(RS_SAMPLE_VIEW);
        R_ALL.add(RS_SAMPLE_EDIT);
        R_ALL.add(RS_SAMPLE_DELETE);
        R_ALL.add(RS_SAMPLE_EXPORT);
        R_ALL.add(RS_SAMPLE_PROPERTY_ADMIN);
        R_ALL.add(RS_SAMPLE_ASSIGN_ADMIN);
        R_ALL.add(RS_SAMPLE_OUTDATA_ADMIN);
        R_ALL.add(RS_SAMPLE_MOREDATA_ADMIN);
        R_ALL.add(RT_MEMBER_LIST);
        R_ALL.add(RT_MEMBER_ADD);
        R_ALL.add(RT_MEMBER_VIEW);
        R_ALL.add(RT_MEMBER_EDIT);
        R_ALL.add(RT_MEMBER_DELETE);
        R_ALL.add(RT_MEMBER_EXPORT);
        R_ALL.add(RT_MEMBER_GROUP_ADMIN);
        R_ALL.add(RT_MEMBER_INVITECODE_ADMIN);
        R_ALL.add(RQ_QNAIRE_LIST);
        R_ALL.add(RQ_QNAIRE_IMPORT);
        R_ALL.add(RQ_QNAIRE_VIEW);
        R_ALL.add(RQ_QNAIRE_EDIT);
        R_ALL.add(RQ_QNAIRE_ISSUE);
        R_ALL.add(RQ_QNAIRE_EXPORT);
        R_ALL.add(RQ_QNAIRE_GROUP);
        R_ALL.add(RA_ANSWER_LIST);
        R_ALL.add(RA_ANSWER_EDIT);
        R_ALL.add(RA_ANSWER_AUDIT);
        R_ALL.add(RA_ANSWER_EXPORT);
        R_ALL.add(RA_ANSWER_VOICE);
        R_ALL.add(RA_ANSWER_FILE);
        R_ALL.add(RQ_QUOTA_LIST);
        R_ALL.add(RQ_QUOTA_ADD);
        R_ALL.add(RQ_QUOTA_EDIT);
        R_ALL.add(RQ_QUOTA_DELETE);
        R_ALL.add(RQ_QUOTA_EXPORT);
        R_ALL.add(RI_ISSUE_LIST);
        R_ALL.add(RI_ISSUE_SMS_ORDER);
        R_ALL.add(RR_REDPACKET_LIST);
        R_ALL.add(RA_STAT_LIST);
        R_ALL.add(RM_MONITOR_LIST);
    }

}
