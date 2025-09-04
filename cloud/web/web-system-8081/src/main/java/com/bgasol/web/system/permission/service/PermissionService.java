package com.bgasol.web.system.permission.service;

import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.web.system.permission.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService extends BaseService<PermissionEntity, BasePageDto<PermissionEntity>> {
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

    @Override
    public Integer delete(String id) {
        PermissionEntity permissionEntity = this.findById(id);
        if (ObjectUtils.isEmpty(permissionEntity)) {
            return 1;
        }
        HashSet<String> permissionIds = new HashSet<>();
        permissionIds.add(id);
        List<PermissionEntity> permissions = findTreeAll(id, null);
        collectDeleteIds(permissions, permissionIds);
        permissionIds.forEach(e -> {
            this.permissionMapper.deleteFromTable("system_c_role_permission", "permission_id", e);
            super.delete(e);
        });
        return 1;
    }

    private void collectDeleteIds(List<PermissionEntity> permissions, HashSet<String> ids) {
        List<PermissionEntity> permissionEntityList = permissions.stream()
                .filter(e -> !e.getChildren().isEmpty())
                .flatMap(e -> e.getChildren().stream())
                .toList();

        // 将当前层级子部门的ID添加到删除集合中
        permissionEntityList.forEach(e -> ids.add(e.getId()));
        permissions.forEach(e -> ids.add(e.getId()));

        if (!permissionEntityList.isEmpty()) {
            // 递归处理下一层级的子部门
            this.collectDeleteIds(permissionEntityList, ids);
        }
    }
}
