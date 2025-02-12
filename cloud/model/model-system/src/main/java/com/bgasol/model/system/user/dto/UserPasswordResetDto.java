package com.bgasol.model.system.user.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.system.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "重置用户密码数据传输对象")
public class UserPasswordResetDto extends BaseUpdateDto<UserEntity> {
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码")
    private String password;

    @Override
    public UserEntity toEntity() {
        UserEntity user = new UserEntity();
        user.setId(this.getId());
        user.setPassword(password);
        return user;
    }
}
