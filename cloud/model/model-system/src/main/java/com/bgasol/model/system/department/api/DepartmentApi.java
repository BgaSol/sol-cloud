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

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/department",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-DepartmentApi"
)
public interface DepartmentApi {

    @PostMapping
    BaseVo<DepartmentEntity> save(@RequestBody @Valid DepartmentCreateDto createDto);

    @PutMapping
    BaseVo<DepartmentEntity> update(@RequestBody @Valid DepartmentUpdateDto updateDto);

    @DeleteMapping("/{ids}")
    BaseVo<Integer[]> delete(@PathVariable("ids") String ids);

    @GetMapping("/{id}")
    BaseVo<DepartmentEntity> findById(@PathVariable("id") String id);

    @GetMapping("/ids/{ids}")
    BaseVo<List<DepartmentEntity>> findByIds(@PathVariable String ids);

    @GetMapping()
    BaseVo<List<DepartmentEntity>> findAll();
}
