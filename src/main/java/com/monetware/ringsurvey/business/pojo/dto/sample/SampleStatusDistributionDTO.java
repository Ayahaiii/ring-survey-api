package com.monetware.ringsurvey.business.pojo.dto.sample;

import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/01 19:59
 * @Description: 样本状态分布
 **/
@Data
public class SampleStatusDistributionDTO {
    /**
     * 数量
     */
    private Integer count;

    /**
     * 状态
     */
    private Integer status;
}
