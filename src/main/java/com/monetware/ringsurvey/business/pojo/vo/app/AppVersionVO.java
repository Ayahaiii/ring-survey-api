package com.monetware.ringsurvey.business.pojo.vo.app;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-04-24
 */
@Data
public class AppVersionVO {

    private String versionName;

    private Integer versionCode;

    private String versionDesc;

    private Integer isMust;

    private String versionUrl;

}
