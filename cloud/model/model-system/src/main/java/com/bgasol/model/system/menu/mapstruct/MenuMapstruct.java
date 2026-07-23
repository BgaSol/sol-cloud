package com.bgasol.model.system.menu.mapstruct;

import com.bgasol.common.core.base.mapstruct.BaseMapstructConfig;
import com.bgasol.model.system.menu.dto.MenuCreateDto;
import com.bgasol.model.system.menu.dto.MenuUpdateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = BaseMapstructConfig.class)
public interface MenuMapstruct {
    MenuMapstruct INSTANCE = Mappers.getMapper(MenuMapstruct.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    MenuEntity toEntity(MenuCreateDto dto);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    MenuEntity toEntity(MenuUpdateDto dto);
}
