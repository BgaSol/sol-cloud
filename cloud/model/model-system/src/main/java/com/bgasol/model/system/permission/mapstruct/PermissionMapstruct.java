package com.bgasol.model.system.permission.mapstruct;

import com.bgasol.common.core.base.mapstruct.BaseMapstructConfig;
import com.bgasol.model.system.permission.dto.PermissionCreateDto;
import com.bgasol.model.system.permission.dto.PermissionUpdateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = BaseMapstructConfig.class)
public interface PermissionMapstruct {
    PermissionMapstruct INSTANCE = Mappers.getMapper(PermissionMapstruct.class);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    PermissionEntity toEntity(PermissionCreateDto dto);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    PermissionEntity toEntity(PermissionUpdateDto dto);
}
