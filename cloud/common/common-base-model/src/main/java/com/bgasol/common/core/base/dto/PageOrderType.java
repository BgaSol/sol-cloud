package com.bgasol.common.core.base.dto;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PageOrderType {
    ASC("ASC"),
    DESC("DESC");

    @EnumValue()
    private final String value;

}
