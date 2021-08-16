package com.monetware.ringsurvey.business.pojo.dto.redpacket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包列表DTO
 */
@Data
public class RedPacketRecordListDTO {

    private Integer id;

    private Integer responseId;

    private String responseGuid;

    private Integer status;// 发送状态

    private String openId;

    private BigDecimal totalAmount;// 红包金额

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer auditUser;

    private Integer auditResult;// 审核结果

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;

    private Integer sampleId;

    private String sampleGuid;

    private String sampleName;

    private String sampleCode;

    private Integer moduleId;

    private String moduleCode;

    private String moduleName;

    private Integer questionnaireId;

}
