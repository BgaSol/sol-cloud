package com.bgasol.model.system.menu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data

@Schema(description = "菜单实体")
@TableName("t_menu")

public class MenuEntity extends BaseTreeEntity<MenuEntity> {

    @Schema(description = "菜单名")
    @TableField("name")
    private String name;

    @Schema(description = "菜单状态")
    @TableField("status")
    private Integer status;

    @Schema(description = "菜单类型")
    @TableField("menu_type")
    @Enumerated(EnumType.STRING)
    private MenuType menuType;

    @Schema(description = "菜单路由地址")
    @TableField("route_path")
    private String path;

    @Schema(description = "菜单图标")
    @TableField("icon")
    private String icon;

    @Schema(description = "菜单路由名")
    @TableField("route_name")
    private String routeName;

    @Schema(description = "按钮代码")
    @TableField("button_code")
    private String buttonCode;

    @Schema(description = "是否是外链")
    @TableField("is_external")
    private Boolean isExternal;

    @Schema(description = "外链地址")
    @TableField("external_url")
    private String externalUrl;

    @Schema(description = "是否外链新窗口打开")
    @TableField("is_external_open")
    private Boolean isExternalOpen;

    @Schema(description = "是否停用（置灰）")
    @TableField("is_disabled")
    private Boolean isDisabled;

    @Schema(description = "是否隐藏（不显示）")
    @TableField("is_hidden")
    private Boolean isHidden;

    @Schema(description = "菜单组")
    @TableField("menu_group")
    private String menuGroup;
}
