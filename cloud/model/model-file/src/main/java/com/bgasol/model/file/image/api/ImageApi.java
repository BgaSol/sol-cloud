package com.bgasol.model.file.image.api;

import com.bgasol.common.constant.value.FileConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.file.image.dto.ImageCreateDto;
import com.bgasol.model.file.image.dto.ImagePageDto;
import com.bgasol.model.file.image.dto.ImageUpdateDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(
        path = "/" + FileConfigValues.SERVICE_NAME + "/image",
        name = FileConfigValues.SERVICE_NAME,
        contextId = FileConfigValues.SERVICE_NAME + "-ImageApi"
)
public interface ImageApi {
    @PostMapping("/insert")
    BaseVo<ImageEntity> insert(@RequestBody @Valid ImageCreateDto createDto);

    @PostMapping("/apply")
    BaseVo<ImageEntity> apply(@RequestBody @Valid ImageUpdateDto updateDto);

    @PostMapping("/delete")
    BaseVo<Integer> delete(@RequestBody Set<String> ids);

    @GetMapping("/{id}/{otherData}")
    BaseVo<ImageEntity> findById(@PathVariable("id") String id, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/get/{otherData}")
    BaseVo<List<ImageEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/page/{otherData}")
    BaseVo<PageVo<ImageEntity>> findByPage(@RequestBody @Valid ImagePageDto pageDto, @PathVariable("otherData") Boolean otherData);

    @GetMapping("/download/{id}")
    ResponseEntity<InputStreamResource> download(@PathVariable("id") String id);
}
