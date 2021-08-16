package com.monetware.ringsurvey.system.util.ip;

import lombok.Data;

/**
 * 根据太平洋ipJson接口解析的对象
 */
@Data
public class IpInfo {

    private String ip;

    private String pro;

    private String proCode;

    private String city;

    private String cityCode;

    private String region;

    private String regionCode;

    private String addr;

    private String regionNames;

    private String err;

}
