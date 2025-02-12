package com.bgasol.gateway;

import com.bgasol.common.constant.value.GatewayConfigValues;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Collections;

@SpringBootApplication(scanBasePackages = {
        "com.bgasol.gateway",
        "com.bgasol.plugin",
        "com.bgasol.common"
})
@EnableDiscoveryClient
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GatewayApplication.class);
        application.setDefaultProperties(Collections.singletonMap("spring.application.name", GatewayConfigValues.SERVICE_NAME));
        application.run(args);
    }
}
