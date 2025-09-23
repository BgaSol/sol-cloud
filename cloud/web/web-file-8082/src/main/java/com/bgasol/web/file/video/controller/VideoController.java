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
import com.bgasol.web.file.video.service.VideoService;
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

    @SneakyThrows
    @GetMapping("/play/{id}")
    @Operation(summary = "在线播放文件", operationId = "playVideo")
    @SaCheckPermission("video:playVideo")
    public ResponseEntity<Resource> playVideo(
            @PathVariable("id") String id,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

        String contentType = videoService.findById(id).getFile().getType();

        // 获取输入流（你的视频数据来源，不要求知道大小）
        InputStream inputStream;
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            // 如果后端能支持 Range，就自己实现 skip
            String[] ranges = rangeHeader.substring(6).split("-");
            long rangeStart = Long.parseLong(ranges[0]);

            inputStream = videoService.videoStreamFindById(id);
            inputStream.skip(rangeStart);

            // 注意：这里没法知道 end 和总大小，只能返回 open-ended 的 Content-Range
            // 规范允许写成 "bytes {start}-/ * "
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-/*")
                    .body(new InputStreamResource(inputStream));

        } else {
            // 普通全量流式返回
            inputStream = videoService.videoStreamFindById(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.TRANSFER_ENCODING, "chunked")
                    .body(new InputStreamResource(inputStream));
        }
    }
}
