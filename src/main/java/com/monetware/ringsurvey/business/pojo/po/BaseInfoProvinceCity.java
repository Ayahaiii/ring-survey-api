package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Simo
 * @date 2020-02-18
 */
@Data
@Table(name = "rs_info_prov_city")
public class BaseInfoProvinceCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String provinceCode;

    private String provinceName;

    private String cityCode;

    private String cityName;

    private String districtCode;

    private String districtName;

    private String townCode;

    private String townName;

    private String villageCode;

    private String villageName;

    private String cityTownClassifyCode;

}
