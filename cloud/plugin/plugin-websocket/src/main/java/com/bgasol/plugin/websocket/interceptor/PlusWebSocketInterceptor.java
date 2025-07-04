package com.bgasol.plugin.websocket.interceptor;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket握手请求的拦截器
 *
 * @author zendwang
 */
@Slf4j
@Component
public class PlusWebSocketInterceptor implements HandshakeInterceptor {

    public static final String USER_ID = "USER_ID";

    // WebSocket握手之前执行的前置处理方法
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            String userId = StpUtil.getLoginIdAsString();
            attributes.put(USER_ID, userId);
            return true;
        } catch (NotLoginException e) {
            log.error("WebSocket 认证失败'{}',无法访问系统资源", e.getMessage());
            return false;
        }
    }

    // WebSocket握手成功后执行的后置处理方法
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}
