package com.monetware.ringsurvey.business.pojo.constants;

/**
 * @author zq
 * @create 2019-02-18 12:59
 * @Description: ${description}
 */
public class PayConstant {

    public static final Integer PAY_WAY_ALI = 1;//阿里支付
    public static final Integer PAY_WAY_WX = 2;//微信支付
    public static final Integer PAY_WAY_BALANCE = 3;//余额支付

    public static final Integer STATUS_NO_PAY = 1;//未支付
    public static final Integer STATUS_IS_PAY = 2;//已支付
    public static final Integer STATUS_WRONG_PAY = 3;//支付失败

    public static final Integer PAY_TYPE_EXPEND = 1;//支出
    public static final Integer PAY_TYPE_INCOME = 2;//收入

    public static final Integer TYPE_RECHARGE = 1;//充值
    public static final Integer TYPE_CND = 2;//中文数据库
    public static final Integer TYPE_SPIDER = 3;//云采集
    public static final Integer TYPE_NLP = 4;//云质析
    public static final Integer TYPE_ZB = 5;//众包
    public static final Integer TYPE_RS = 8;//云调查
}
