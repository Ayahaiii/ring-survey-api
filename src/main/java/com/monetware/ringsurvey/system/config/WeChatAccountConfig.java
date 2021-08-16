package com.monetware.ringsurvey.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "wechat")
@PropertySource("classpath:/application.yml")
public class WeChatAccountConfig {
    /**
     * 公众平台Id
     */
    private String myAppId;

    /**
     * 公众平台密钥
     */
    private String myAppSecret;

    /**
     * 商户Id
     */
    private String mchId;

    /**
     * 商户密钥
     */
    private String mchKey;

    /**
     * 商户证书路径
     */
    private String certPath;

    /**
     * 微信支付异步通知地址
     */
    private String notifyUrl;

    /**
     * 回调地址
     */
    private String callBackUrl;
}
