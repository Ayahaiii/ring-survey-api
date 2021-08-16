package com.monetware.ringsurvey.business.pojo.vo.sample;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-26
 */
@Data
public class SampleAssignVO extends BaseVO {

    private Integer userId;

    private String property;

    private String keyword;

    private String authCondition;

}
