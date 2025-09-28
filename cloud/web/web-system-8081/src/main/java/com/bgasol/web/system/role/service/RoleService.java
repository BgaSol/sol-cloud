package com.bgasol.web.system.role.service;

import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.web.system.menu.service.MenuService;
import com.bgasol.web.system.permission.service.PermissionService;
import com.bgasol.web.system.role.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService extends BaseService<RoleEntity, BasePageDto<RoleEntity>> {
    private final RoleMapper roleMapper;

    private final MenuService menuService;
    private final PermissionService permissionService;

    private final RedissonClient redissonClient;

    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

    @Override
    public RoleMapper commonBaseMapper() {
        return roleMapper;
    }

    @Override
    public void findOtherTable(List<RoleEntity> list) {
        List<String> roleIds = list.stream().map(RoleEntity::getId).toList();

        Map<String, List<String>> menuIdGroup = this.findFromTableBatch(
                "system_c_role_menu", "role_id", roleIds, "menu_id"
        );
        Map<String, List<String>> permissionIdGroup = this.findFromTableBatch(
                "system_c_role_permission", "role_id", roleIds, "permission_id"
        );

        Set<String> allMenuIds = menuIdGroup
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        Set<String> allPermissionIds = permissionIdGroup
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        Map<String, MenuEntity> menuMap = menuService
                .findByIds(allMenuIds.toArray(String[]::new))
                .stream()
                .collect(Collectors.toMap(MenuEntity::getId, Function.identity()));
        Map<String, PermissionEntity> permissionMap = permissionService
                .findByIds(allPermissionIds.toArray(String[]::new))
                .stream()
                .collect(Collectors.toMap(PermissionEntity::getId, Function.identity()));

        list.forEach(roleEntity -> {
            roleEntity.setMenus(menuIdGroup
                    .getOrDefault(roleEntity.getId(), List.of())
                    .stream()
                    .map(menuMap::get)
                    .filter(ObjectUtils::isNotEmpty)
                    .toList());
            roleEntity.setPermissions(permissionIdGroup
                    .getOrDefault(roleEntity.getId(), List.of())
                    .stream()
                    .map(permissionMap::get)
                    .filter(ObjectUtils::isNotEmpty)
                    .toList()
            );
        });
    }

}
