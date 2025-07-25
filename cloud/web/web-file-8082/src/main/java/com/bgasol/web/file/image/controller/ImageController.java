package com.bgasol.web.file.image.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.model.file.image.dto.ImageCreateDto;
import com.bgasol.model.file.image.dto.ImagePageDto;
import com.bgasol.model.file.image.dto.ImageUpdateDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import com.bgasol.web.file.file.service.FileService;
import com.bgasol.web.file.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@Tag(name = "图片管理")
@RequestMapping("/image")
public class ImageController extends BaseController<
        ImageEntity,
        ImagePageDto,
        ImageCreateDto,
        ImageUpdateDto> {
    private final ImageService imageService;

    private final FileService fileService;

    @Override
    public ImageService commonBaseService() {
        return imageService;
    }

    @Override
    @PostMapping
    @Operation(summary = "新增图片", operationId = "saveImage")
    @SaCheckPermission("image:save")
    public BaseVo<ImageEntity> save(@RequestBody @Valid ImageCreateDto createDto) {
        return super.save(createDto);
    }

    @Override
    @PutMapping
    @Operation(summary = "更新图片", operationId = "updateImage")
    @SaCheckPermission("image:update")
    public BaseVo<ImageEntity> update(@RequestBody @Valid ImageUpdateDto updateDto) {
        return super.update(updateDto);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "查询图片", operationId = "findImageById")
    @SaCheckPermission("image:findById")
    public BaseVo<ImageEntity> findById(@PathVariable String id) {
        return super.findById(id);
    }

    @Override
    @PostMapping("/page")
    @Operation(summary = "分页查询图片", operationId = "findPageImage")
    @SaCheckPermission("image:findByPage")
    public BaseVo<PageVo<ImageEntity>> findByPage(@RequestBody @Valid ImagePageDto pageDto) {
        return super.findByPage(pageDto);
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除图片", operationId = "deleteImage")
    @SaCheckPermission("image:delete")
    public BaseVo<Integer[]> delete(@PathVariable String ids) {
        return super.delete(ids);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载图片", operationId = "downloadImage")
    @SaCheckPermission("file:download")
    public ResponseEntity<InputStreamResource> download(@PathVariable("id") String id) {
        ImageEntity imageEntity = imageService.findById(id);
        FileEntity file = imageEntity.getFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getSize()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(
                        fileService.getFileName(file),
                        StandardCharsets.UTF_8
                ))
                .contentType(MediaType.valueOf(file.getType()))
                .body(new InputStreamResource(imageService.imageStreamFindById(imageEntity.getId())));
    }
}
