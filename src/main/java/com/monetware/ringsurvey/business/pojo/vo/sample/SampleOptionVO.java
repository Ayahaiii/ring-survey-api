package com.monetware.ringsurvey.business.pojo.vo.sample;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * 回收、去重、未接触
 */
@Data
public class SampleOptionVO extends BaseVO {

    private List<Integer> statusList;

    private List<Integer> sampleIds;

    private Integer userId;

}
