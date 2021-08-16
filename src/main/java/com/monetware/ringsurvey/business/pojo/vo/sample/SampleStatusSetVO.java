package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

@Data
public class SampleStatusSetVO {

    private Integer projectId;

    private String sampleGuid;

    private Integer sampleId;

    private Integer status;

    private String notes;

}
