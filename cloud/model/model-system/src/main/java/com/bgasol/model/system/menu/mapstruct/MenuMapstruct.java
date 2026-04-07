package com.bgasol.model.system.menu.mapstruct;

import com.bgasol.model.system.menu.dto.MenuCreateDto;
import com.bgasol.model.system.menu.dto.MenuUpdateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface MenuMapstruct {
    MenuMapstruct MENU_MAPSTRUCT_IMPL = Mappers.getMapper(MenuMapstruct.class);

    MenuEntity toEntity(MenuCreateDto dto);

    MenuEntity toEntity(MenuUpdateDto dto);
}
