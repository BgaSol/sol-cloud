package com.bgasol.model.system.permission.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/permission",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-PermissionApi"
)
public interface PermissionApi {
    @PostMapping("/init")
    BaseVo<PermissionEntity> init(@RequestBody PermissionEntity entity);

    @GetMapping("/{id}")
    BaseVo<PermissionEntity> findById(@PathVariable("id") String id);
}
