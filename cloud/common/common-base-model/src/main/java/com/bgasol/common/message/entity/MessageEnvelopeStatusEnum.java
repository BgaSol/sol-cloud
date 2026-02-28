package com.bgasol.common.message.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "消息状态枚举")
public enum MessageEnvelopeStatusEnum {
    UNREAD("UNREAD"),
    READ("READ"),
    IGNORE("IGNORE");

    @EnumValue()
    private final String value;
}
