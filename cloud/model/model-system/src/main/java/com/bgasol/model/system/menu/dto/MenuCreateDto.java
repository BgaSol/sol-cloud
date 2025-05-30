package com.bgasol.model.system.menu.dto;


import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class MenuCreateDto extends BaseCreateDto<MenuEntity> {
    @Override
    public MenuEntity toEntity() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
