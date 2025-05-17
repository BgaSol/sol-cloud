package com.bgasol.common.core.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "响应类型")
public enum ResponseType {

    SUCCESS("success"),
    WARNING("warning"),
    INFO("info"),
    ERROR("error");

    private final String value;
}
