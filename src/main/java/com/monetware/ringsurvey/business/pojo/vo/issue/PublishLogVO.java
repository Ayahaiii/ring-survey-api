package com.monetware.ringsurvey.business.pojo.vo.issue;

import lombok.Data;

/**
 *
 */
@Data
public class PublishLogVO {

    private Integer type;
    private Integer status;
    private String sampleGuid;
    private String publishFrom;
    private String publishTo;
    private String subject;
    private String content;

}
