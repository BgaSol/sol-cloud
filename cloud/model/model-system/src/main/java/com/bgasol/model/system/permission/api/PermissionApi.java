package com.bgasol.model.system.permission.api;

import ch.qos.logback.core.spi.ConfigurationEvent;
import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.permission.dto.PermissionCreateDto;
import com.bgasol.model.system.permission.dto.PermissionUpdateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/permission",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-PermissionApi"
)
public interface PermissionApi {

    @PostMapping("/insert")
    BaseVo<PermissionEntity> insert(@RequestBody @Valid PermissionCreateDto createDto);

    @PostMapping("/apply")
    BaseVo<PermissionEntity> apply(@RequestBody @Valid PermissionUpdateDto updateDto);

    @PostMapping("/delete")
    BaseVo<Integer> delete(@RequestBody Set<String> ids);

    @GetMapping("/{id}/{otherData}")
    BaseVo<PermissionEntity> findById(@PathVariable("id") String id, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/get/{otherData}")
    BaseVo<List<PermissionEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable("otherData") Boolean otherData);

    @GetMapping("/all/{otherData}")
    BaseVo<List<PermissionEntity>> findAll(@PathVariable("otherData") Boolean otherData);

    @PostMapping("/init")
    BaseVo<PermissionEntity> init(@RequestBody() PermissionEntity entity);
}
