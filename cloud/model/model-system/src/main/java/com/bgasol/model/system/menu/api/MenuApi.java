package com.bgasol.model.system.menu.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.menu.dto.MenuCreateDto;
import com.bgasol.model.system.menu.dto.MenuUpdateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/menu",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-MenuApi"
)
public interface MenuApi {
    @PostMapping("/insert")
    BaseVo<MenuEntity> insert(@RequestBody @Valid MenuCreateDto createDto);

    @PostMapping("/apply")
    BaseVo<MenuEntity> apply(@RequestBody @Valid MenuUpdateDto updateDto);

    @PostMapping("/delete")
    BaseVo<Integer> delete(@RequestBody Set<String> ids);

    @GetMapping("/{id}/{otherData}")
    BaseVo<MenuEntity> findById(@PathVariable("id") String id, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/get/{otherData}")
    BaseVo<List<MenuEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable("otherData") Boolean otherData);

    @GetMapping("/all/{otherData}")
    BaseVo<List<MenuEntity>> findAll(@PathVariable("otherData") Boolean otherData);

    @GetMapping("/routes")
    BaseVo<List<MenuEntity>> findAllMenuRoutes();

    @GetMapping("/find-by-menu-group/{group}")
    BaseVo<List<MenuEntity>> findByMenuGroup(@PathVariable("group") String group);

    @PostMapping("/init")
    BaseVo<Void> init(@RequestBody() MenuEntity entity);
}
