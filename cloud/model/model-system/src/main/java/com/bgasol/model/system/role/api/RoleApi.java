package com.bgasol.model.system.role.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.dto.RoleUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/role",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-RoleApi"
)
public interface RoleApi {
    @PostMapping
    BaseVo<RoleEntity> save(@RequestBody @Valid RoleCreateDto createDto);

    @PutMapping
    BaseVo<RoleEntity> update(@RequestBody @Valid RoleUpdateDto updateDto);

    @DeleteMapping("/{ids}")
    BaseVo<Integer[]> delete(@PathVariable("ids") String ids);

    @GetMapping("/{id}")
    BaseVo<RoleEntity> findById(@PathVariable("id") String id);

    @GetMapping("/ids/{ids}")
    BaseVo<List<RoleEntity>> findByIds(@PathVariable String ids);

    @GetMapping()
    BaseVo<List<RoleEntity>> findAll();
}
