package com.bgasol.model.system.permission.mapstruct;

import com.bgasol.model.system.permission.dto.PermissionCreateDto;
import com.bgasol.model.system.permission.dto.PermissionUpdateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface PermissionMapstruct {
    PermissionMapstruct PERMISSION_MAPSTRUCT_IMPL = Mappers.getMapper(PermissionMapstruct.class);

    PermissionEntity toEntity(PermissionCreateDto dto);

    PermissionEntity toEntity(PermissionUpdateDto dto);
}
