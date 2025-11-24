package com.bgasol.model.file.file.dto;

import com.bgasol.model.file.file.entity.FileEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "创建文件")
public class FileCreateDto {
    @Schema(description = "要上传的文件块")
    private MultipartFile uploadFile;

    @Schema(description = "文件名称(包含文件后缀)")
    private String name;

    @Schema(description = "文件hash")
    private String hash;

    @Schema(description = "文件状态")
    private String status;

    @Schema(description = "文件后缀")
    private String suffix;

    @Schema(description = "文件来源")
    private String source;

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
