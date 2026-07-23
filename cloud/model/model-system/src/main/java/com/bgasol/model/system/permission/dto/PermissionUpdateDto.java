package com.bgasol.model.system.permission.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.permission.mapstruct.PermissionMapstruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "更新权限Dto")
public class PermissionUpdateDto extends BaseUpdateDto<PermissionEntity> {

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
        return PermissionMapstruct.INSTANCE.toEntity(this);
    }
}
