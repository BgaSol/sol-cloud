package com.bgasol.web.file.file.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.file.file.dto.FileCreateDto;
import com.bgasol.model.file.file.dto.FilePageDto;
import com.bgasol.model.file.file.dto.FileUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.web.file.file.service.FileService;
import com.bgasol.web.file.file.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@Tag(name = "文件管理")
@RequestMapping("/file")
@Slf4j
public class FileController extends BaseController<
        FileEntity,
        FilePageDto,
        FileCreateDto,
        BaseUpdateDto<FileEntity>> {
    private final FileService fileService;

    private final OssService ossService;

    @Override
    public FileService commonBaseService() {
        return fileService;
    }

    @Override
    @PostMapping("/page")
    @Operation(summary = "分页查询文件", operationId = "findPageFile")
    @SaCheckPermission("file:findByPage")
    public BaseVo<PageVo<FileEntity>> findByPage(@RequestBody @Valid FilePageDto pageDto) {
        return super.findByPage(pageDto);
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "保存|上传文件", operationId = "saveFile")
    @SaCheckPermission("file:save")
    public BaseVo<FileEntity> save(FileCreateDto fileCreateDto) {
        FileEntity save = fileService.save(fileCreateDto.getUploadFile(), fileCreateDto.toEntity());
        return BaseVo.success(save, "文件上传成功");
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "更新|上传文件", operationId = "updateFile")
    @SaCheckPermission("file:update")
    public BaseVo<FileEntity> update(FileUpdateDto fileUpdateDto) {
        FileEntity update = fileService.update(fileUpdateDto.getUploadFile(), fileUpdateDto.toEntity());
        return BaseVo.success(update, "文件更新成功");
    }

    @Override
    @Operation(summary = "删除文件", operationId = "deleteFile")
    @DeleteMapping("/{ids}")
    @SaCheckPermission("file:delete")
    public BaseVo<Integer[]> delete(@PathVariable("ids") String ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询文件", operationId = "findFileById")
    @SaCheckPermission("file:findById")
    public BaseVo<FileEntity> findById(@PathVariable("id") String id) {
        return super.findById(id);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载文件", operationId = "downloadFile")
    @SaCheckPermission("file:download")
    public ResponseEntity<InputStreamResource> download(@PathVariable("id") String id) {
        FileEntity file = fileService.findById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getSize()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(
                        fileService.getFileName(file),
                        StandardCharsets.UTF_8
                ))
                .contentType(MediaType.valueOf(file.getType()))
                .body(new InputStreamResource(ossService.readFileStream(file.getBucket(), file.getId(), file.getName())));
    }

    @GetMapping("/stream/{id}")
    @Operation(summary = "在线播放文件", operationId = "streamFile")
    @SaCheckPermission("file:stream")
    public ResponseEntity<InputStreamResource> stream(@PathVariable("id") String id) {
        FileEntity file = fileService.findById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getType())) // video/mp4
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getSize()))
                .body(new InputStreamResource(
                        ossService.readFileStream(file.getBucket(), file.getId(), file.getName())
                ));
    }
}
