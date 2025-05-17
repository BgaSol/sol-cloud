package com.bgasol.common.core.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "参数校验结果")
@Builder
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
