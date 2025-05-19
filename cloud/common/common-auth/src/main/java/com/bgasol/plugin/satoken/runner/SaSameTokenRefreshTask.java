package com.bgasol.plugin.satoken.runner;

import cn.dev33.satoken.same.SaSameUtil;
import lombok.RequiredArgsConstructor;
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

    @Scheduled(cron = "0 0 * * * ?")
    public void refreshToken() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(1, 30, TimeUnit.SECONDS);
            if (acquired) {
                SaSameUtil.refreshToken();
                Thread.sleep(1000);
                log.info("令牌刷新令牌成功");
            } else {
                log.info("未获取到锁");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("被中断", e);
        } catch (Exception e) {
            log.error("令牌刷新期间出现意外错误", e);
        } finally {
            if (acquired) {
                try {
                    lock.unlock();
                    log.info("锁已释放");
                } catch (IllegalMonitorStateException e) {
                    log.warn("未获取到令牌锁", e);
                }
            }
        }
    }
}