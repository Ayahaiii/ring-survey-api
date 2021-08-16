package com.monetware.ringsurvey.business.pojo.vo.issue;

import lombok.Data;

import java.util.Date;

@Data
public class PublishTaskVO {

    private Integer type;
    private String sampleGuid;
    private String publishTo;
    private String subject;
    private String content;
    private Date sendTime;

}
