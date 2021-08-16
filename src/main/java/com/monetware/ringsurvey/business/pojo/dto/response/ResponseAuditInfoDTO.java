package com.monetware.ringsurvey.business.pojo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectSample;
import com.monetware.ringsurvey.business.pojo.po.BaseResponseAudio;
import com.monetware.ringsurvey.business.pojo.vo.response.ResponseAuditInfoVO;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Linked
 * @date 2020/3/27 10:39
 */
@Data
public class ResponseAuditInfoDTO {

    private Integer id;

    private String name;

    private Integer ifVirtual;
    /**
     * 答卷有效性
     */
    private Integer responseType;

    /**
     * 答卷状态
     */
    private String responseStatus;

    /**
     * 答卷状态说明
     */
    private String statusInfo;

    /**
     * 审核状态
     */
    private Integer auditResult;


    private Integer auditTimes;


    private String sampleGuid;

    private String ipProvince;

    private Integer questionnaireId;

    private Integer version;

    /**
     * 访问员
     */
    private String interviewerName;

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
     * 答卷时长
     */
    private Long responseDuration;

    /**
     * 答卷时长格式化
     */
    private String responseDurationStr;

    /**
     * 问题答案选项
     */
    private List<HashMap<String, Object>> answeredQuestions;

    /**
     * 审核日志
     */
    private List<ResponseAuditLogDTO> responseAuditLogs;

    /**
     * 样本属性
     */
    private List<HashMap<String, Object>> samplePropertyMapList;

    /**
     * 录音列表
     */
    private List<BaseResponseAudio> audioList;

    /**
     * 是否有上一个
     */
    private boolean hasPrev;

    private ResponseAuditInfoVO prevInfo;

    /**
     * 是否有下一个
     */
    private boolean hasNext;

    private ResponseAuditInfoVO nextInfo;

    /**
     * 是否有红包
     */
    private boolean hasRedPacket;

    private boolean hasSendFlag;

}
