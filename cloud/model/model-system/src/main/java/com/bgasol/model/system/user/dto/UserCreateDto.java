package com.bgasol.model.system.user.dto;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bgasol.common.constant.value.SystemConfigValues.DEFAULT_DEPARTMENT_ID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "创建用户数据传输对象")
public class UserCreateDto extends BaseCreateDto<UserEntity> {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @NotNull
    @Schema(description = "账户锁定")
    private Boolean locked;

    @Schema(description = "角色id列表")
    private List<String> roleIds;

    @Schema(description = "部门id")
    private String departmentId;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public UserEntity toEntity() {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setLocked(locked);
        if (ObjectUtils.isNotEmpty(departmentId)) {
            user.setDepartmentId(departmentId);
        } else {
            user.setDepartmentId(DEFAULT_DEPARTMENT_ID);
        }
        if (roleIds != null) {
            Stream<RoleEntity> roleEntityStream = roleIds.stream().map((id) -> {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setId(id);
                return roleEntity;
            });
            List<RoleEntity> collect = roleEntityStream.collect(Collectors.toList());
            user.setRoles(collect);
        }
        return this.toEntity(user);
    }
}
