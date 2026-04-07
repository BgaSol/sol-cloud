package com.bgasol.model.system.permission.dto;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.bgasol.model.system.permission.mapstruct.PermissionMapstruct.PERMISSION_MAPSTRUCT_IMPL;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "新增权限Dto")
public class PermissionCreateDto extends BaseCreateDto<PermissionEntity> {
    @Schema(description = "权限id")
    private String id;

    @Schema(description = "父权限id")
    private String parentId;

    @Schema(description = "权限名")
    private String name;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "权限路径")
    private String path;

    @Schema(description = "微服务名")
    private String microService;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public PermissionEntity toEntity() {
        PermissionEntity entity = PERMISSION_MAPSTRUCT_IMPL.toEntity(this);
        return super.toEntity(entity);
    }
}
