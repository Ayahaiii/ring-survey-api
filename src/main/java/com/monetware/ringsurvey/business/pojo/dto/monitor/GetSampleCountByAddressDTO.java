package com.monetware.ringsurvey.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/02 11:11
 * @Description: 显示当前省份下样本数量汇总
 **/
@Data
public class GetSampleCountByAddressDTO {

    private String province;

    private String city;

    private String district;

    /**
     * 经度
     */
    private String lon;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 总数
     */
    private Integer count;

}
