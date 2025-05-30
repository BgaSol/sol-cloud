package com.bgasol.model.system.department.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.department.mapstruct.DepartmentMapstruct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.mapstruct.factory.Mappers;

@Getter
@Setter
@SuperBuilder
@Schema(description = "更新部门Dto")
public class DepartmentUpdateDto extends BaseUpdateDto<DepartmentEntity> {
    @Schema(description = "部门名")
    private String name;

    @Schema(description = "部门编码")
    private String code;

    @Schema(description = "部门域名")
    private String domain;

    @Schema(description = "部门地址")
    private String address;

    @Schema(description = "部门电话")
    private String phone;

    @Schema(description = "部门备注HTML")
    private String html;

    @Schema(description = "部门图标id")
    private String iconId;

    @Schema(description = "父部门id")
    private String parentId;

    @Override
    public DepartmentEntity toEntity() {
        DepartmentMapstruct mapper = Mappers.getMapper(DepartmentMapstruct.class);
        return super.toEntity(mapper.toEntity(this));
    }
}
