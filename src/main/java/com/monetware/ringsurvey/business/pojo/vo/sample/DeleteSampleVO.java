package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/18 18:18
 */
@Data
public class DeleteSampleVO {

    private Integer projectId;

    private List<Integer> sampleIds;
}
