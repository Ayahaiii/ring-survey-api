package com.monetware.ringsurvey.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/02 19:51
 * @Description: 获取来源报告传出dto
 **/
@Data
public class GetSourceReportDTO {

    /**
     * 省
     */
    private String province;

    /**
     * count
     */
    private Integer count;

    /**
     * 经度
     */
    private String lon;

    /**
     * 纬度
     */
    private String lat;
}
