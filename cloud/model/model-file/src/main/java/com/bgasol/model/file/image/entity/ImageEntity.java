package com.bgasol.model.file.image.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.model.file.file.entity.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@TableName("file_t_image")
@Schema(description = "图片实体类")
@Entity
public class ImageEntity extends BaseEntity {
    @Schema(description = "是否删除")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    @TableLogic(value = "false", delval = "true")
    private Boolean deleted;

    @Schema(description = "图片名称")
    @TableField("name")
    private String name;

    @Schema(description = "图片宽度")
    @TableField("width")
    private Integer width;

    @Schema(description = "图片高度")
    @TableField("height")
    private Integer height;

    @Schema(description = "图片文件id")
    @TableField("file_id")
    @Transient
    private String fileId;

    @TableField(exist = false)
    @Schema(description = "图片文件")
    @OneToOne()
    @JoinColumn(name = "file_id")
    private FileEntity file;
}
