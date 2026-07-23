package com.bgasol.model.system.user.mapstruct;

import com.bgasol.common.core.base.mapstruct.BaseMapstructConfig;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.user.dto.UserCreateDto;
import com.bgasol.model.system.user.dto.UserUpdateDto;
import com.bgasol.model.system.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import static com.bgasol.common.constant.value.SystemConfigValues.DEFAULT_DEPARTMENT_ID;

@Mapper(config = BaseMapstructConfig.class)
public interface UserMapstruct {

    UserMapstruct INSTANCE = Mappers.getMapper(UserMapstruct.class);

    @Mapping(source = "roleIds", target = "roles")
    @Mapping(source = "departmentId", target = "departmentId", qualifiedByName = "departmentIdOrDefault")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "avatarId", ignore = true)
    @Mapping(target = "department", ignore = true)
    UserEntity toEntity(UserCreateDto dto);

    @Mapping(source = "roleIds", target = "roles")
    @Mapping(source = "departmentId", target = "departmentId", qualifiedByName = "departmentIdOrDefault")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "avatarId", ignore = true)
    @Mapping(target = "department", ignore = true)
    UserEntity toEntity(UserUpdateDto dto);

    default RoleEntity toRoleEntity(String id) {
        return id == null ? null : RoleEntity.builder().id(id).build();
    }

    @Named("departmentIdOrDefault")
    default String departmentIdOrDefault(String departmentId) {
        return departmentId == null || departmentId.isEmpty() ? DEFAULT_DEPARTMENT_ID : departmentId;
    }
}
