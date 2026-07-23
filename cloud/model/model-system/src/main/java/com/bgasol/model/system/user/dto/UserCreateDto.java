package com.bgasol.model.system.user.dto;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.model.system.user.mapstruct.UserMapstruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
        return UserMapstruct.INSTANCE.toEntity(this);
    }
}
