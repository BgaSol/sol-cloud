package com.bgasol.model.system.role.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.dto.RoleUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/role",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-RoleApi"
)
public interface RoleApi {
    @PostMapping("/insert")
    BaseVo<RoleEntity> insert(@RequestBody @Valid RoleCreateDto createDto);

    @PostMapping("/apply")
    BaseVo<RoleEntity> apply(@RequestBody @Valid RoleUpdateDto updateDto);

    @PostMapping("/delete")
    BaseVo<Integer> delete(@RequestBody Set<String> ids);

    @GetMapping("/{id}/{otherData}")
    BaseVo<RoleEntity> findById(@PathVariable("id") String id, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/get/{otherData}")
    BaseVo<List<RoleEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/page/{otherData}")
    BaseVo<PageVo<RoleEntity>> findByPage(@RequestBody BasePageDto<RoleEntity> pageDto, @PathVariable("otherData") Boolean otherData);

    @GetMapping("/all/{otherData}")
    BaseVo<List<RoleEntity>> findAll(@PathVariable("otherData") Boolean otherData);
}
