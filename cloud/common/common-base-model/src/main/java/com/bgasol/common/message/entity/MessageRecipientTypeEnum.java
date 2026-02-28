package com.bgasol.common.message.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "接收者类型枚举")
public enum MessageRecipientTypeEnum {
    USER("USER"),
    ROLE("ROLE"),
    DEPARTMENT("DEPARTMENT");

    @EnumValue()
    private final String value;
}
