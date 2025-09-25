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
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除菜单", operationId = "deleteMenu")
    @SaCheckPermission("menu:delete")
    public BaseVo<Integer[]> delete(@PathVariable("ids") String ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜单", operationId = "findMenuById")
    @SaCheckPermission("menu:findById")
    public BaseVo<MenuEntity> findById(@PathVariable("id") String id) {
        return super.findById(id);
    }

    @GetMapping("/routes")
    @Operation(summary = "查询可访问的所有路由", operationId = "findAllMenuRoutes")
    public BaseVo<List<MenuEntity>> findAllMenuRoutes() {
        return BaseVo.success(menuService.findAllMenuRoutes());
    }

    @Override
    @GetMapping()
    @Operation(summary = "查询所有菜单", operationId = "findAllMenu")
    @SaCheckPermission("menu:findAll")
    public BaseVo<List<MenuEntity>> findAll() {
        return super.findAll();
    }

    @GetMapping("/find-admin-menu-group")
    @Operation(summary = "查询管理员菜单组", operationId = "findAdminMenuGroup")
    @SaCheckPermission("menu:findAdminMenuGroup")
    public BaseVo<List<MenuEntity>> findAdminMenuGroup() {
        return BaseVo.success(this.menuService.findAdminMenuGroup());
    }

    @GetMapping("/find-by-menu-group/{group}")
    @Operation(summary = "查询指定菜单组下的菜单", operationId = "findByMenuGroup")
    @SaCheckPermission("menu:findByMenuGroup")
    public BaseVo<List<MenuEntity>> findByMenuGroup(@PathVariable("group") String group) {
        return BaseVo.success(this.menuService.findByMenuGroup(group));
    }

    @PostMapping("/init")
    @Operation(summary = "批量初始化系统的菜单信息", operationId = "initMenu")
    @SaCheckPermission("menu:init")
    public BaseVo<MenuEntity> init(@RequestBody() MenuEntity entity) {
        MenuEntity save = menuService.init(entity);
        return BaseVo.success(save, "保存成功");
    }
}
