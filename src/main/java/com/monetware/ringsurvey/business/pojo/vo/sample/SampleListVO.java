package com.monetware.ringsurvey.business.pojo.vo.sample;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/2/18 13:40
 */
@Data
public class SampleListVO extends PageParam {

    private Integer status;

    private String managerName;

    private Integer userId;

    private String keyword;

    private String sampleCondition;
}
