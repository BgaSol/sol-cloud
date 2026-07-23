package com.bgasol.model.system.role.mapstruct;

import com.bgasol.common.core.base.mapstruct.BaseMapstructConfig;
import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.dto.RoleUpdateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.role.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = BaseMapstructConfig.class)
public interface RoleMapstruct {

    RoleMapstruct INSTANCE = Mappers.getMapper(RoleMapstruct.class);

    @Mapping(source = "code", target = "id")
    @Mapping(source = "permissionIds", target = "permissions")
    @Mapping(source = "menuIds", target = "menus")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    RoleEntity toEntity(RoleCreateDto dto);

    @Mapping(source = "permissionIds", target = "permissions")
    @Mapping(source = "menuIds", target = "menus")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    RoleEntity toEntity(RoleUpdateDto dto);

    default PermissionEntity toPermissionEntity(String id) {
        return id == null ? null : PermissionEntity.builder().id(id).build();
    }

    default MenuEntity toMenuEntity(String id) {
        return id == null ? null : MenuEntity.builder().id(id).build();
    }
}
