package com.bgasol.plugin.websocket.handler;

import com.bgasol.plugin.websocket.dto.WsSendMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

import static com.bgasol.common.util.WSUtils.GetWSTopic;

@Component
@RequiredArgsConstructor
public class PingMessageHandler implements MyMessageHandler {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.name}")
    private String serviceName;

    public static String PING = "ping";
    public static Map<String, String> PingResponse = Map.of("type", "pong");

    @SneakyThrows
    @Override
    public void handle(WebSocketSession session, String payload) {
        rabbitTemplate.convertAndSend(GetWSTopic(serviceName), "", WsSendMessageDto.builder()
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
