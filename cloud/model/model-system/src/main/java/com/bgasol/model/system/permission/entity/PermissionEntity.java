package com.bgasol.model.system.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "权限实体")
@TableName("system_t_permission")
public class PermissionEntity extends BaseTreeEntity<PermissionEntity> {

    @Schema(description = "权限名")
    @TableField("name")
    private String name;

    @Schema(description = "权限编码")
    @TableField("code")
    private String code;

    @Schema(description = "权限路径")
    @TableField("path")
    private String path;

    @Schema(description = "微服务名")
    @TableField("micro_service")
    private String microService;
}
