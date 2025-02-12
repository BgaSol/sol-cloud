package com.bgasol.model.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "更新用户密码数据传输对象")
@Data
public class UserPasswordUpdateDto {
    @Schema(description = "旧密码")
    @NotBlank(message = "旧密码不能为空")
    String oldPassword;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    String newPassword;
}
