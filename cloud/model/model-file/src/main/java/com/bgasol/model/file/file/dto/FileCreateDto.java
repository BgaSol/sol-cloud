package com.bgasol.model.file.file.dto;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@SuperBuilder
@Schema(description = "创建文件")
public class FileCreateDto extends BaseCreateDto<FileEntity> {
    @Schema(description = "要上传的文件块")
    @NotNull(message = "上传文件不能为空")
    private MultipartFile uploadFile;

    @Override
    public FileEntity toEntity() {
        throw new UnsupportedOperationException("方法未实现");
    }
}
