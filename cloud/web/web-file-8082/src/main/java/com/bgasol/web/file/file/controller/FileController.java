package com.bgasol.web.file.file.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.file.file.dto.FileCreateDto;
import com.bgasol.model.file.file.dto.FilePageDto;
import com.bgasol.model.file.file.dto.FileUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.plugin.minio.service.OssService;
import com.bgasol.web.file.file.service.FileService;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "文件管理")
@RequestMapping("/file")
@Slf4j
public class FileController extends BaseController<
        FileEntity,
        FilePageDto,
        BaseCreateDto<FileEntity>,
        FileUpdateDto> {
    private final FileService fileService;

    private final OssService ossService;
    private final MinioClient minioClient;

    @Override
    public FileService commonBaseService() {
        return fileService;
    }

    @Override
    @PostMapping("/page")
    @Operation(summary = "分页查询文件", operationId = "findPageFile")
    @SaCheckPermission(value = "file:findByPage", orRole = "admin")
    public BaseVo<PageVo<FileEntity>> findByPage(@RequestBody @Valid FilePageDto pageDto) {
        return super.findByPage(pageDto);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "保存|上传文件", operationId = "saveFile")
    @SaCheckPermission(value = "file:save", orRole = "admin")
    public BaseVo<FileEntity> save(FileCreateDto fileCreateDto) {
        FileEntity save = fileService.save(fileCreateDto.getUploadFile(), fileCreateDto.toEntity());
        return BaseVo.success(save, "文件上传成功");
    }

    @PutMapping
    @Operation(summary = "更新文件状态", operationId = "updateFile")
    @SaCheckPermission(value = "file:update", orRole = "admin")
    public BaseVo<FileEntity> update(@RequestBody FileUpdateDto fileUpdateDto) {
        return super.update(fileUpdateDto);
    }

    @Override
    @Operation(summary = "删除文件", operationId = "deleteFile")
    @DeleteMapping("/{ids}")
    @SaCheckPermission(value = "file:delete", orRole = "admin")
    public BaseVo<Integer[]> delete(@PathVariable("ids") String ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询文件", operationId = "findFileById")
    @SaCheckPermission(value = "file:findById", orRole = "admin")
    public BaseVo<FileEntity> findById(@PathVariable("id") String id) {
        return super.findById(id);
    }

    @Override
    @GetMapping("/ids/{ids}")
    @Operation(summary = "根据id批量查询图片", operationId = "findFileByIds")
    @SaCheckPermission(value = "file:findByIds", orRole = "admin")
    public BaseVo<List<FileEntity>> findByIds(@PathVariable String ids) {
        return super.findByIds(ids);
    }

    @SneakyThrows
    @GetMapping("/download/{id}")
    @Operation(summary = "下载文件", operationId = "downloadFile")
    @SaCheckPermission(value = "file:download", orRole = "admin")
    public ResponseEntity<InputStreamResource> download(@PathVariable("id") String id) {
        FileEntity file = fileService.findById(id);
        String bucket = file.getBucket();
        String objectName = ossService.buildObjectPath(file);

        StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(stat.size()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(
                        fileService.getFileName(file),
                        StandardCharsets.UTF_8
                ))
                .contentType(MediaType.valueOf(file.getType()))
                .body(new InputStreamResource(ossService.readFileStream(file)));
    }
}
