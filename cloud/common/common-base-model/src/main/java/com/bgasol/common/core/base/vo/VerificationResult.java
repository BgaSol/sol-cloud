package com.bgasol.common.core.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Schema(description = "参数校验结果")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResult {
    @Schema(description = "校验结果")
    private boolean result;

    @Schema(description = "校验字段")
    private String field;

    @Schema(description = "校验消息")
    private String message;
}
