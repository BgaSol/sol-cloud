package com.bgasol.model.system.role.mapstruct;

import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.dto.RoleUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface RoleMapstruct {

    RoleMapstruct ROLE_MAPSTRUCT_IMPL = Mappers.getMapper(RoleMapstruct.class);

    RoleEntity toEntity(RoleCreateDto dto);

    RoleEntity toEntity(RoleUpdateDto dto);
}
