package com.bgasol.model.file.file.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "更新文件")
public class FileUpdateDto extends BaseUpdateDto<FileEntity> {
    @Override
    public FileEntity toEntity() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
