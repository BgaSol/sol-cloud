package com.bgasol.common.core.base.dto;

import com.bgasol.common.core.base.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public abstract class BaseUpdateDto<ENTITY extends BaseEntity> {

    @Schema(description = "主键")
    @NotBlank(message = "主键不能为空")
    private String id;

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

    @Schema(hidden = true)
    public ENTITY toEntity(ENTITY entity) {
        entity.setSort(this.getSort());
        entity.setDescription(this.getDescription());
        entity.setId(this.getId());
        return entity;
    }
}
