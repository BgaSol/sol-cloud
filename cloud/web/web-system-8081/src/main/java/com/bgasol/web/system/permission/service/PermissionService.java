package com.bgasol.web.system.permission.service;

import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseTreeService;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.web.system.permission.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService extends BaseTreeService<PermissionEntity, BasePageDto<PermissionEntity>> {
    private final PermissionMapper permissionMapper;

    private final RedissonClient redissonClient;

    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

    @Override
    public PermissionMapper commonBaseMapper() {
        return permissionMapper;
    }

    public PermissionEntity init(PermissionEntity parentPermission) {
        if (this.cacheSearch(parentPermission.getId()) == null) {
            this.save(parentPermission);
        } else {
            this.update(parentPermission);
        }
        for (PermissionEntity permission : parentPermission.getChildren()) {
            if (this.cacheSearch(permission.getId()) == null) {
                this.save(permission);
            } else {
                this.update(permission);
            }
        }
        return this.findById(parentPermission.getId());
    }
}
