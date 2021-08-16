package com.monetware.ringsurvey.business.pojo.dto.response;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectSample;
import lombok.Data;

import java.util.Date;

/**
 * 答卷导出查询DTO
 */
@Data
public class ResponseExportSelectDTO extends BaseProjectSample {

    private String userName;

    private String userPhone;

    private String userEmail;

    private Integer responseId;

    private Integer questionnaireId;

    private Integer responseType;

    private Integer responseStatus;

    private String responseData;

    private String submitData;

    private String questionData;

    private String startTime;

    private String endTime;

    private Long responseLen;

    private String auditUserName;

    private Integer auditResult;

    private String auditScore;

    private String auditTime;

    private String auditDesc;

    private String auditNote;

}
