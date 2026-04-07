package com.bgasol.common.core.base.dto;

import com.bgasol.common.core.base.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "实体创建通用参数")
public abstract class BaseCreateDto<ENTITY extends BaseEntity> {

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "类型")
    private String type;

    @JsonIgnore
    @Schema(hidden = true)
    public abstract ENTITY toEntity();

    @JsonIgnore
    @Schema(hidden = true)
    public ENTITY toEntity(ENTITY entity) {
        entity.setSort(this.getSort());
        entity.setDescription(this.getDescription());
        entity.setType(this.getType());
        return entity;
    }
}
