package com.bgasol.common.core.base.scheduled;

import com.bgasol.common.core.base.model.NodeConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.bgasol.common.constant.value.SystemConfigValues.SERVICE_REGISTRY_KEY;

@Service
@RequiredArgsConstructor
public class RegistryServer {
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final NodeConfig nodeConfig;
    private final static int REGISTRY_TIMEOUT_SECONDS = 5;

    @Scheduled(fixedRate = REGISTRY_TIMEOUT_SECONDS * 1000)
    public void registryCurrentServer() throws JsonProcessingException {
        RSetCache<String> registrySet = redissonClient.getSetCache(SERVICE_REGISTRY_KEY);
        registrySet.add(objectMapper.writeValueAsString(nodeConfig),
                REGISTRY_TIMEOUT_SECONDS + 1,
                TimeUnit.SECONDS);
    }
}
