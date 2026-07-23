package com.bgasol.model.file.file.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.model.file.file.mapstruct.FileMapstruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "更新文件")
public class FileUpdateDto extends BaseUpdateDto<FileEntity> {

    @Schema(description = "文件大小")
    private Long size;

    @Schema(description = "文件状态")
    private String status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "描述")
    private String description;

    @JsonIgnore
    @Schema(hidden = true)
    @Override
    public FileEntity toEntity() {
        return FileMapstruct.INSTANCE.toEntity(this);
    }

}
