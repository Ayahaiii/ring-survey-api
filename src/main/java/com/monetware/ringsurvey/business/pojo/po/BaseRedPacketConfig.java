package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目红包配置
 */
@Data
@Table(name="rs_project_redPacket_config")
public class BaseRedPacketConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    private Integer number;

    private BigDecimal perAmount;

    private BigDecimal totalAmount;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private Integer rate;

    private Integer intervalNumber;

    private Integer sendType;// 发送方式 1-审核 2-不审核

    private Integer winningType;// 获取方式 1-概率固定 2-人数固定

    private Integer amountType;//

    private String areaLimit;// 区域限制

    private Integer systemDayTimes;

    private Integer wechatTotalTimes;

    private Integer wechatDayTimes;

    private String sendName;// 商户名称

    private String actName;// 活动名称

    private String wish;// 红包祝福

    private Date createTime;
}
