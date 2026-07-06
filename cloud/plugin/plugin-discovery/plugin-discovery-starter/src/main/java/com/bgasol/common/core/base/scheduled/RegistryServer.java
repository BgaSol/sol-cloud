package com.bgasol.common.core.base.scheduled;

import com.bgasol.common.core.base.model.NodeConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistryServer {

    private final RedissonClient redissonClient;
    private final NodeConfig nodeConfig;

    public final static String SERVICE_REGISTRY_KEY = "SERVICE_REGISTRY";
    private final static int REGISTRY_BROADCAST_INTERVAL_SECONDS = 5;

    @Getter
    private final Cache<String, NodeConfig> nodeConfigCacheMap = Caffeine
            .newBuilder()
            .expireAfterWrite(REGISTRY_BROADCAST_INTERVAL_SECONDS * 2, TimeUnit.SECONDS)
            .build();

    private boolean online = false;

    @PostConstruct
    public void init() {
        RTopic registryTopic = redissonClient.getTopic(SERVICE_REGISTRY_KEY);
        registryTopic.addListener(NodeConfig.class, (channel, message) -> {
            nodeConfigCacheMap.put(message.getAppName() + ":" + message.getName(), message);
        });
        online = true;
        registryCurrentServer();
    }

    @Scheduled(fixedRate = REGISTRY_BROADCAST_INTERVAL_SECONDS * 1000)
    public void registryCurrentServer() {
        if (!online) {
            return;
        }
        redissonClient.getTopic(SERVICE_REGISTRY_KEY).publish(nodeConfig);
    }

    public boolean isOnLine(String appName, String name) {
        return nodeConfigCacheMap.asMap().containsKey(appName + ":" + name);
    }

}
