package com.bgasol.web.system.user.cache;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CaptchaCache {
    private static final String KEY_PREFIX = "system:user:captcha:";
    private final RedissonClient redissonClient;

    public String save(String key, String code) {
        String redisKey = KEY_PREFIX + key;
        RBucket<String> bucket = redissonClient.getBucket(redisKey);
        // 缓存2分钟
        bucket.set(code, Duration.ofMinutes(2));
        return code;
    }

    public String getAndDelete(String key) {
        String redisKey = KEY_PREFIX + key;
        RBucket<String> bucket = redissonClient.getBucket(redisKey);
        return bucket.getAndDelete(); // 原子操作
    }
}
