package com.monetware.ringsurvey.business.pojo.constants;

/**
 * @author Simo
 * @date 2020-03-03
 */
public class ProjectConstants {

    // 初始化值
    public static final Integer INIT_VALUE = 0;

    /**
     * 项目配置开关
     * 0-关闭 1-开启
     */
    public static final Integer CLOSE = 0;
    public static final Integer OPEN = 1;

    /**
     * 项目类型
     * 1-在线录入
     * 2-面访调查
     * 3-电话调查
     * 4-网络调查
     * 5-混合调查
     */
    public static final String PROJECT_TYPE_CADI = "CADI";
    public static final String PROJECT_TYPE_CAPI = "CAPI";
    public static final String PROJECT_TYPE_CATI = "CATI";
    public static final String PROJECT_TYPE_CAWI = "CAWI";
    public static final String PROJECT_TYPE_CAXI = "CAXI";

    /**
     * 邀请码自动审核
     * 1-自动
     * 0-手动
     */
    public static final Integer CODE_AUDIT_AUTO = 1;
    public static final Integer CODE_AUDIT_MANUAL = 0;

    /**
     * 全部样本
     */
    public static final Integer SAMPLE_TYPE_ALL = 1;

    /**
     * 样本范围
     */
    public static final Integer SAMPLE_TYPE_LESS = 2;

    /**
     * 永久
     */
    public static final Integer AUTH_TYPE_FOREVER = 1;

    /**
     * 时效
     */
    public static final Integer AUTH_TYPE_TIME = 2;

    /**
     * 待同意
     */
    public static final Integer USER_STATUS_WAIT = 1;

    /**
     * 有效
     */
    public static final Integer USER_STATUS_ENABLE = 2;

    /**
     * 无效
     */
    public static final Integer USER_STATUS_DISABLE = 3;

    /**
     * 项目项目状态
     * 0-准备中
     * 1-已启动
     * 2-已结束
     * 3-已归档
     */
    public static final Integer STATUS_WAIT = 0;
    public static final Integer STATUS_RUN = 1;
    public static final Integer STATUS_PAUSE = 2;
    public static final Integer STATUS_FINISH = 3;

    /**
     * 删除标记
     * 0-未删除
     * 1-已删除
     */
    public static final Integer DELETE_NO = 0;
    public static final Integer DELETE_YES = 1;

    /**
     * 项目问卷模块类型
     * 1-主模块 2-子模块
     */
    public static final Integer PROJECT_MODULE_TYPE_MASTER = 1;
    public static final Integer PROJECT_MODULE_TYPE_SON = 2;

    /**
     * 项目问卷状态
     * 0-草稿 1-发布
     */
    public static final Integer QUESTIONNAIRE_STATUS_INIT = 0;
    public static final Integer QUESTIONNAIRE_STATUS_DRAFT = 1;

    /**
     * 团队成员限制
     */
    public static final Integer TEAM_MEMBER_VIP = 10;
    public static final Integer TEAM_MEMBER_COMPANY = 50;
    public static final Integer TEAM_MEMBER_ORG = 200;

    /**
     * 项目创建上限
     */
    public static final Integer TOP_LIMIT = 120;

    // 项目样本表名
    public static String getSampleTableName(Integer projectId) {
        return "rs_project_sample_" + projectId;
    }

    // 项目样本分派表名
    public static String getSampleAssignmentTableName(Integer projectId) {
        return "rs_project_sample_assignment_" + projectId;
    }

    // 项目样本通讯录表名
    public static String getSampleContactsTableName(Integer projectId) {
        return "rs_project_sample_contacts_" + projectId;
    }

    // 项目样本地址表名
    public static String getSampleAddressTableName(Integer projectId) {
        return "rs_project_sample_address_" + projectId;
    }

    // 项目样本接触记录表名
    public static String getSampleTouchTableName(Integer projectId) {
        return "rs_project_sample_touch_" + projectId;
    }

    // 项目样本池表名
    public static String getSamplePoolTableName(Integer projectId) {
        return "rs_project_sample_pool_" + projectId;
    }

    // 项目答卷表名
    public static String getResponseTableName(Integer projectId) {
        return "rs_project_response_" + projectId;
    }

    // 项目答卷审核表名
    public static String getResponseAuditTableName(Integer projectId) {
        return "rs_project_response_audit_" + projectId;
    }

    // 项目发布日志表名
    public static String getPublishLogTableName(Integer projectId) {
        return "rs_project_publish_log_" + projectId;
    }

    // 项目发布任务表名
    public static String getPublishQueueTableName(Integer projectId) {
        return "rs_project_publish_queue_" + projectId;
    }

    // 项目答卷录音表名
    public static String getResponseAudioTableName(Integer projectId) {
        return "rs_project_response_audio_" + projectId;
    }

    // 项目答卷附件表名
    public static String getResponseFileTableName(Integer projectId) {
        return "rs_project_response_file_" + projectId;
    }

    // 项目答卷历史表名
    public static String getResponseHistoryTableName(Integer projectId) {
        return "rs_project_response_history_" + projectId;
    }

    // 项目答卷定位表名
    public static String getResponsePositionTableName(Integer projectId) {
        return "rs_project_response_position_" + projectId;
    }

    // 样本管理表
    public static String getBaseSampleTableName(Integer sampleBaseId) {
        return "rs_sample_base_" + sampleBaseId;
    }

    // 红包领取记录表
    public static String getRedPackRecordTableName(Integer projectId) {
        return "rs_project_redpacket_record_" + projectId;
    }

    // 访员轨迹表
    public static String getInterviewerTravelTableName(Integer projectId) {
        return "rs_project_interviewer_travel_" + projectId;
    }

    /**
     * 拨号方式
     * 0-手动 1-自动
     */
    public static Integer DIAL_MANUAL = 0;
    public static Integer DIAL_AUTO = 1;

    /**
     * 获取样本样式
     * 0-手动 1-自动
     */
    public static Integer GET_SAMPLE_MANUAL = 0;
    public static Integer GET_SAMPLE_AUTO = 1;

    /**
     * 查询范围
     */
    public static String RANGE_TODAY = "today";
    public static String RANGE_ALL = "all";

}
