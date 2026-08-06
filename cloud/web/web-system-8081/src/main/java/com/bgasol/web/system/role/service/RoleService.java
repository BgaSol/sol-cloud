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
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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
    public void findRequiredTable(List<RoleEntity> list) {
        if (ObjectUtils.isEmpty(list)) {
            return;
        }

        Set<String> roleIds = list.stream()
                .map(RoleEntity::getId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());

        if (ObjectUtils.isEmpty(roleIds)) {
            return;
        }

        Map<String, Set<String>> menuIdGroup = this.findFromTableBatch(
                RoleMenuTable.NAME, RoleMenuTable.ROLE_ID, roleIds, RoleMenuTable.MENU_ID
        );
        Map<String, Set<String>> permissionIdGroup = this.findFromTableBatch(
                RolePermissionTable.NAME, RolePermissionTable.ROLE_ID, roleIds, RolePermissionTable.PERMISSION_ID
        );

        list.forEach(roleEntity -> {
            roleEntity.setMenus(menuIdGroup
                    .getOrDefault(roleEntity.getId(), Set.of())
                    .stream()
                    .<MenuEntity>map(menuId -> MenuEntity.builder().id(menuId).build())
                    .toList());
            roleEntity.setPermissions(permissionIdGroup
                    .getOrDefault(roleEntity.getId(), Set.of())
                    .stream()
                    .<PermissionEntity>map(permissionId -> PermissionEntity.builder().id(permissionId).build())
                    .toList());
        });
    }

    @Transactional(readOnly = true)
    @Override
    public void findOtherTable(List<RoleEntity> list) {
        if (ObjectUtils.isEmpty(list)) {
            return;
        }

        Set<String> allMenuIds = list
                .stream()
                .map(RoleEntity::getMenus)
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(List::stream)
                .map(MenuEntity::getId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());

        Set<String> allPermissionIds = list
                .stream()
                .map(RoleEntity::getPermissions)
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(List::stream)
                .map(PermissionEntity::getId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());

        final Map<String, MenuEntity> menuMap = ObjectUtils.isNotEmpty(allMenuIds)
                ? menuService.findById(allMenuIds, true)
                  .stream()
                  .collect(Collectors.toMap(MenuEntity::getId, Function.identity()))
                : Map.of();

        final Map<String, PermissionEntity> permissionMap = ObjectUtils.isNotEmpty(allPermissionIds)
                ? permissionService.findById(allPermissionIds, true)
                  .stream()
                  .collect(Collectors.toMap(PermissionEntity::getId, Function.identity()))
                : Map.of();

        list.forEach(roleEntity -> {
            List<MenuEntity> menus = ObjectUtils.defaultIfNull(roleEntity.getMenus(), List.of());
            roleEntity.setMenus(menus
                    .stream()
                    .map(MenuEntity::getId)
                    .map(menuMap::get)
                    .filter(ObjectUtils::isNotEmpty)
                    .toList());
            List<PermissionEntity> permissions = ObjectUtils.defaultIfNull(roleEntity.getPermissions(), List.of());
            roleEntity.setPermissions(permissions
                    .stream()
                    .map(PermissionEntity::getId)
                    .map(permissionMap::get)
                    .filter(ObjectUtils::isNotEmpty)
                    .toList());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> findAll(boolean otherData) {
        if (!StpUtil.isLogin()) {
            // 未登录，一定是服务内部远程调用，直接返回全部
            return super.findAll(otherData);
        }
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

            // 其他业务校验...
            return true;
        };
    }
}
