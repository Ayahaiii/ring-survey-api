package com.monetware.ringsurvey.business.pojo.po.publishConfig;

import lombok.Data;

/**
 * 助通短信平台配置
 */
@Data
public class ZtMessageConfig {

    private String messageType = "ZT";

    private String url;

    private String username;

    private String password;

    private String productId;

    private String sign;

}
