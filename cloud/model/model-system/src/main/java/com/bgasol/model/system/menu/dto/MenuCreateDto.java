package com.bgasol.model.system.menu.dto;


import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class MenuCreateDto extends BaseCreateDto<MenuEntity> {
    @Override
    public MenuEntity toEntity() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
