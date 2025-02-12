package com.bgasol.model.file.image.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "更新图片实体类")
public class ImageUpdateDto extends BaseUpdateDto<ImageEntity> {
    @Schema(description = "图片名称")
    private String name;

    @NotBlank(message = "图片不能为空")
    @Schema(description = "图片文件id")
    private String fileId;

    @Override
    public ImageEntity toEntity() {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(name);
        imageEntity.setFileId(fileId);
        return this.toEntity(imageEntity);
    }
}
