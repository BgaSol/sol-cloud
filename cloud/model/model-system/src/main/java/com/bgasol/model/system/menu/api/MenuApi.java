package com.bgasol.model.system.menu.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.menu.entity.MenuEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/menu",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-MenuApi"
)
public interface MenuApi {
    @PostMapping("/init")
    BaseVo<MenuEntity> init(@RequestBody MenuEntity entity);

    @GetMapping("/{id}")
    BaseVo<MenuEntity> findById(@PathVariable("id") String id);
}
