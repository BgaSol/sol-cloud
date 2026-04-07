package com.bgasol.model.system.department.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.department.dto.DepartmentCreateDto;
import com.bgasol.model.system.department.dto.DepartmentUpdateDto;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/department",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-DepartmentApi"
)
public interface DepartmentApi {

    @PostMapping("/insert")
    BaseVo<DepartmentEntity> insert(@RequestBody @Valid DepartmentCreateDto createDto);

    @PostMapping("/apply")
    BaseVo<DepartmentEntity> apply(@RequestBody @Valid DepartmentUpdateDto updateDto);

    @PostMapping("/delete")
    BaseVo<Integer> delete(@RequestBody Set<String> ids);

    @GetMapping("/{id}/{otherData}")
    BaseVo<DepartmentEntity> findById(@PathVariable("id") String id, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/get/{otherData}")
    BaseVo<List<DepartmentEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable("otherData") Boolean otherData);

    @GetMapping("/all/{otherData}")
    BaseVo<List<DepartmentEntity>> findAll(@PathVariable("otherData") Boolean otherData);

    @GetMapping("/get/default/{otherData}")
    BaseVo<DepartmentEntity> findDefault(@PathVariable("otherData") Boolean otherData);
}
