package com.bgasol.model.file.file.dto;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import static com.bgasol.model.file.file.mapstruct.FileMapstruct.FILE_MAPSTRUCT_IMPL;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "更新文件")
public class FileUpdateDto extends BaseCreateDto<FileEntity> {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "要上传的文件块")
    private MultipartFile uploadFile;

    @Schema(description = "文件名称")
    private String name;

    @Schema(description = "文件状态")
    private String status;

    @Schema(description = "文件后缀")
    private String suffix;

    @Schema(description = "文件来源")
    private String source;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public FileEntity toEntity() {
        return super.toEntity(FILE_MAPSTRUCT_IMPL.toEntity(this));
    }

}
