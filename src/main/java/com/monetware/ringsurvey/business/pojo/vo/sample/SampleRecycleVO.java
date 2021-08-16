package com.monetware.ringsurvey.business.pojo.vo.sample;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-04-26
 */
@Data
public class SampleRecycleVO extends BaseVO {

    private String type;

    private List<Integer> sampleIds;


}
