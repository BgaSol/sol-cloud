package com.bgasol.model.system.department.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data

@Schema(description = "部门实体")
@TableName("t_department")
public class DepartmentEntity extends BaseTreeEntity<DepartmentEntity> {

    @Schema(description = "部门名")
    @TableField("name")
    private String name;

    @Schema(description = "部门编码")
    @TableField("code")
    private String code;

    @Schema(description = "部门域名")
    @TableField("domain")
    private String domain;

    @Schema(description = "部门地址")
    @TableField("address")
    private String address;

    @Schema(description = "部门电话")
    @TableField("phone")
    private String phone;

    @Schema(description = "部门备注HTML")
    @TableField("html")
    private String html;

    @Schema(description = "部门图标id 关联图片id")
    @TableField("icon_id")
    private String iconId;
}
