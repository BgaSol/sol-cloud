package com.bgasol.model.file.file.api;

import com.bgasol.common.constant.value.FileConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.file.file.dto.FileCreateDto;
import com.bgasol.model.file.file.dto.FilePageDto;
import com.bgasol.model.file.file.dto.FileUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(
        path = "/" + FileConfigValues.SERVICE_NAME + "/file",
        name = FileConfigValues.SERVICE_NAME,
        contextId = FileConfigValues.SERVICE_NAME + "-FileApi"
)
public interface FileApi {
    @PostMapping(value = "/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    BaseVo<FileEntity> insert(@ModelAttribute FileCreateDto createDto);

    @PostMapping("/apply")
    BaseVo<FileEntity> apply(@RequestBody @Valid FileUpdateDto updateDto);

    @PostMapping("/delete")
    BaseVo<Integer> delete(@RequestBody Set<String> ids);

    @GetMapping("/{id}/{otherData}")
    BaseVo<FileEntity> findById(@PathVariable("id") String id, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/get/{otherData}")
    BaseVo<List<FileEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/page/{otherData}")
    BaseVo<PageVo<FileEntity>> findByPage(@RequestBody @Valid FilePageDto pageDto, @PathVariable("otherData") Boolean otherData);

    @GetMapping("/all/{otherData}")
    BaseVo<List<FileEntity>> findAll(@PathVariable("otherData") Boolean otherData);

    @GetMapping("/download/{id}")
    ResponseEntity<InputStreamResource> download(@PathVariable("id") String id);
}
