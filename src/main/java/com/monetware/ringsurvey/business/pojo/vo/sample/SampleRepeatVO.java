package com.monetware.ringsurvey.business.pojo.vo.sample;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-04-26
 */
@Data
public class SampleRepeatVO extends BaseVO {

    private List<String> fields;

    private String groupBy;

    private Integer userId;

}
