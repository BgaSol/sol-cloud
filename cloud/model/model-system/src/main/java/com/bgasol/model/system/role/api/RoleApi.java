package com.bgasol.model.system.role.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.role.entity.RoleEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/role",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-RoleApi"
)
public interface RoleApi {
    @GetMapping("/{id}")
    BaseVo<RoleEntity> findById(@PathVariable("id") String id);
}
