package com.bgasol.common.core.base.dto;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "排序类型：ASC-正序，DESC-倒序")
public enum PageOrderType {
    ASC("ASC"),
    DESC("DESC");

    @EnumValue()
    private final String value;
}
