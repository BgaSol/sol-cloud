package com.bgasol.model.system.menu.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "菜单类型：MENU-菜单，PAGE-页面，BUTTON-按钮")
public enum MenuType {
    MENU("MENU"),
    PAGE("PAGE"),
    BUTTON("BUTTON");

    @EnumValue()
    private final String value;
}