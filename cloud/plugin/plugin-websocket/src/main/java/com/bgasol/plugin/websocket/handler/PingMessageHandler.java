package com.bgasol.plugin.websocket.handler;

import com.bgasol.plugin.websocket.dto.WsSendMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

import static com.bgasol.common.util.WSUtils.GetWSTopic;

@Component
@RequiredArgsConstructor
public class PingMessageHandler implements MyMessageHandler {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String serviceName;

    public static String PING = "ping";
    public static Map<String, String> PingResponse = Map.of("type", "pong");

    @SneakyThrows
    @Override
    public void handle(WebSocketSession session, String payload) {
        RTopic ws = redissonClient.getTopic(GetWSTopic(serviceName));
        ws.publish(WsSendMessageDto.builder()
                .json(objectMapper.writeValueAsString(PingResponse))
                .type(PING)
                .sessionIds(List.of(session.getId()))
                .build());
    }

    @Override
    public boolean support(String type) {
        return PING.equals(type);
    }
}
