package com.bgasol.model.system.menu.dto;


import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.menu.entity.MenuType;
import com.bgasol.model.system.menu.mapstruct.MenuMapstruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.bgasol.model.system.menu.mapstruct.MenuMapstruct.MENU_MAPSTRUCT_IMPL;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "新增菜单Dto")
public class MenuCreateDto extends BaseCreateDto<MenuEntity> {
    @Schema(description = "父菜单id")
    private String parentId;

    @Schema(description = "菜单名")
    private String name;

    @Schema(description = "菜单状态")
    private Integer status;

    @Schema(description = "菜单类型")
    private MenuType menuType;

    @Schema(description = "菜单路由地址")
    private String path;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "菜单路由名")
    private String routeName;

    @Schema(description = "按钮代码")
    private String buttonCode;

    @Schema(description = "是否是外链")
    private Boolean isExternal;

    @Schema(description = "外链地址")
    private String externalUrl;

    @Schema(description = "是否外链新窗口打开")
    private Boolean isExternalOpen;

    @Schema(description = "是否停用（置灰）")
    private Boolean isDisabled;

    @Schema(description = "是否隐藏（不显示）")
    private Boolean isHidden;

    @Schema(description = "菜单组")
    private String menuGroup;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public MenuEntity toEntity() {
        MenuEntity entity = MENU_MAPSTRUCT_IMPL.toEntity(this);
        return super.toEntity(entity);
    }
}
