package com.bgasol.model.file.file.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.bgasol.model.file.file.mapstruct.FileMapstruct.FILE_MAPSTRUCT_IMPL;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "更新文件")
public class FileUpdateDto extends BaseUpdateDto<FileEntity> {

    @Schema(description = "文件状态")
    private String status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "描述")
    private String description;

    @JsonIgnore
    @Schema(hidden = true)
    public FileEntity toEntity() {
        return FILE_MAPSTRUCT_IMPL.toEntity(this);
    }

}