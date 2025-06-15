package com.bgasol.model.file.file.api;

import com.bgasol.common.constant.value.FileConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.file.file.dto.FileCreateDto;
import com.bgasol.model.file.file.dto.FileUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        path = "/" + FileConfigValues.SERVICE_NAME + "/file",
        name = FileConfigValues.SERVICE_NAME,
        contextId = FileConfigValues.SERVICE_NAME + "-FileApi"
)
public interface FileApi {
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    BaseVo<FileEntity> save(FileCreateDto fileCreateDto);

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    BaseVo<FileEntity> update(FileUpdateDto fileUpdateDto);

    @DeleteMapping("/{ids}")
    BaseVo<Integer[]> delete(@PathVariable("ids") String ids);

    @GetMapping("/{id}")
    BaseVo<FileEntity> findById(@PathVariable("id") String id);

    @GetMapping(value = "/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Response download(@PathVariable("id") String id);

}
