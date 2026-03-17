package com.bgasol.model.system.message.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "接收者类型枚举")
public enum MessageRecipientTypeEnum {
    DEPARTMENT("DEPARTMENT"),
    DEPARTMENT_ROLE("DEPARTMENT_ROLE"),
    USER("USER");

    @EnumValue()
    private final String value;
}
