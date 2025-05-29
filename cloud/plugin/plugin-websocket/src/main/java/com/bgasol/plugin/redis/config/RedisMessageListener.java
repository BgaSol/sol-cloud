package com.bgasol.plugin.redis.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageListener {
    private final RedissonClient redissonClient;

    private final SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void subscribeToTopic() {
        RTopic topic = redissonClient.getTopic("topic");

        topic.addListener(String.class, (channel, msg) -> {
            log.info("Received message from Redis channel {}: {}", channel, msg);
            messagingTemplate.convertAndSend("/topic", msg);
        });
    }
}
