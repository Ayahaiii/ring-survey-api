package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-26
 */
@Data
public class SampleUpdateVO {

    private Integer projectId;

    private Integer status;

    private List<String> sampleGuids;
}
