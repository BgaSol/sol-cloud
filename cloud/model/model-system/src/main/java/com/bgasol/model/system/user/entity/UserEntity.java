package com.bgasol.model.system.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "用户实体类")
@TableName("system_t_user")
@Entity
public class UserEntity extends BaseEntity {

    @Schema(description = "用户名")
    @TableField("username")
    private String username;

    @Schema(description = "密码")
    @TableField("password")
    @JsonIgnore
    private String password;

    @Schema(description = "昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "状态")
    @TableField("status")
    private String status;

    @Schema(description = "头像id")
    @TableField("avatar_id")
    private String avatarId;

    @Schema(description = "账户锁定")
    @TableField("locked")
    private Boolean locked;

    @Schema(description = "角色")
    @TableField(exist = false)
    @ManyToMany(targetEntity = RoleEntity.class)
    @JoinTable(
            name = "system_c_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> roles;

    @Schema(description = "部门id")
    @TableField("department_id")
    @Transient
    private String departmentId;

    @Schema(description = "部门")
    @TableField(exist = false)
    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

}
