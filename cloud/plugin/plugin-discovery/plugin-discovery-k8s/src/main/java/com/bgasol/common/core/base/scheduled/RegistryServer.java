package com.bgasol.common.core.base.scheduled;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.bgasol.common.constant.value.SystemConfigValues.*;

@Service
@RequiredArgsConstructor
public class RegistryServer {
    @Value("${system.node-name}")
    private String nodeName;
    @Value("${system.node-ip}")
    private String nodeIp;

    private final RedissonClient redissonClient;

    @Value("${spring.application.name}")
    private String serviceName;

    @Scheduled(fixedRate = 5000)
    public void registryCurrentServer() {
        RMapCache<String, Map<String, String>> serverInfo = redissonClient.getMapCache(SERVICE_REGISTRY_KEY);
        serverInfo.put(serviceName + nodeName, Map.of(
                NODE_NAME_KEY, nodeName,
                NODE_IP_KEY, nodeIp
        ), 10, TimeUnit.SECONDS);
    }
}
