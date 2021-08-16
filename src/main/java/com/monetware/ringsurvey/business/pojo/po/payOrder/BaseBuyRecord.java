package com.monetware.ringsurvey.business.pojo.po.payOrder;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 购买记录
 */
@Data
@Table(name = "rs_buy_record")
public class BaseBuyRecord {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private Integer projectId;

    /**
     * 订单类型
     * 1：充值，2：中文数据库，3：云采集，4：云质析，5：众包 8.云调查
     */
    private Integer type;

    /**
     * 支付类型
     * 1：支出
     * 2：收入
     */
    private Integer payType;

    /**
     * 用户编号
     */
    private Integer userId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 订单表编号
     */
    private Integer payOrderId;

    /**
     * 删除标志
     */
    private Integer isDelete;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 消费信息
     */
    private String message;
}
