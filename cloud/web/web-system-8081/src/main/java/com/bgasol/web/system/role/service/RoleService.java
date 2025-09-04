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
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    @Transactional(readOnly = true)
    public void findOtherTable(RoleEntity roleEntity) {
        // 查询关联的角色
        List<String> permissionIds = this.roleMapper.findFromTable(
                "system_c_role_permission",
                "role_id",
                roleEntity.getId(),
                "permission_id");
        List<PermissionEntity> permissionEntities = new ArrayList<>();
        for (String id : permissionIds) {
            permissionEntities.add(permissionService.findById(id));
        }
        roleEntity.setPermissions(permissionEntities);

        // 查询关联的菜单
        List<String> roleIds = this.roleMapper.findFromTable(
                "system_c_role_menu",
                "role_id",
                roleEntity.getId(),
                "menu_id");
        List<MenuEntity> menuEntities = new ArrayList<>();
        for (String id : roleIds) {
            menuEntities.add(menuService.findById(id));
        }
        roleEntity.setMenus(menuEntities);
        super.findOtherTable(roleEntity);
    }

    @Override
    public Integer delete(String id) {
        this.roleMapper.deleteFromTable("system_c_role_permission", "role_id", id);
        this.roleMapper.deleteFromTable("system_c_role_menu", "role_id", id);
        this.roleMapper.deleteFromTable("system_c_user_role", "role_id", id);
        return super.delete(id);
    }
}
