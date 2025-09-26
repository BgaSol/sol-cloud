package com.bgasol.common.core.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@Entity
public class BaseEntity implements Serializable {
    @Id
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    @OrderBy(asc = true, sort = 33)
    private String id;

    @Schema(description = "类型")
    @TableField("type")
    private String type;

    @Schema(description = "排序")
    @TableField("sort")
    @OrderBy(asc = true, sort = 30)
    private Integer sort;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    @OrderBy(sort = 31)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @Schema(description = "更新时间")
    @OrderBy(sort = 32)
    private Date updateTime;

    @Schema(description = "描述")
    @TableField("description")
    private String description;
}
