package com.monetware.ringsurvey.business.pojo.po;

import com.monetware.ringsurvey.system.mapper.annotation.Constraints;
import com.monetware.ringsurvey.system.mapper.annotation.SQLField;
import lombok.Data;
import tk.mybatis.mapper.entity.IDynamicTableName;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包领取记录
 */
@Data
public class BaseRedPacketRecord implements IDynamicTableName {

    @Transient
    private String dynamicTableName;

    @Override
    public String getDynamicTableName() {
        return dynamicTableName;
    }

    @Id
    @GeneratedValue(generator = "JDBC")
    @SQLField(column = "id", type = "int", auto = true, constraint = @Constraints(primaryKey = true))
    private Integer id;

    /**
     * 答卷编号
     */
    @SQLField(column = "response_guid", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String responseGuid;

    /**
     * 状态
     */
    @SQLField(column = "status", type = "int", constraint = @Constraints(allowNull = false))
    private Integer status;

    /**
     * 审核者
     */
    @SQLField(column = "audit_user", type = "int", constraint = @Constraints(allowNull = true))
    private Integer auditUser;

    /**
     * 审核结果
     */
    @SQLField(column = "audit_result", type = "int", constraint = @Constraints(allowNull = true))
    private Integer auditResult;

    /**
     * 审核时间
     */
    @SQLField(column = "audit_time", type = "dateTime", constraint = @Constraints(allowNull = true))
    private Date auditTime;

    /**
     * 微信支付接口返回信息
     */
    @SQLField(column = "result", type = "text", constraint = @Constraints(allowNull = true))
    private String result;

    /**
     * 商户订单号
     */
    @SQLField(column = "mch_bill_no", type = "varchar", len = 50, constraint = @Constraints(allowNull = true))
    private String mchBillNo;

    /**
     * 商户号
     */
    @SQLField(column = "mch_id", type = "varchar", len = 50, constraint = @Constraints(allowNull = true))
    private String mchId;

    /**
     * 公众账号appid
     */
    @SQLField(column = "wx_app_id", type = "varchar", len = 50, constraint = @Constraints(allowNull = true))
    private String wxAppId;

    /**
     * 商户名称
     */
    @SQLField(column = "send_name", type = "varchar", len = 50, constraint = @Constraints(allowNull = true))
    private String sendName;

    /**
     * 用户openid
     */
    @SQLField(column = "open_id", type = "varchar", len = 50, constraint = @Constraints(allowNull = true))
    private String openId;

    /**
     * 付款金额，单位分
     */
    @SQLField(column = "total_amount", type = "int", constraint = @Constraints(allowNull = true))
    private BigDecimal totalAmount;

    /**
     * 红包发放总人数 total_num=1
     */
    @SQLField(column = "total_num", type = "int", constraint = @Constraints(allowNull = true))
    private Integer totalNum;

    /**
     * 红包祝福语
     */
    @SQLField(column = "wishing", type = "varchar", len = 255, constraint = @Constraints(allowNull = true))
    private String wishing;

    /**
     * 活动名称
     */
    @SQLField(column = "act_name", type = "varchar", len = 255, constraint = @Constraints(allowNull = true))
    private String actName;

    /**
     * 创建时间
     */
    @SQLField(column = "create_time", type = "datetime", constraint = @Constraints(allowNull = true))
    private Date createTime;

}
