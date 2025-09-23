package com.bgasol.common.core.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/// 参数校验结果
@Schema(description = "参数校验结果")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class VerificationResult {
    /// 校验结果
    @Schema(description = "校验结果")
    private boolean result;

    /// 校验字段
    @Schema(description = "校验字段")
    private String field;

    /// 校验消息
    @Schema(description = "校验消息")
    private String message;
}
