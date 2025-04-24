package com.bgasol.web.system.user.cache;

import com.bgasol.model.system.user.entity.UserEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "system:entity:user")
public class UserCache {

    @CachePut(key = "#id")
    public UserEntity saveUserEntity(String id, UserEntity userEntity) {
        return userEntity;
    }

    @Cacheable(key = "#id")
    public UserEntity getUserEntity(String id) {
        return null;
    }

    @CacheEvict(key = "#id")
    public void removeUserEntity(String id) {
    }
}