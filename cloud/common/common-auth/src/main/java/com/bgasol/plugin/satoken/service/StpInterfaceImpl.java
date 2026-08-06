package com.bgasol.plugin.satoken.service;

import cn.dev33.satoken.stp.StpInterface;
import com.bgasol.common.core.base.model.NodeConfig;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.role.api.RoleApi;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/// 权限数据加载源
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserApi userApi;
    private final RoleApi roleApi;
    private final NodeConfig nodeConfig;

    /// 返回一个账号所拥有的权限码集合
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Set<String> roleIds = userApi.findById((String) loginId, false)
                .getData()
                .getRoles()
                .stream()
                .map(RoleEntity::getId)
                .collect(Collectors.toSet());
        return roleApi.findByIds(roleIds, false)
                .getData()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .filter(permission -> nodeConfig.getAppName().equals(permission.getMicroService()))
                .map(PermissionEntity::getCode)
                .distinct()
                .collect(Collectors.toList());
    }

    /// 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return userApi.findById((String) loginId, false)
                .getData()
                .getRoles()
                .stream()
                .map(RoleEntity::getId)
                .toList();
    }
}