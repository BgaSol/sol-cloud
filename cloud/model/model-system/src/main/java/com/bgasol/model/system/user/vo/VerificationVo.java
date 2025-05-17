package com.bgasol.model.system.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "验证码结果")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationVo {

    @Schema(description = "验证码图片的base64编码")
    private String verificationCode;

    @Schema(description = "验证码id")
    private String verificationId;
}
