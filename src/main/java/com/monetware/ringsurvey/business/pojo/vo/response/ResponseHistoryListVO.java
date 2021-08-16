package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/3/25 19:10
 */
@Data
public class ResponseHistoryListVO extends PageParam {

    private String responseGuid;

    private String sampleMark;

    private Integer interviewerId;

    private Integer responseStatus;

    private String startTime;

    private String endTime;




}
