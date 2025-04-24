package com.bgasol.web.system.user.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "system:login:captcha", cacheManager = "captchaCacheManager")
public class CaptchaCache {

    @CachePut(key = "#key")
    public String saveCaptcha(String key, String code) {
        return code;
    }

    @Cacheable(key = "#key")
    public String getCaptcha(String key) {
        return null;
    }

    @CacheEvict(key = "#key")
    public void removeCaptcha(String key) {
    }
}
