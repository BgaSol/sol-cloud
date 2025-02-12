package com.bgasol.gateway.filters;

import cn.dev33.satoken.same.SaSameUtil;
import com.bgasol.common.constant.value.GatewayConfigValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器，为请求添加 Same-Token
 */
@Component
@Slf4j
public class ForwardAuthFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 判断url是否包含"actuator"
        if (exchange.getRequest().getURI().getPath().contains("/actuator")) {
            // 响应404
            return exchange.getResponse().setComplete();
        }
        ServerHttpRequest oldRequest = exchange.getRequest();
        ServerHttpRequest newRequest = oldRequest.mutate()
                // 为请求追加 Same-Token 参数
                .header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken())
                .header(GatewayConfigValues.XFromGateway, "true")
                .build();
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        // 继续执行
        return chain.filter(newExchange);
    }
}
