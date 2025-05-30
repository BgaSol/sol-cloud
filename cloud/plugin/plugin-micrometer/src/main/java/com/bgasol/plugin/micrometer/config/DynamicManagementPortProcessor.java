package com.bgasol.plugin.micrometer.config;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DynamicManagementPortProcessor implements EnvironmentPostProcessor {
    private static final int OFFSET = 100;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String portStr = environment.getProperty("server.port", "8080");
        int serverPort = Integer.parseInt(portStr);
        int managementPort = serverPort + OFFSET;

        Map<String, Object> map = new HashMap<>();
        // 使用另外端口，不暴露
        map.put("management.server.port", managementPort);

        environment.getPropertySources().addFirst(new MapPropertySource("dynamic-management-port", map));
    }
}