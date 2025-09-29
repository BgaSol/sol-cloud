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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        path = "/" + FileConfigValues.SERVICE_NAME + "/video",
        name = FileConfigValues.SERVICE_NAME,
        contextId = FileConfigValues.SERVICE_NAME + "-VideoApi"
)
public interface VideoApi {
    @PostMapping
    BaseVo<VideoEntity> save(@RequestBody @Valid VideoCreateDto createDto);

    @PutMapping
    BaseVo<VideoEntity> update(@RequestBody @Valid VideoUpdateDto updateDto);

    @GetMapping("/{id}")
    BaseVo<VideoEntity> findById(@PathVariable String id);

    @GetMapping("/ids/{ids}")
    BaseVo<List<VideoEntity>> findByIds(@PathVariable String ids);

    @PostMapping("/page")
    BaseVo<PageVo<VideoEntity>> findByPage(@RequestBody @Valid VideoPageDto pageDto);

    @DeleteMapping("/{ids}")
    BaseVo<Integer[]> delete(@PathVariable String ids);
}
