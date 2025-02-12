package com.bgasol.model.system.permission.dto;


import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;

public class PermissionCreateDto extends BaseCreateDto<PermissionEntity> {
    @Override
    public PermissionEntity toEntity() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
