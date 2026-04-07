package com.bgasol.model.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "重置用户密码数据传输对象")
public class UserPasswordResetDto {
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码")
    private String password;

    @NotBlank(message = "id不能为空")
    @Schema(description = "用户id")
    private String id;
}
