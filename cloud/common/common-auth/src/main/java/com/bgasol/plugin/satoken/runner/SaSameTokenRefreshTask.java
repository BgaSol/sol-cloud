package com.bgasol.plugin.satoken.runner;

import cn.dev33.satoken.same.SaSameUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Same-Token，定时刷新（分布式锁保证同一时刻只有一个实例执行）
 */
@Component
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class SaSameTokenRefreshTask {

    private final RedissonClient redissonClient;

    private static final String LOCK_KEY = "same-token:refresh:lock";

    @SneakyThrows
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshToken() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (!lock.tryLock(0, 5, TimeUnit.SECONDS)) {
            return;
        }
        SaSameUtil.refreshToken();
        log.info("Token refresh token successful");
    }
}