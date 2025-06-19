package com.bgasol.plugin.websocket.handler;

import org.springframework.web.socket.WebSocketSession;

public interface MyMessageHandler {
    void handle(WebSocketSession session, String payload);

    boolean support(String type);
}