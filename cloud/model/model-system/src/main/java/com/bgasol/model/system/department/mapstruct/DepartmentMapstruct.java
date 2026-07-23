package com.bgasol.model.system.department.mapstruct;

import com.bgasol.common.core.base.mapstruct.BaseMapstructConfig;
import com.bgasol.model.system.department.dto.DepartmentCreateDto;
import com.bgasol.model.system.department.dto.DepartmentUpdateDto;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = BaseMapstructConfig.class)
public interface DepartmentMapstruct {
    DepartmentMapstruct INSTANCE = Mappers.getMapper(DepartmentMapstruct.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    DepartmentEntity toEntity(DepartmentCreateDto dto);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    DepartmentEntity toEntity(DepartmentUpdateDto dto);
}
