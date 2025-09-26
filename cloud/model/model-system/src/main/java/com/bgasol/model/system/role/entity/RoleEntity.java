package com.bgasol.model.system.role.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "角色实体")
@TableName("system_t_role")
@Entity
public class RoleEntity extends BaseEntity {
    @Schema(description = "角色名")
    @TableField("name")
    private String name;

    @Schema(description = "角色编码")
    @TableField("code")
    private String code;

    @Schema(description = "角色状态")
    @TableField("status")
    private Integer status;

    @Schema(description = "角色权限")
    @TableField(exist = false)
    @ManyToMany(targetEntity = PermissionEntity.class)
    @JoinTable(
            name = "system_c_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<PermissionEntity> permissions;

    @Schema(description = "角色菜单")
    @TableField(exist = false)
    @ManyToMany(targetEntity = MenuEntity.class)
    @JoinTable(
            name = "system_c_role_menu",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    private List<MenuEntity> menus;
}
