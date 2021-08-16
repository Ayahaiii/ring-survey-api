package com.monetware.ringsurvey.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author Simo
 * @date 2019-03-13
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndExporter() {
        return new ServerEndpointExporter();
    }
}
