package com.monetware.ringsurvey.business.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringsurvey.system.mapper.annotation.Constraints;
import com.monetware.ringsurvey.system.mapper.annotation.SQLField;
import lombok.Data;
import tk.mybatis.mapper.entity.IDynamicTableName;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Data
public class BaseResponseAudit implements IDynamicTableName {

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

    @SQLField(column = "response_guid", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String responseGuid;

    /**
     * 审核用户
     */
    @SQLField(column = "audit_user", type = "int", len = 11, constraint = @Constraints(allowNull = true))
    private Integer auditUser;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "audit_time", type = "datetime", constraint = @Constraints(allowNull = true))
    private Date auditTime;

    /**
     * 审核问题
     */
    @SQLField(column = "audit_questions", type = "varchar", len = 255, constraint = @Constraints(allowNull = true))
    private String auditQuestions;

    /**
     * 审核结果
     */
    @SQLField(column = "audit_result", type = "int", len = 11, constraint = @Constraints(allowNull = true))
    private Integer auditResult;

    /**
     * 审核次数
     */
    @SQLField(column = "audit_times", type = "numeric", constraint = @Constraints(allowNull = true))
    private Double auditTimes;

    /**
     * 审核评分
     */
    @SQLField(column = "audit_score", type = "varchar", len = 255, constraint = @Constraints(allowNull = true))
    private String auditScore;

    /**
     * 审核退回理由
     */
    @SQLField(column = "audit_return_reason", type = "text", constraint = @Constraints(allowNull = true))
    private String auditReturnReason;

    /**
     * 审核说明
     */
    @SQLField(column = "audit_comments", type = "text", constraint = @Constraints(allowNull = true))
    private String auditComments;

    /**
     * 审核前答卷类型
     */
    @SQLField(column = "pre_response_type", type = "int", len = 1, constraint = @Constraints(allowNull = true))
    private Integer preResponseType;

    /**
     * 审核后答卷类型
     */
    @SQLField(column = "post_response_type", type = "int", len = 1, constraint = @Constraints(allowNull = true))
    private Integer postResponseType;

    /**
     * 审核前答卷状态
     */
    @SQLField(column = "pre_response_status", type = "int", len = 11, constraint = @Constraints(allowNull = true))
    private Integer preResponseStatus;

    /**
     * 审核后答卷状态
     */
    @SQLField(column = "post_response_status", type = "int", len = 11, constraint = @Constraints(allowNull = true))
    private Integer postResponseStatus;

    /**
     * 审核前答卷数据，json格式
     */
    @SQLField(column = "pre_response_data", type = "text", constraint = @Constraints(allowNull = true))
    private Integer preResponseData;

    /**
     * 审核后答卷数据，json格式
     */
    @SQLField(column = "post_response_data", type = "text", constraint = @Constraints(allowNull = true))
    private Integer postResponseData;

    /**
     * 审核前问题数据，json格式
     */
    @SQLField(column = "pre_question_data", type = "text", constraint = @Constraints(allowNull = true))
    private Integer preQuestionData;

    /**
     * 审核后问题数据，json格式
     */
    @SQLField(column = "post_question_data", type = "text", constraint = @Constraints(allowNull = true))
    private Integer postQuestionData;

}
