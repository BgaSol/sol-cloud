package com.bgasol.model.system.menu.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuType {
    MENU("MENU"),
    PAGE("PAGE"),
    BUTTON("BUTTON");

    @EnumValue()
    private final String value;
}
