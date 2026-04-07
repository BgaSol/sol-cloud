package com.bgasol.web.system.role.service;

import cn.dev33.satoken.stp.StpUtil;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.role.entity.RoleMenuTable;
import com.bgasol.model.system.role.entity.RolePermissionTable;
import com.bgasol.web.system.menu.service.MenuService;
import com.bgasol.web.system.permission.service.PermissionService;
import com.bgasol.web.system.role.mapper.RoleMapper;
import com.bgasol.web.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bgasol.common.constant.value.SystemConfigValues.ADMIN_ROLE_ID;

@Service
@RequiredArgsConstructor
public class RoleService extends BaseService<RoleEntity, BasePageDto<RoleEntity>> {
    private final RoleMapper roleMapper;

    private final MenuService menuService;
    private final PermissionService permissionService;

    @Override
    public RoleMapper commonBaseMapper() {
        return roleMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public void findOtherTable(List<RoleEntity> list) {
        List<String> roleIds = list.stream()
                .map(RoleEntity::getId)
                .filter(ObjectUtils::isNotEmpty)
                .toList();

        Map<String, List<String>> menuIdGroup = this.findFromTableBatch(
                RoleMenuTable.NAME, RoleMenuTable.ROLE_ID, roleIds, RoleMenuTable.MENU_ID
        );
        Map<String, List<String>> permissionIdGroup = this.findFromTableBatch(
                RolePermissionTable.NAME, RolePermissionTable.ROLE_ID, roleIds, RolePermissionTable.PERMISSION_ID
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
                .findById(allMenuIds, true)
                .stream()
                .collect(Collectors.toMap(MenuEntity::getId, Function.identity()));
        Map<String, PermissionEntity> permissionMap = permissionService
                .findById(allPermissionIds, true)
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

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> findAll(boolean otherData) {
        List<String> roleList = StpUtil.getRoleList();
        if (roleList.contains(ADMIN_ROLE_ID)) {
            return super.findAll(otherData);
        }
        return this.findById(new HashSet<>(roleList), otherData);
    }

    @Override
    public BiPredicate<RoleEntity, List<String>> importValidator() {
        return (entity, errors) -> {

            if (entity.getName() == null) {
                errors.add("角色名称不能为空");
                return false;
            }

            if (entity.getCode() == null) {
                errors.add("角色编码不能为空");
                return false;
            }

            // 其他业务校验...
            return true;
        };
    }
}
