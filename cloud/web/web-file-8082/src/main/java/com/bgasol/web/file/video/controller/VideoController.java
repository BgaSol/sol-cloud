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
import com.bgasol.web.file.file.service.FileService;
import com.bgasol.web.file.video.service.VideoService;
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
@Tag(name = "视频管理")
@RequestMapping("/video")
public class VideoController extends BaseController<
        VideoEntity,
        VideoPageDto,
        VideoCreateDto,
        VideoUpdateDto> {
    private final VideoService videoService;

    private final FileService fileService;

    @Override
    public VideoService commonBaseService() {
        return videoService;
    }

    @Override
    @PostMapping
    @Operation(summary = "新增视频", operationId = "saveVideo")
    @SaCheckPermission("video:save")
    public BaseVo<VideoEntity> save(@RequestBody @Valid VideoCreateDto createDto) {
        return super.save(createDto);
    }

    @Override
    @PutMapping
    @Operation(summary = "更新视频", operationId = "updateVideo")
    @SaCheckPermission("video:update")
    public BaseVo<VideoEntity> update(@RequestBody @Valid VideoUpdateDto updateDto) {
        return super.update(updateDto);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "查询视频", operationId = "findVideoById")
    @SaCheckPermission("video:findById")
    public BaseVo<VideoEntity> findById(@PathVariable String id) {
        return super.findById(id);
    }

    @Override
    @PostMapping("/page")
    @Operation(summary = "分页查询视频", operationId = "findPageVideo")
    @SaCheckPermission("video:findByPage")
    public BaseVo<PageVo<VideoEntity>> findByPage(@RequestBody @Valid VideoPageDto pageDto) {
        return super.findByPage(pageDto);
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除视频", operationId = "deleteVideo")
    @SaCheckPermission("video:delete")
    public BaseVo<Integer[]> delete(@PathVariable String ids) {
        return super.delete(ids);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载视频", operationId = "downloadVideo")
    @SaCheckPermission("video:download")
    public ResponseEntity<InputStreamResource> download(@PathVariable("id") String id) {
        VideoEntity videoEntity = videoService.findById(id);
        FileEntity file = videoEntity.getFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getSize()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(
                        fileService.getFileName(file),
                        StandardCharsets.UTF_8
                ))
                .contentType(MediaType.valueOf(file.getType()))
                .body(new InputStreamResource(videoService.videoStreamFindById(videoEntity.getId())));
    }
}
