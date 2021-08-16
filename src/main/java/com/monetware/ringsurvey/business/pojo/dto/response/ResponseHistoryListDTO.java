package com.monetware.ringsurvey.business.pojo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectSample;
import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/4/17 18:41
 */
@Data
public class ResponseHistoryListDTO extends BaseProjectSample {

    private String code;

    private String moduleName;


    private String sampleMark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private String responseDuration;

    private String interviewer;

    private Integer responseStatus;

    private String completionRate;


}
