package com.bgasol.model.system.permission.dto;


import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;

public class PermissionUpdateDto extends BaseUpdateDto<PermissionEntity> {
    @Override
    public PermissionEntity toEntity() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
