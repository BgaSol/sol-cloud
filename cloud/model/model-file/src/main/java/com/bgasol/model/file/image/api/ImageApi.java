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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        path = "/" + FileConfigValues.SERVICE_NAME + "/image",
        name = FileConfigValues.SERVICE_NAME,
        contextId = FileConfigValues.SERVICE_NAME + "-ImageApi"
)
public interface ImageApi {
    @PostMapping
    BaseVo<ImageEntity> save(@RequestBody @Valid ImageCreateDto createDto);

    @PutMapping
    BaseVo<ImageEntity> update(@RequestBody @Valid ImageUpdateDto updateDto);

    @GetMapping("/{id}")
    BaseVo<ImageEntity> findById(@PathVariable String id);

    @PostMapping("/page")
    BaseVo<PageVo<ImageEntity>> findByPage(@RequestBody @Valid ImagePageDto pageDto);

    @DeleteMapping("/{ids}")
    BaseVo<Integer[]> delete(@PathVariable String ids);

    @GetMapping("/ids/{ids}")
    BaseVo<List<ImageEntity>> findByIds(@PathVariable String ids);

}
