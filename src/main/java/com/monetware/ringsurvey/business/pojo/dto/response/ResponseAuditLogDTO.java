package com.monetware.ringsurvey.business.pojo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/3/27 14:31
 */
@Data
public class ResponseAuditLogDTO {

    private String auditor;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;

    private Integer auditResult;

    private String auditScore;

    private String auditComments;

}
