package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/6 12:28
 */
@Data
public class SampleAssignedVO {

    private Integer projectId;

    private List<Integer> sampleIds;

    private String keyword;

    private Integer managerId;

}
