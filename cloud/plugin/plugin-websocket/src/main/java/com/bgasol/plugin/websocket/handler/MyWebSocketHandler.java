package com.bgasol.plugin.websocket.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.bgasol.plugin.websocket.dto.WsSendMessageDto;
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
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final RedissonClient redissonClient;

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
                if (send) {
                    try {
                        session.sendMessage(new TextMessage(msg.getJson()));
                    } catch (IOException e) {
                        sessions.remove(sessionId);
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}