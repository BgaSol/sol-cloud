package com.bgasol.model.file.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@TableName("file_t_file")
@Schema(description = "文件实体类")
public class FileEntity extends BaseEntity {
    @TableField("name")
    @Schema(description = "文件名称")
    private String name;

    @Schema(description = "文件地址")
    @TableField("url")
    private String url;

    @Schema(description = "文件大小")
    @TableField("size")
    private Long size;

    @Schema(description = "文件HASH")
    @TableField("hash")
    private String hash;

    @Schema(description = "文件状态")
    @TableField("status")
    private String status;

    @Schema(description = "文件后缀")
    @TableField("suffix")
    private String suffix;

    @Schema(description = "文件来源")
    @TableField("source")
    private String source;

    @Schema(description = "文件所在桶")
    @TableField("bucket")
    private String bucket;
}
