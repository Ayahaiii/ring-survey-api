package com.monetware.ringsurvey.business.pojo.vo.issue;

import lombok.Data;

@Data
public class PublishConfigListVO {

    private Integer projectId;

    private Integer type;

    private String name;

    private String config;

}
