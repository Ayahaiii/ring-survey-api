package com.monetware.ringsurvey.business.pojo.constants;

public class RedPacketConstants {

    public static final String WECHAT_OPEN_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
    public static final String WECHAT_PAY_URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
    public static final String WECHAT_PAY_URL_SEND_RED_PACK = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
    public static final String WECHAT_PAY_URL_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    public static final String TYADE_TYPE_JSAPI = "JSAPI";
    public static final String TYADE_TYPE_NATIVE = "NATIVE";

    /**
     * 0-否 1-是
     */
    public static final Integer NO = 0;
    public static final Integer YES = 1;

    /**
     * 红包发送方式
     * 1-需审核 0-无需审核
     */
    public static final Integer SEND_TYPE_AUDIT = 1;
    public static final Integer SEND_TYPE_FREE = 0;

    /**
     * 审核结果
     */
    public static final Integer AUDIT_SUCCESS = 1;
    public static final Integer AUDIT_FAIL = 2;

    /**
     * 中奖方式
     * 1-概率固定 2-人数固定(每间隔固定人数发送，第一个必中)
     */
    public static final Integer BONUS_RATE_FIXED = 0;
    public static final Integer BONUS_NUM_FIXED = 1;

    /**
     * 金额方式
     * 1-固定金额 2-随机金额
     */
    public static final Integer AMOUNT_FIXED = 0;
    public static final Integer AMOUNT_RANDOM = 1;

    /**
     * 红包发送状态
     */
    public static final Integer SEND_WAIT = 1;
    public static final Integer SEND_SUCCESS = 2;
    public static final Integer SEND_FAIL = 3;

}
