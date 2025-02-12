package com.bgasol.web.system;

import com.bgasol.common.constant.value.SystemConfigValues;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Collections;

@SpringBootApplication(scanBasePackages = {
        "com.bgasol.model.system",
        "com.bgasol.web.system",
        "com.bgasol.common",
        "com.bgasol.plugin"
})
@MapperScan(basePackages = {
        "com.bgasol.web.system.**.mapper",
        "com.bgasol.common.**.mapper"})
@EnableDiscoveryClient
@EnableFeignClients("com.bgasol.**.api")
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SystemApplication.class);
        application.setDefaultProperties(Collections.singletonMap("spring.application.name", SystemConfigValues.SERVICE_NAME));
        application.run(args);
    }
}
