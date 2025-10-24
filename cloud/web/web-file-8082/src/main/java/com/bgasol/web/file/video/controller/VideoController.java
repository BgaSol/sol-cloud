package com.bgasol.web.file.video.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.file.file.entity.FileEntity;
import com.bgasol.model.file.video.dto.VideoCreateDto;
import com.bgasol.model.file.video.dto.VideoPageDto;
import com.bgasol.model.file.video.dto.VideoUpdateDto;
import com.bgasol.model.file.video.entity.VideoEntity;
import com.bgasol.plugin.minio.service.OssService;
import com.bgasol.web.file.video.service.VideoService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "视频管理")
@RequestMapping("/video")
public class VideoController extends BaseController<
        VideoEntity,
        VideoPageDto,
        VideoCreateDto,
        VideoUpdateDto> {
    private final VideoService videoService;
    private final OssService ossService;

    private final MinioClient minioClient;

    @Override
    public VideoService commonBaseService() {
        return videoService;
    }

    @Override
    @PostMapping
    @Operation(summary = "新增视频", operationId = "saveVideo")
    @SaCheckPermission(value = "video:save", orRole = "admin")
    public BaseVo<VideoEntity> save(@RequestBody @Valid VideoCreateDto createDto) {
        return super.save(createDto);
    }

    @Override
    @PutMapping
    @Operation(summary = "更新视频", operationId = "updateVideo")
    @SaCheckPermission(value = "video:update", orRole = "admin")
    public BaseVo<VideoEntity> update(@RequestBody @Valid VideoUpdateDto updateDto) {
        return super.update(updateDto);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "查询视频", operationId = "findVideoById")
    @SaCheckPermission(value = "video:findById", orRole = "admin")
    public BaseVo<VideoEntity> findById(@PathVariable String id) {
        return super.findById(id);
    }

    @Override
    @GetMapping("/ids/{ids}")
    @Operation(summary = "根据id批量查询视频", operationId = "findVideoByIds")
    @SaCheckPermission(value = "video:findByIds", orRole = "admin")
    public BaseVo<List<VideoEntity>> findByIds(@PathVariable String ids) {
        return super.findByIds(ids);
    }

    @Override
    @PostMapping("/page")
    @Operation(summary = "分页查询视频", operationId = "findPageVideo")
    @SaCheckPermission(value = "video:findByPage", orRole = "admin")
    public BaseVo<PageVo<VideoEntity>> findByPage(@RequestBody @Valid VideoPageDto pageDto) {
        return super.findByPage(pageDto);
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除视频", operationId = "deleteVideo")
    @SaCheckPermission(value = "video:delete", orRole = "admin")
    public BaseVo<Integer[]> delete(@PathVariable String ids) {
        return super.delete(ids);
    }


    @SneakyThrows
    @GetMapping("/play/{id}")
    @Operation(summary = "在线播放文件", operationId = "playVideo")
    @SaCheckPermission(value = "video:playVideo", orRole = "admin")
    public ResponseEntity<Resource> playVideo(
            @PathVariable("id") String id,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

        FileEntity file = videoService.findById(id).getFile();
        String contentType = file.getType();
        String bucket = file.getBucket();
        String objectName = ossService.buildObjectPath(file);

        StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build());
        long fileSize = stat.size();

        // 默认返回整个文件
        long rangeStart = 0L;
        long rangeEnd = fileSize - 1L;
        boolean isPartial = false;

        // 只支持单个 Range；多段 Range（含逗号）不处理 -> 返回 416
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String rangesSpec = rangeHeader.substring(6).trim();
            if (rangesSpec.contains(",")) {
                // 多区间不支持
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                        .build();
            }

            if (rangesSpec.startsWith("-")) {
                // suffix bytes: e.g. bytes=-500  -> last 500 bytes
                long suffixLength = Long.parseLong(rangesSpec.substring(1));
                if (suffixLength <= 0) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                            .build();
                }
                if (suffixLength > fileSize) suffixLength = fileSize;
                rangeStart = fileSize - suffixLength;
                rangeEnd = fileSize - 1;
            } else {
                String[] parts = rangesSpec.split("-", 2);
                rangeStart = Long.parseLong(parts[0].trim());
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    rangeEnd = Long.parseLong(parts[1].trim());
                } else {
                    rangeEnd = fileSize - 1;
                }
            }

            if (rangeStart > rangeEnd || rangeStart >= fileSize) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                        .build();
            }
            isPartial = true;
        }

        long contentLength = rangeEnd - rangeStart + 1; // 逐位计算，确保无误

        // 从 MinIO 获取分片或全量流
        GetObjectArgs.Builder getArgsBuilder = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName);
        if (isPartial) {
            getArgsBuilder.offset(rangeStart).length(contentLength);
        }
        InputStream objectStream = minioClient.getObject(getArgsBuilder.build());
        InputStreamResource resource = new InputStreamResource(objectStream);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, contentType);
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        // 可选：ETag/Last-Modified 如果需要缓存或断点续传
        if (stat.etag() != null) {
            headers.set(HttpHeaders.ETAG, stat.etag());
        }
        if (isPartial) {
            headers.set(HttpHeaders.CONTENT_RANGE,
                    "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resource);
        }
    }
}