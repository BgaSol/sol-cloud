package com.bgasol.model.file.file.api;

import com.bgasol.common.constant.value.FileConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.file.file.dto.FileCreateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        path = "/" + FileConfigValues.SERVICE_NAME + "/file",
        name = FileConfigValues.SERVICE_NAME,
        contextId = FileConfigValues.SERVICE_NAME + "-FileApi"
)
public interface FileApi {
    @PostMapping
    BaseVo<FileEntity> save(FileCreateDto fileCreateDto);

    @DeleteMapping("/{ids}")
    BaseVo<Integer[]> delete(@PathVariable("ids") String ids);

    @GetMapping("/{id}")
    BaseVo<FileEntity> findById(@PathVariable("id") String id);

    @GetMapping("/download/{id}")
    ResponseEntity<InputStreamResource> download(@PathVariable("id") String id);
}
