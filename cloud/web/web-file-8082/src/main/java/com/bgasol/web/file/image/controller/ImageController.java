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
import com.bgasol.plugin.minio.service.OssService;
import com.bgasol.web.file.file.service.FileService;
import com.bgasol.web.file.image.service.ImageService;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
@Tag(name = "图片管理")
@RequestMapping("/image")
public class ImageController extends BaseController<
        ImageEntity,
        ImagePageDto,
        ImageCreateDto,
        ImageUpdateDto> {
    private final ImageService imageService;

    private final FileService fileService;
    private final OssService ossService;
    private final MinioClient minioClient;

    @Override
    public ImageService commonBaseService() {
        return imageService;
    }

    @Override
    @PostMapping("/insert")
    @Operation(summary = "创建图片", operationId = "insertImageController")
    @SaCheckPermission(value = "ImageController:insert")
    public BaseVo<ImageEntity> insert(@RequestBody ImageCreateDto createDto) {
        return super.insert(createDto);
    }

    @Override
    @PostMapping("/apply")
    @SaCheckPermission(value = "ImageController:apply")
    @Operation(summary = "更新图片", operationId = "applyImageController")
    public BaseVo<ImageEntity> apply(@RequestBody ImageUpdateDto updateDto) {
        return super.apply(updateDto);
    }

    @Override
    @PostMapping("/delete")
    @SaCheckPermission(value = "ImageController:delete")
    @Operation(summary = "删除图片", operationId = "deleteImageController")
    public BaseVo<Integer> delete(@RequestBody Set<String> ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}/{otherData}")
    @SaCheckPermission(value = "ImageController:findById")
    @Operation(summary = "根据ID查询图片", operationId = "findByIdImageController")
    public BaseVo<ImageEntity> findById(@PathVariable String id, @PathVariable Boolean otherData) {
        return super.findById(id, otherData);
    }

    @Override
    @PostMapping("/get/{otherData}")
    @SaCheckPermission(value = "ImageController:findByIds")
    @Operation(summary = "根据ID批量查询图片", operationId = "findByIdsImageController")
    public BaseVo<List<ImageEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable Boolean otherData) {
        return super.findByIds(ids, otherData);
    }

    @Override
    @PostMapping("/page/{otherData}")
    @SaCheckPermission(value = "ImageController:findByPage")
    @Operation(summary = "分页查询图片", operationId = "findByPageImageController")
    public BaseVo<PageVo<ImageEntity>> findByPage(@RequestBody ImagePageDto pageDto, @PathVariable Boolean otherData) {
        return super.findByPage(pageDto, otherData);
    }

    @SneakyThrows
    @GetMapping("/download/{id}")
    @Operation(summary = "下载图片", operationId = "downloadImageController")
    @SaCheckPermission(value = "ImageController:download")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) {
        ImageEntity imageEntity = imageService.findById(id, true);
        FileEntity file = imageEntity.getFile();
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
