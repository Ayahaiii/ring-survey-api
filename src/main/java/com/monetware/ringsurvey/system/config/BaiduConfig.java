package com.monetware.ringsurvey.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "baidu")
@PropertySource("classpath:/application.yml")
@Data
public class BaiduConfig {

    private String ak;

}
