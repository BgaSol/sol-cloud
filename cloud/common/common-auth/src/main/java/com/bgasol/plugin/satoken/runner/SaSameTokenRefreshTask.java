package com.bgasol.plugin.satoken.runner;

import cn.dev33.satoken.same.SaSameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
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

    @Scheduled(cron = "0 0 * * * ?")
    public void refreshToken() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        try {
            // 尝试获取锁，最多等待1秒，锁自动释放时间30秒
            boolean acquired = lock.tryLock(1, 30, TimeUnit.SECONDS);
            if (acquired) {
                SaSameUtil.refreshToken();
                log.info("Same-Token refresh token success");
                Thread.sleep(Duration.ofSeconds(1).toMillis());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 保留中断信号
            log.error("Same-Token refresh token interrupted", e);
        } finally {
            lock.unlock();
        }
    }
}