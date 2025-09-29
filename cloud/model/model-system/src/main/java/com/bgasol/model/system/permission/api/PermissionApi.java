package com.bgasol.model.system.permission.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/permission",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-PermissionApi"
)
public interface PermissionApi {

    @GetMapping()
    BaseVo<List<PermissionEntity>> findAll();

    @DeleteMapping("/{ids}")
    BaseVo<Integer[]> delete(@PathVariable("ids") String ids);

    @PostMapping("/init")
    BaseVo<PermissionEntity> init(@RequestBody() PermissionEntity entity);

    @GetMapping("/ids/{ids}")
    BaseVo<List<PermissionEntity>> findByIds(@PathVariable String ids);

}
