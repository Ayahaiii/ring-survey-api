package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-26
 */
@Data
public class SampleMoreAddressVO {

    private Integer id;

    private Integer projectId;

    private String name;

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

    /**
     * 镇
     */
    private String town;

    /**
     * 村
     */
    private String village;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 备注
     */
    private String description;

}
