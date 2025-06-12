package com.bgasol.common.constant.value;

import org.apache.commons.rng.simple.RandomSource;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisConfigValues {
    // 默认缓存时间 设置多个 防止redis雪崩
    public final static int DEFAULT_EXPIRE = 60 * 20;

    // 默认缓存时间单位
    public final static TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    public static int randomizeTtl() {
        return randomizeTtl(DEFAULT_EXPIRE, 5 * 60);
    }

    /**
     * 生成带扰动的过期时间（单位：秒）
     *
     * @param baseTtl   原始 TTL（例如：3600 表示一小时）
     * @param jitterMax 最大扰动值（例如：±300 表示最多增减5分钟）
     * @return 实际使用的 TTL
     */
    public static int randomizeTtl(int baseTtl, int jitterMax) {
        int jitter = RandomSource.JSF_64.create().nextInt(-jitterMax, jitterMax);
        return baseTtl + jitter;
    }
}
