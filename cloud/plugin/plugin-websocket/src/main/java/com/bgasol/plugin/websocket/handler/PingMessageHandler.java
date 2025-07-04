package com.bgasol.plugin.websocket.handler;

import com.bgasol.plugin.websocket.dto.WsSendMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PingMessageHandler implements MyMessageHandler {
    public static String PING = "ping";

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void handle(WebSocketSession session, String payload) {
        RTopic ws = redissonClient.getTopic("ws");
        ws.publish(WsSendMessageDto.builder()
                .json(objectMapper.writeValueAsString(Map.of("type", "pong")))
                .type(PING)
                .sessionIds(List.of(session.getId()))
                .build());
    }

    @Override
    public boolean support(String type) {
        return PING.equals(type);
    }
}
