package com.bgasol.model.system.department.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "部门实体")
@TableName("system_t_department")
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

    /**
     * 递归获取当前部门下的所有子部门
     */
    @JsonIgnore
    @Schema(hidden = true)
    public List<DepartmentEntity> getAllChildren() {
        List<DepartmentEntity> allChildren = new java.util.ArrayList<>();
        allChildren.add(this);
        if (this.getChildren() != null && !this.getChildren().isEmpty()) {
            for (DepartmentEntity child : this.getChildren()) {
                allChildren.addAll(child.getAllChildren());
            }
        }
        return allChildren;
    }

}
