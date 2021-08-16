package com.monetware.ringsurvey.business.pojo.vo.monitor;


import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/02 10:54
 * @Description: 通过省市区获取答卷数量
 **/
@Data
public class GetSampleCountByAddressVO {

    private Integer projectId;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;
}
