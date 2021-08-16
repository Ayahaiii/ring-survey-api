package com.monetware.ringsurvey.business.pojo.constants;

public class IssueConstants {

    /**
     * 类型
     * 1-短信 2-邮件
     */
    public static final Integer PUBLISH_TYPE_MESSAGE = 1;
    public static final Integer PUBLISH_TYPE_EMAIL = 2;

    /**
     * 发送方式
     * 1-全部样本 2-所选样本
     */
    public static final Integer PUBLISH_METHOD_ALL = 1;
    public static final Integer PUBLISH_METHOD_CHOOSE = 2;

    /**
     * 发送状态
     * 0-待发送 1-发送成功 2-发送失败
     */
    public static final Integer SEND_WAIT = 0;
    public static final Integer SEND_SUCCESS = 1;
    public static final Integer SEND_FAIL = 2;

}
