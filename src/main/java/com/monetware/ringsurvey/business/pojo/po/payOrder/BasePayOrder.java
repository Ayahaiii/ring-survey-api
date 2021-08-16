package com.monetware.ringsurvey.business.pojo.po.payOrder;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 订单表
 */
@Data
@Table(name = "rs_pay_order")
public class BasePayOrder {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 流水编号
     */
    private String outTradeNo;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 订单类型
     * 1：充值，2：中文数据库，3：云采集，4：云质析，5：众包
     */
    private Integer type;

    /**
     * 支付方式
     * 1：支付宝
     * 2：微信
     * 3：余额
     */
    private Integer payWay;

    /**
     * 支付类型
     * 1：支出
     * 2：收入
     */
    private Integer payType;

    /**
     * 购买用户ID
     */
    private Integer userId;

    /**
     * 支付订单号
     */
    private String returnOrderNo;

    /**
     * 回调金额
     */
    private BigDecimal returnAmount;

    /**
     * 订单状态
     * 0：未支付，
     * 1：已支付，
     * 2：支付失败，
     * 3：支付成功但存在异常，
     * 4：已退款
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 额外存储信息
     */
    private String extraData;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
