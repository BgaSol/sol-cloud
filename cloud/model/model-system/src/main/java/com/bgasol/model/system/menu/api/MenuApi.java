package com.bgasol.model.system.menu.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.menu.entity.MenuEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/menu",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-MenuApi"
)
public interface MenuApi {
    @DeleteMapping("/{ids}")
    BaseVo<Integer[]> delete(@PathVariable("ids") String ids);

    @GetMapping("/{id}")
    BaseVo<MenuEntity> findById(@PathVariable("id") String id);

    @GetMapping()
    BaseVo<List<MenuEntity>> findAll();

    @GetMapping("/ids/{ids}")
    BaseVo<List<MenuEntity>> findByIds(@PathVariable String ids);

    @GetMapping("/find-by-menu-group/{group}")
    BaseVo<List<MenuEntity>> findByMenuGroup(@PathVariable("group") String group);

    @PostMapping("/init")
    BaseVo<MenuEntity> init(@RequestBody() MenuEntity entity);
}
