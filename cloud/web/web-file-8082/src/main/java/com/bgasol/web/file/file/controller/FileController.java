package com.bgasol.web.file.file.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
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
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "文件管理")
@RequestMapping("/file")
@Slf4j
public class FileController extends BaseController<
        FileEntity,
        FilePageDto,
        FileCreateDto,
        FileUpdateDto> {
    private final FileService fileService;

    private final OssService ossService;
    private final MinioClient minioClient;

    @Override
    public FileService commonBaseService() {
        return fileService;
    }

    @PostMapping("/insert")
    @Operation(summary = "保存|上传文件", operationId = "insertFileController")
    @SaCheckPermission(value = "FileController:insert", orRole = "admin")
    public BaseVo<FileEntity> insert(@ModelAttribute FileCreateDto fileCreateDto) {
        FileEntity save = fileService.insert(fileCreateDto.getUploadFile(), fileCreateDto.toEntity());
        return BaseVo.success(save, "文件上传成功");
    }

    @Override
    @PostMapping("/apply")
    @SaCheckPermission(value = "FileController:apply", orRole = "admin")
    @Operation(summary = "更新文件", operationId = "applyFileController")
    public BaseVo<FileEntity> apply(@RequestBody FileUpdateDto updateDto) {
        return super.apply(updateDto);
    }

    @Override
    @PostMapping("/delete")
    @SaCheckPermission(value = "FileController:delete", orRole = "admin")
    @Operation(summary = "删除文件", operationId = "deleteFileController")
    public BaseVo<Integer> delete(@RequestBody Set<String> ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}/{otherData}")
    @SaCheckPermission(value = "FileController:findById", orRole = "admin")
    @Operation(summary = "根据ID查询文件", operationId = "findByIdFileController")
    public BaseVo<FileEntity> findById(@PathVariable String id, @PathVariable Boolean otherData) {
        return super.findById(id, otherData);
    }

    @Override
    @PostMapping("/get/{otherData}")
    @SaCheckPermission(value = "FileController:findByIds", orRole = "admin")
    @Operation(summary = "根据ID批量查询文件", operationId = "findByIdsFileController")
    public BaseVo<List<FileEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable Boolean otherData) {
        return super.findByIds(ids, otherData);
    }

    @Override
    @PostMapping("/page/{otherData}")
    @SaCheckPermission(value = "FileController:findByPage", orRole = "admin")
    @Operation(summary = "分页查询文件", operationId = "findByPageFileController")
    public BaseVo<PageVo<FileEntity>> findByPage(@RequestBody FilePageDto pageDto, @PathVariable Boolean otherData) {
        return super.findByPage(pageDto, otherData);
    }

    @SneakyThrows
    @GetMapping("/download/{id}")
    @Operation(summary = "下载文件", operationId = "downloadFile")
    @SaCheckPermission(value = "file:download", orRole = "admin")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) {
        FileEntity file = fileService.findById(id, false);
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
