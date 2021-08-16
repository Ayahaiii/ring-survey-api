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
public class BaseResponseHistory implements IDynamicTableName {

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
     * 模块ID
     */
    @SQLField(column = "module_id", type = "int", len = 11, constraint = @Constraints(allowNull = false))
    private Integer moduleId;

    /**
     * 模块名称
     */
    @SQLField(column = "module_name", type = "varchar", len = 255)
    private String moduleName;

    /**
     * 问卷ID
     */
    @SQLField(column = "questionnaire_id", type = "int", len = 11, constraint = @Constraints(allowNull = false))
    private Integer questionnaireId;

    /**
     * 问卷名称
     */
    @SQLField(column = "questionnaire_name", type = "varchar", len = 255)
    private String questionnaireName;

    /**
     * 样本GUID
     */
    @SQLField(column = "sample_guid", type = "varchar", len = 255, constraint = @Constraints(allowNull = false))
    private String sampleGuid;

    /**
     * 版本
     */
    @SQLField(column = "version", type = "int", len = 11, constraint = @Constraints(allowNull = false), defaultValue = "1")
    private Integer version;

    /**
     * app生成子问卷的json
     */
    @SQLField(column = "sub_questionnaire_json", type = "longtext")
    private String subQuestionnaireJson;

    /**
     * 抽样状态
     */
    @SQLField(column = "sampling_status", type = "int", len = 1, constraint = @Constraints(allowNull = true), defaultValue = "0")
    private Integer samplingStatus;

    /**
     * 做这份问卷时拨打的号码
     */
    @SQLField(column = "last_dial_no", type = "varchar", len = 255)
    private String lastDialNo;

    /**
     * 二维码扫描状态
     */
    @SQLField(column = "qr_scan_status", type = "int", len = 1, constraint = @Constraints(allowNull = true), defaultValue = "0")
    private Integer qrScanStatus;

    /**
     * 上次修改源
     */
    @SQLField(column = "last_modify_resource", type = "varchar", len = 255)
    private String lastModifyResource;

    /**
     * 是否监听
     */
    @SQLField(column = "is_monitor", type = "int", len = 1, constraint = @Constraints(allowNull = false), defaultValue = "0")
    private Integer isMonitor;

    /**
     * 是否抽奖
     */
    @SQLField(column = "if_bonus", type = "int", len = 1, constraint = @Constraints(allowNull = false), defaultValue = "0")
    private Integer ifBonus;

    /**
     * 是否发红包
     */
    @SQLField(column = "is_send_redpacket", type = "int", len = 1, constraint = @Constraints(allowNull = false), defaultValue = "0")
    private Integer isSendRedpacket;

    /**
     * 浏览器类型
     */
    @SQLField(column = "explorer_type", type = "varchar", len = 255)
    private String explorerType;

    /**
     * 浏览器版本
     */
    @SQLField(column = "explorer_version", type = "varchar", len = 255)
    private String explorerVersion;

    /**
     * IP
     */
    @SQLField(column = "ip_address", type = "varchar", len = 255)
    private String ipAddress;

    /**
     * 省份
     */
    @SQLField(column = "ip_province", type = "varchar", len = 255)
    private String ipProvince;

    /**
     * 国家
     */
    @SQLField(column = "ip_country", type = "varchar", len = 255)
    private String ipCountry;

    /**
     * 答卷开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "start_time", type = "datetime", constraint = @Constraints(allowNull = false))
    private Date startTime;

    /**
     * 答卷结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "end_time", type = "datetime", constraint = @Constraints(allowNull = false))
    private Date endTime;

    /**
     * 分数
     */
    @SQLField(column = "score", type = "numeric")
    private Double score;

    /**
     * 提交的数据
     */
    @SQLField(column = "submit_data", type = "longtext")
    private String submitData;

    /**
     * 提交状态
     */
    @SQLField(column = "submit_state", type = "longtext")
    private String submitState;

    /**
     * 答卷实际作答时长
     */
    @SQLField(column = "response_duration", type = "long")
    private Long responseDuration;

    /**
     * 答卷类型
     */
    @SQLField(column = "response_type", type = "int", len = 1, constraint = @Constraints(allowNull = false))
    private Integer responseType;

    /**
     * 答卷状态
     */
    @SQLField(column = "response_status", type = "int", len = 11, constraint = @Constraints(allowNull = false))
    private Integer responseStatus;

    /**
     * 答卷数据，json格式
     */
    @SQLField(column = "response_data", type = "longtext")
    private String responseData;

    /**
     * 问题数据，json格式
     */
    @SQLField(column = "question_data", type = "longtext")
    private String questionData;

    /**
     * 当前答到第几题
     */
    @SQLField(column = "answered_current", type = "varchar", len = 255)
    private String answeredCurrent;

    /**
     * 已经回答过的问题，数组形式
     */
    @SQLField(column = "answered_questions", type = "text")
    private String answeredQuestions;

    /**
     * 配额id，数组形式
     */
    @SQLField(column = "quota_ids", type = "text")
    private String quotaIds;

    /**
     * is_exception	是否异常答卷
     */
    @SQLField(column = "is_exception", type = "int", len = 1, constraint = @Constraints(allowNull = false))
    private Integer isException;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @SQLField(column = "create_time", type = "datetime", constraint = @Constraints(allowNull = false))
    private Date createTime;
}
