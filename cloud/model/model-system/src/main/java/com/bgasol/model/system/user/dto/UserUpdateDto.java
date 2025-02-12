package com.bgasol.model.system.user.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "更新用户数据传输对象")
public class UserUpdateDto extends BaseUpdateDto<UserEntity> {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

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
    public UserEntity toEntity() {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setLocked(locked);
        user.setDepartmentId(departmentId);
        if (ObjectUtils.isNotEmpty(departmentId)) {
            user.setDepartmentId(departmentId);
        } else {
            user.setDepartmentId("default");
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
