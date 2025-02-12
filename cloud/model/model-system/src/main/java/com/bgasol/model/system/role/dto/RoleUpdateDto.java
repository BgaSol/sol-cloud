package com.bgasol.model.system.role.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.role.entity.RoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "更新角色实体")
public class RoleUpdateDto extends BaseUpdateDto<RoleEntity> {
    @NotBlank(message = "角色名不能为空")
    @Schema(description = "角色名")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "角色权限ID列表")
    private List<String> permissionIds;

    @Schema(description = "角色菜单ID列表")
    private List<String> menuIds;

    @Override
    public RoleEntity toEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        roleEntity.setCode(code);
        if (permissionIds != null) {
            Stream<PermissionEntity> permissionEntityStream = permissionIds.stream().map((id) -> {
                PermissionEntity permissionEntity = new PermissionEntity();
                permissionEntity.setId(id);
                return permissionEntity;
            });
            List<PermissionEntity> collect = permissionEntityStream.collect(Collectors.toList());
            roleEntity.setPermissions(collect);
        }
        if (menuIds != null) {
            Stream<MenuEntity> menuEntityStream = menuIds.stream().map((id) -> {
                MenuEntity menuEntity = new MenuEntity();
                menuEntity.setId(id);
                return menuEntity;
            });
            List<MenuEntity> collect = menuEntityStream.collect(Collectors.toList());
            roleEntity.setMenus(collect);
        }
        return this.toEntity(roleEntity);
    }
}
