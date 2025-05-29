package com.bgasol.plugin.tomcat.config;

import org.apache.tomcat.websocket.server.WsSci;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketTomcatConfig {
    @Bean
    public ServletContextInitializer initializer() {
        return servletContext -> new WsSci().onStartup(null, servletContext);
    }
}
