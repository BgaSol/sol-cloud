package com.bgasol.model.system.department.dto;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Schema(description = "新增部门Dto")
@Data
public class DepartmentCreateDto extends BaseCreateDto<DepartmentEntity> {

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
        DepartmentEntity departmentEntity = new DepartmentEntity();
        departmentEntity.setName(name);
        departmentEntity.setCode(code);
        departmentEntity.setDomain(domain);
        departmentEntity.setAddress(address);
        departmentEntity.setPhone(phone);
        departmentEntity.setHtml(html);
        departmentEntity.setIconId(iconId);
        departmentEntity.setParentId(parentId);
        return this.toEntity(departmentEntity);
    }
}
