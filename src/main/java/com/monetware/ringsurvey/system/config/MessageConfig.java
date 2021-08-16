package com.monetware.ringsurvey.system.config;

import com.monetware.ringsurvey.business.pojo.po.publishConfig.MessageAccount;
import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 短信发布配置
 */
@Component
@ConfigurationProperties(prefix = "message.config")
//@EnableAutoConfiguration(exclude = {MultipartAutoConfiguration.class})
@PropertySource("classpath:/application.yml")
@Data
public class MessageConfig {
    private List<MessageAccount> list;
}
