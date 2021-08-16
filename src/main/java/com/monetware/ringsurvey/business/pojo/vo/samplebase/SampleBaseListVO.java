package com.monetware.ringsurvey.business.pojo.vo.samplebase;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/4/1 17:12
 */
@Data
public class SampleBaseListVO extends PageParam {

    /**
     * 1:编号 2:名称
     */
    private Integer searchType;

    private String keyword;


}
