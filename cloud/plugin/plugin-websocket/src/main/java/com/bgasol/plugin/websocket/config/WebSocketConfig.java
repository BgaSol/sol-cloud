package com.bgasol.plugin.websocket.config;

import com.bgasol.plugin.websocket.handler.WebSocketHandlerImpl;
import com.bgasol.plugin.websocket.interceptor.PlusWebSocketInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandlerImpl handler;
    private final PlusWebSocketInterceptor plusWebSocketInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws")
                .addInterceptors(plusWebSocketInterceptor)
                .setAllowedOrigins("*");
    }
}