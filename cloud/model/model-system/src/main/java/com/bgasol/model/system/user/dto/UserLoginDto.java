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
@Schema(description = "登录用户数据传输对象")
public class UserLoginDto {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    String password;

//    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String verificationCode;

//    @NotBlank(message = "验证码Key不能为空")
    @Schema(description = "验证码key")
    private String verificationCodeKey;
}
