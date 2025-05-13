package com.bgasol.model.system.department.mapstruct;

import com.bgasol.model.system.department.dto.DepartmentCreateDto;
import com.bgasol.model.system.department.dto.DepartmentUpdateDto;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface DepartmentMapstruct {
    DepartmentMapstruct DEPARTMENT_MAPSTRUCT_IMPL = Mappers.getMapper(DepartmentMapstruct.class);

    DepartmentEntity toEntity(DepartmentCreateDto dto);

    DepartmentEntity toEntity(DepartmentUpdateDto dto);
}

