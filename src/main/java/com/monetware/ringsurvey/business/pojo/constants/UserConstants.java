package com.monetware.ringsurvey.business.pojo.constants;

/**
 * @author Simo
 * @date 2020-03-18
 */
public class UserConstants {

    /**
     * 管理员
     */
    public static Integer ROLE_ADMIN = 0;

    /**
     * 免费版
     */
    public static Integer ROLE_COMMON = 1;

    /**
     * 标准版
     */
    public static Integer ROLE_VIP = 2;

    /**
     * 高级版
     */
    public static Integer ROLE_CONPANY = 3;

    /**
     * 企业版
     */
    public static Integer ROLE_CUSTOM = 4;

    /**
     * 禁用
     */
    public static Integer STATUS_DISABLE = 0;

    /**
     * 正常
     */
    public static Integer STATUS_ENABLE = 1;

    /**
     * 项目问卷数量限制
     */
    public static final Integer QUESTION_NUM_COMMON = 50;
    public static final Integer QUESTION_NUM_VIP = 100;

    /**
     * 退款类型
     */
    public static final Integer REFUND_TYPE_RED_PACKET = 1;
    public static final Integer REFUND_TYPE_SMS = 2;
    public static final Integer REFUND_TYPE_EMAIL = 3;

}
