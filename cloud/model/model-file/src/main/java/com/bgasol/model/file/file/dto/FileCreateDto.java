package com.bgasol.model.file.file.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.file.file.entity.FileEntity;
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
public class FileCreateDto extends BaseCreateDto<FileEntity> {
    @Schema(description = "要上传的文件块")
    private MultipartFile uploadFile;

    @TableField("name")
    private String name;

    @Schema(description = "文件状态")
    private String status;

    @Schema(description = "文件后缀")
    private String suffix;

    @Schema(description = "文件来源")
    private String source;

    @Override
    public FileEntity toEntity() {
        return super.toEntity(FILE_MAPSTRUCT_IMPL.toEntity(this));
    }
}
