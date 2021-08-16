package com.monetware.ringsurvey.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/02 10:25
 * @Description: 前100答卷定位信息
 **/
@Data
public class ResponseLocationDTO {
    /**
     * 经度
     */
    private String lon;

    /**
     * 纬度
     */
    private String lat;

    /**
     * count
     */
    private Integer count;
}
