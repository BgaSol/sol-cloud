package com.bgasol.web.system.user.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 验证码缓存服务
 */
@Service
@CacheConfig(cacheNames = "system:login:captcha")
public class CaptchaCache {

    @CachePut(key = "#key", cacheManager = "redisCacheManager")
    public String saveCaptcha(String key, String code) {
        return code;
    }

    @Cacheable(key = "#key", cacheManager = "redisCacheManager")
    public String getCaptcha(String key) {
        return null;
    }

    @CacheEvict(key = "#key", cacheManager = "redisCacheManager")
    public void removeCaptcha(String key) {
    }
}
