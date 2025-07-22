package com.bgasol.plugin.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${system.describe}")
    private String serverDescribe;

    @Value("${sa-token.token-name}")
    private String tokenName;

    /**
     * 构建 OpenAPI 配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(createInfo())
                .servers(createServers())
                // 添加全局安全要求
                .addSecurityItem(new SecurityRequirement().addList(tokenName))
                .components(createComponents());
    }

    /**
     * 创建 API 信息
     */
    private Info createInfo() {
        return new Info()
                .title(serviceName)
                .version("1.0.0")
                .description(serverDescribe);
    }

    /**
     * 创建服务列表
     */
    private List<Server> createServers() {
        List<Server> servers = new ArrayList<>();
        servers.add(createServer("http://localhost:" + 9527 + "/" + serviceName, "网关"));
        return servers;
    }

    /**
     * 初始化服务器信息
     */
    public Server createServer(String url, String description) {
        Server server = new Server();
        server.setUrl(url);
        server.setDescription(description);
        return server;
    }

    /**
     * 创建安全方案
     */
    private Components createComponents() {
        Components components = new Components();
        components.addSecuritySchemes(
                tokenName,
                new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name(tokenName)
                        .description("身份验证 Token")
                        .bearerFormat("UUID")
        );
        return components;
    }
}
