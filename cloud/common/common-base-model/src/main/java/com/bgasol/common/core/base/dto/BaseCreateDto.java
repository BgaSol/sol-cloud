package com.bgasol.common.core.base.dto;

import com.bgasol.common.core.base.entity.BaseEntity;
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

    /**
     * CreateDto转Entity
     *
     * @return ENTITY
     */
    @Schema(hidden = true)
    public abstract ENTITY toEntity();

    /**
     * CreateDto转Entity
     *
     * @param entity 实体
     * @return ENTITY
     */
    @Schema(hidden = true)
    public ENTITY toEntity(ENTITY entity) {
        entity.setSort(this.getSort());
        entity.setDescription(this.getDescription());
        return entity;
    }
}
