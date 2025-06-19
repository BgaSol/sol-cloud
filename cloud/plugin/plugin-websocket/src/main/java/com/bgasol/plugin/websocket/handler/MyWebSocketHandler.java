package com.bgasol.plugin.websocket.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.bgasol.plugin.websocket.dto.WsSendMessageDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final RedissonClient redissonClient;
    private final List<MyMessageHandler> myMessageHandlers;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    @SneakyThrows
    public void afterConnectionEstablished(WebSocketSession session) {
        MultiValueMap<String, String> params = UriComponentsBuilder.newInstance()
                .query(Objects.requireNonNull(session.getUri()).getRawQuery())
                .build()
                .getQueryParams();
        List<String> token = params.get(StpUtil.getTokenName());
        String userId = (String) StpUtil.getLoginIdByToken(token.get(0));
        session.getAttributes().put("userId", userId);
        session.getAttributes().put("token", token.get(0));
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);
        String type = jsonNode.get("type").asText();
        if ("ping".equals(type)) {
            // 处理心跳消息
            session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
            return;
        }
        for (MyMessageHandler handler : myMessageHandlers) {
            if (handler.support(type)) {
                handler.handle(session, payload);
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
                return;
            }
        }

        log.warn("未找到消息类型 {} 的处理器", type);
    }


    @PostConstruct
    public void subscribeToTopic() {
        RTopic topic = redissonClient.getTopic("ws");

        topic.addListener(WsSendMessageDto.class, (channel, msg) -> {
            for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                String sessionId = entry.getKey();
                WebSocketSession session = entry.getValue();
                boolean send = true;
                if (ObjectUtils.isNotEmpty(msg.getUserId())) {
                    String userId = (String) session.getAttributes().get("userId");
                    if (!Objects.equals(userId, msg.getUserId())) {
                        send = false;
                    }
                }
                if (ObjectUtils.isNotEmpty(msg.getSessionId())) {
                    if (!Objects.equals(sessionId, msg.getSessionId())) {
                        send = false;
                    }
                }
                int chunkSize = 10000;
                if (send) {
                    try {
                        UUID uuid = UUID.randomUUID();
                        int size = msg.getJson().length() / chunkSize + (msg.getJson().length() % chunkSize == 0 ? 0 : 1);
                        // 分块发送消息
                        for (int index = 0; index < size; index++) {
                            int start = index * chunkSize;
                            int end = Math.min(start + chunkSize, msg.getJson().length());
                            String chunk = msg.getJson().substring(start, end);
                            session.sendMessage(new TextMessage("#(" + msg.getType() + ")#(" + uuid + ")#(" + size + ")#(" + index + ")" + chunk));
                        }
                    } catch (IOException e) {
                        sessions.remove(sessionId);
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}