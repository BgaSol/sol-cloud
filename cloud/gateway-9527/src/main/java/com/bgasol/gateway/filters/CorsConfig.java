package com.bgasol.gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@Slf4j
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); // 允许所有来源，也可以指定具体的域名
        config.addAllowedHeader("*"); // 允许所有请求头
        config.addAllowedMethod("*"); // 允许所有方法（GET、POST等）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(exchange -> {
            String path = exchange.getRequest().getPath().value();
            if (path.contains("/ws")) {
                // 不进行 CORS 处理，直接返回 null（即不添加任何 CORS Header）
                return null;
            }
            return source.getCorsConfiguration(exchange);
        });
    }
}
