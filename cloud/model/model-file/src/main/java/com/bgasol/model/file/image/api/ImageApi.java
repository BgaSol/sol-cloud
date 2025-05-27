package com.bgasol.model.file.image.api;

import com.bgasol.common.constant.value.FileConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.file.image.dto.ImageCreateDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        path = "/" + FileConfigValues.SERVICE_NAME + "/image",
        name = FileConfigValues.SERVICE_NAME,
        contextId = FileConfigValues.SERVICE_NAME + "-ImageApi"
)
public interface ImageApi {
    @GetMapping("/{id}")
    BaseVo<ImageEntity> findById(@PathVariable("id") String id);

    @PostMapping
    BaseVo<ImageEntity> save(@RequestBody @Valid ImageCreateDto createDto);
}
