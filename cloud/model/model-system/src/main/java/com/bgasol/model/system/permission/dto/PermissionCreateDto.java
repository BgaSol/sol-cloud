package com.bgasol.model.system.permission.dto;


import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class PermissionCreateDto extends BaseCreateDto<PermissionEntity> {
    @Override
    public PermissionEntity toEntity() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
