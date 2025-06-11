package com.bgasol.model.file.file.dto;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "创建文件")
public class FileCreateDto extends BaseCreateDto<FileEntity> {
    @Schema(description = "要上传的文件块")
    @NotNull(message = "上传文件不能为空")
    private MultipartFile uploadFile;

    @Override
    public FileEntity toEntity() {
        FileEntity fileEntity = new FileEntity();
        return super.toEntity(fileEntity);
    }

    public FileCreateDto(String fileName, byte[] bytes, String mediaType) {
        super();
        this.uploadFile = new MultipartFile() {
            @Override
            public String getName() {
                return fileName;
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return mediaType;
            }

            @Override
            public boolean isEmpty() {
                return bytes.length == 0;
            }

            @Override
            public long getSize() {
                return bytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return bytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayResource(bytes).getInputStream();
            }

            @Override
            public void transferTo(File dest) throws IllegalStateException {
                throw new UnsupportedOperationException("Not implemented");
            }
        };
    }

}
