package com.bgasol.web.file;

import com.bgasol.common.constant.value.FileConfigValues;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Collections;

@SpringBootApplication(scanBasePackages = {
        "com.bgasol.model.file",
        "com.bgasol.web.file",
        "com.bgasol.common",
        "com.bgasol.plugin"
})
@MapperScan(basePackages = {
        "com.bgasol.web.file.**.mapper",
        "com.bgasol.common.**.mapper"})
@EnableDiscoveryClient
@EnableFeignClients("com.bgasol.**.api")
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(FileApplication.class);
        application.setDefaultProperties(Collections.singletonMap("spring.application.name", FileConfigValues.SERVICE_NAME));
        application.run(args);
    }
}
