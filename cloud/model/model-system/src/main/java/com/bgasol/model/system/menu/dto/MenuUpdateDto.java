package com.bgasol.model.system.menu.dto;


import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;

public class MenuUpdateDto extends BaseUpdateDto<MenuEntity> {
    @Override
    public MenuEntity toEntity() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
