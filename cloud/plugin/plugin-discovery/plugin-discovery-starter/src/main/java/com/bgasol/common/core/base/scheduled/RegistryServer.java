package com.bgasol.common.core.base.scheduled;

import com.bgasol.common.core.base.model.NodeConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.bgasol.common.core.base.scheduled.RegistryRabbitConfig.SYSTEM_SERVICE_REGISTRY;

@Service
@RequiredArgsConstructor
public class RegistryServer {

    private final RabbitTemplate rabbitTemplate;
    private final NodeConfig nodeConfig;

    private final static int REGISTRY_BROADCAST_INTERVAL_SECONDS = 5;

    @Getter
    private final Cache<String, NodeConfig> nodeConfigCacheMap = Caffeine
            .newBuilder()
            .expireAfterWrite(REGISTRY_BROADCAST_INTERVAL_SECONDS * 2, TimeUnit.SECONDS)
            .build();

    private boolean online = false;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        online = true;
        registryCurrentServer();
    }

    @RabbitListener(queues = "#{serviceRegistryQueue.name}")
    public void onRegistryMessage(NodeConfig message) {
        nodeConfigCacheMap.put(message.getAppName() + ":" + message.getName(), message);
    }

    @Scheduled(fixedRate = REGISTRY_BROADCAST_INTERVAL_SECONDS * 1000)
    public void registryCurrentServer() {
        if (!online) {
            return;
        }
        rabbitTemplate.convertAndSend(SYSTEM_SERVICE_REGISTRY, "", nodeConfig);
    }

    public boolean isOnLine(String appName, String name) {
        return nodeConfigCacheMap.asMap().containsKey(appName + ":" + name);
    }

}
