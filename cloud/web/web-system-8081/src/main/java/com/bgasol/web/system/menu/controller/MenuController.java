package com.bgasol.web.system.menu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.menu.dto.MenuCreateDto;
import com.bgasol.model.system.menu.dto.MenuUpdateDto;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.web.system.menu.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "菜单管理")
@RequestMapping("/menu")
@Validated
public class MenuController extends BaseController<
        MenuEntity,
        BasePageDto<MenuEntity>,
        MenuCreateDto,
        MenuUpdateDto> {
    private final MenuService menuService;

    @Override
    public MenuService commonBaseService() {
        return menuService;
    }

    @Override
    @PostMapping("/insert")
    @SaCheckPermission(value = "MenuController:insert", orRole = "admin")
    @Operation(summary = "新增菜单", operationId = "insertMenuController")
    public BaseVo<MenuEntity> insert(@RequestBody MenuCreateDto createDto) {
        return super.insert(createDto);
    }

    @Override
    @PostMapping("/apply")
    @SaCheckPermission(value = "MenuController:apply", orRole = "admin")
    @Operation(summary = "更新菜单", operationId = "applyMenuController")
    public BaseVo<MenuEntity> apply(@RequestBody MenuUpdateDto updateDto) {
        return super.apply(updateDto);
    }

    @Override
    @PostMapping("/delete")
    @SaCheckPermission(value = "MenuController:delete", orRole = "admin")
    @Operation(summary = "删除菜单", operationId = "deleteMenuController")
    public BaseVo<Integer> delete(@RequestBody Set<String> ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}/{otherData}")
    @SaCheckPermission(value = "MenuController:findById", orRole = "admin")
    @Operation(summary = "根据ID查询菜单", operationId = "findByIdMenuController")
    public BaseVo<MenuEntity> findById(@PathVariable String id, @PathVariable Boolean otherData) {
        return super.findById(id, otherData);
    }

    @Override
    @PostMapping("/get/{otherData}")
    @SaCheckPermission(value = "MenuController:findByIds", orRole = "admin")
    @Operation(summary = "根据ID批量查询菜单", operationId = "findByIdsMenuController")
    public BaseVo<List<MenuEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable Boolean otherData) {
        return super.findByIds(ids, otherData);
    }

    @Override
    @GetMapping("/all/{otherData}")
    @SaCheckPermission(value = "MenuController:findAll", orRole = "admin")
    @Operation(summary = "查询所有菜单", operationId = "findAllMenuController")
    public BaseVo<List<MenuEntity>> findAll(@PathVariable Boolean otherData) {
        return super.findAll(otherData);
    }

    @GetMapping("/routes")
    @Operation(summary = "查询可访问的所有路由", operationId = "findAllMenuRoutes")
    public BaseVo<List<MenuEntity>> findAllMenuRoutes() {
        return BaseVo.success(menuService.findAllMenuRoutes());
    }

    @GetMapping("/get/menu-group/{group}")
    @Operation(summary = "查询指定菜单组下的菜单", operationId = "findByGroupMenuController")
    @SaCheckPermission(value = "MenuController:findByGroup", orRole = "admin")
    public BaseVo<List<MenuEntity>> findByGroup(@PathVariable String group) {
        return BaseVo.success(this.menuService.findByMenuGroup(group));
    }

    @PostMapping("/init")
    @Operation(summary = "批量初始化系统的菜单信息", operationId = "initMenu")
    @SaCheckPermission(value = "menu:init", orRole = "admin")
    public BaseVo<Void> init(@RequestBody() MenuEntity entity) {
        menuService.init(entity);
        return BaseVo.success(null, "保存成功");
    }
}
