package com.bgasol.plugin.satoken.service;

import cn.dev33.satoken.stp.StpInterface;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.user.api.UserApi;
import com.bgasol.model.system.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.bgasol.common.constant.value.SystemConfigValues.ADMIN_USER_ID;

/**
 * 自定义权限验证接口扩展
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserApi userApi;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public UserEntity getUser(String userId, String loginType) {
        BaseVo<UserEntity> userEntityBaseVo = userApi.findById(userId);
        return userEntityBaseVo.getData();
    }

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        UserEntity user = this.getUser((String) loginId, loginType);
        Set<String> permissions = new HashSet<>();
        if (user.getId().equals(ADMIN_USER_ID)) {
            permissions.add("*");
        } else {
            for (RoleEntity role : user.getRoles()) {
                for (PermissionEntity permission : role.getPermissions()) {
                    if (permission.getMicroService().equals(contextPath)) {
                        permissions.add(permission.getCode());
                    }
                }
            }
        }
        return new ArrayList<>(permissions);
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        UserEntity user = this.getUser((String) loginId, loginType);
        Set<String> roles = new HashSet<>();
        if (user.getId().equals(ADMIN_USER_ID)) {
            roles.add("*");
        } else {
            for (RoleEntity role : user.getRoles()) {
                roles.add(role.getCode());
            }
        }
        return new ArrayList<>(roles);
    }
}