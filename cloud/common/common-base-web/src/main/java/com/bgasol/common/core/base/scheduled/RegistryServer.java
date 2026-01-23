package com.bgasol.common.core.base.scheduled;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.bgasol.common.constant.value.SystemConfigValues.SERVICE_REGISTRY_KEY;

@Server
@RequiredArgsConstructor
public class RegistryServer {
    public static final String POD_IP = System.getenv("POD_IP");
    public static final String NODE_NAME = System.getenv("NODE_NAME");
    public static final Map<String, String> SERVER_INFO;

    static {
        SERVER_INFO = Map.of(
                "POD_IP", POD_IP,
                "NODE_NAME", NODE_NAME
        );
    }

    private final RedissonClient redissonClient;

    @Value("${spring.application.name}")
    private String serviceName;

    @Scheduled(fixedRate = 5000)
    public void registryServer() {
        RMapCache<String, Map<String, String>> serverInfo = redissonClient.getMapCache(SERVICE_REGISTRY_KEY);
        serverInfo.put(serviceName + ":" + NODE_NAME, SERVER_INFO, 10, TimeUnit.SECONDS);
    }
}
