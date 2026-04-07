package com.bgasol.model.file.video.api;

import com.bgasol.common.constant.value.FileConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.file.video.dto.VideoCreateDto;
import com.bgasol.model.file.video.dto.VideoPageDto;
import com.bgasol.model.file.video.dto.VideoUpdateDto;
import com.bgasol.model.file.video.entity.VideoEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(
        path = "/" + FileConfigValues.SERVICE_NAME + "/video",
        name = FileConfigValues.SERVICE_NAME,
        contextId = FileConfigValues.SERVICE_NAME + "-VideoApi"
)
public interface VideoApi {
    @PostMapping("/insert")
    BaseVo<VideoEntity> insert(@RequestBody @Valid VideoCreateDto createDto);

    @PostMapping("/apply")
    BaseVo<VideoEntity> apply(@RequestBody @Valid VideoUpdateDto updateDto);

    @PostMapping("/delete")
    BaseVo<Integer> delete(@RequestBody Set<String> ids);

    @GetMapping("/{id}/{otherData}")
    BaseVo<VideoEntity> findById(@PathVariable("id") String id, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/get/{otherData}")
    BaseVo<List<VideoEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/page/{otherData}")
    BaseVo<PageVo<VideoEntity>> findByPage(@RequestBody @Valid VideoPageDto pageDto, @PathVariable("otherData") Boolean otherData);

    @GetMapping("/play/{id}")
    ResponseEntity<Resource> playVideo(@PathVariable("id") String id, @RequestHeader(value = "Range", required = false) String rangeHeader);
}
