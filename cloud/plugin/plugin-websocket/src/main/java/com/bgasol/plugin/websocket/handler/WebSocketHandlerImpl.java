package com.bgasol.plugin.websocket.handler;

import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.plugin.websocket.dto.SendMessageChunkDto;
import com.bgasol.plugin.websocket.dto.WsSendMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.bgasol.common.util.WSUtils.GetWSTopic;
import static com.bgasol.plugin.websocket.interceptor.PlusWebSocketInterceptor.USER_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandlerImpl implements WebSocketHandler {
    private final RedissonClient redissonClient;

    private final List<MyMessageHandler> myMessageHandlers;

    private final ObjectMapper objectMapper;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Value("${spring.application.name}")
    private String serviceName;

    // 在 WebSocket 协商成功且 WebSocket 连接打开并可供使用后调用。
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    // 当新的 WebSocket 消息到达时调用。
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws JsonProcessingException {
        if (!(message instanceof TextMessage textMessage)) {
            throw new IllegalStateException("意外的 WebSocket 消息类型: " + message);
        }
        String payload = textMessage.getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);
        String type = jsonNode.get("type").asText();
        if (ObjectUtils.isNotEmpty(type)) {
            for (MyMessageHandler handler : myMessageHandlers) {
                if (handler.support(type)) {
                    handler.handle(session, payload);
                }
            }
        }
    }

    // 处理来自底层 WebSocket 消息传输的错误。
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("websocket错误,sessionId:{},userId:{}", session.getId(), session.getAttributes().get(USER_ID), exception);
    }

    // 在任一端关闭 WebSocket 连接后或在发生传输错误后调用。
    // 尽管从技术上讲，会话可能仍处于打开状态，但根据底层实现，不建议在此时发送消息，并且很可能不会成功。
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("websocket断开链接,sessionId:{},userId:{}", session.getId(), session.getAttributes().get(USER_ID));
        sessions.remove(session.getId());
    }

    // WebSocketHandler 是否处理部分消息。
    // 如果此标志设置为 true 并且底层 WebSocket 服务器支持部分消息，
    // 则可能会拆分大型 WebSocket 消息或未知大小的消息，并可能通过多次 handleMessage(WebSocketSession, WebSocketMessage)调用接收。
    // 该标志 WebSocketMessage.isLast() 指示消息是否为部分消息以及它是否为最后一部分。
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @PostConstruct
    public void subscribeToTopic() {
        RTopic topic = redissonClient.getTopic(GetWSTopic(serviceName));

        topic.addListener(WsSendMessageDto.class, this::onMessage);
    }

    private void onMessage(CharSequence channel, WsSendMessageDto msg) {
        sessions.entrySet().forEach(entry -> onMessage(entry, msg));
    }

    private void onMessage(Map.Entry<String, WebSocketSession> entry, WsSendMessageDto msg) {
        String sessionId = entry.getKey();
        WebSocketSession session = entry.getValue();
        boolean send = true;
        if (ObjectUtils.isNotEmpty(msg.getUserIds())) {
            String userId = (String) session.getAttributes().get(USER_ID);
            // 筛选用户
            if (!msg.getUserIds().contains(userId)) {
                send = false;
            }
        } else if (ObjectUtils.isNotEmpty(msg.getSessionIds())) {
            // 筛选会话
            if (!msg.getSessionIds().contains(sessionId)) {
                send = false;
            }
        }
        if (!send) {
            return;
        }
        String uuid = UUID.randomUUID().toString();
        String sendData;
        try {
            sendData = objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // 计算分块数
        int chunkSize = 10000;
        int size = sendData.length() / chunkSize + (sendData.length() % chunkSize == 0 ? 0 : 1);
        // 分块发送消息
        for (int index = 0; index < size; index++) {
            int start = index * chunkSize;
            int end = Math.min(start + chunkSize, sendData.length());
            String chunk = sendData.substring(start, end);
            String json = null;
            try {
                json = objectMapper.writeValueAsString(SendMessageChunkDto.builder()
                        .uuid(uuid)
                        .size(size)
                        .index(index)
                        .data(chunk).build());
            } catch (JsonProcessingException e) {
                throw new BaseException("序列化发送消息分块失败");
            }
            try {
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                throw new BaseException("发送WebSocket消息失败", e);
            }
        }
    }
}
