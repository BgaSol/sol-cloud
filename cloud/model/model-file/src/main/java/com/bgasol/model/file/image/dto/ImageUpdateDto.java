package com.bgasol.model.file.image.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import com.bgasol.model.file.image.mapstruct.ImageMapstruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "更新图片实体类")
public class ImageUpdateDto extends BaseUpdateDto<ImageEntity> {
    @Schema(description = "图片名称")
    private String name;

    @Schema(description = "图片文件id")
    private String fileId;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public ImageEntity toEntity() {
        ImageEntity imageEntity = ImageMapstruct.IMAGE_MAPSTRUCT.toEntity(this);
        return this.toEntity(imageEntity);
    }
}
