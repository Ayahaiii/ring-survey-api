package com.monetware.ringsurvey.business.pojo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectSample;
import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/3/24 11:27
 */
@Data
public class ResponseListDTO extends BaseProjectSample {

    private Integer responseId;

    /***
     * 样本标识
     */
    private String sampleMark;

    /**
     * 答卷标识
     */
    private String responseIdentifier;


    private Integer questionnaireId;

    private Integer sampleId;

    private String responseGuid;

    /**
     * 是否抽审
     */
    private Integer samplingStatus;

    /**
     * 审核状态
     */
    private String responseStatus;

    /***
     * 问卷编号
     */
    private String qnaireCode;

    /**
     * 问卷名称
     */
    private String moduleName;

    /**
     * 时长
     */
    private String duration;

    /**
     * 填答率
     */
    private String completionRate;

    /**
     * 督导员
     */
    private String supervisor;

    /**
     * 访问员
     */
    private String interviewer;

    private String auditor;

    /**
     * 审核次数
     */
    private Integer auditTimes;

    // private String sampleGuid;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 最近修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rLastModifyTime;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;
}
