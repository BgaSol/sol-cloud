package com.bgasol.web.system.permission.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.permission.dto.PermissionCreateDto;
import com.bgasol.model.system.permission.dto.PermissionUpdateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.web.system.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "权限管理")
@RequestMapping("/permission")
public class PermissionController extends BaseController<
        PermissionEntity,
        BasePageDto<PermissionEntity>,
        PermissionCreateDto,
        PermissionUpdateDto> {
    private final PermissionService permissionService;

    @Override
    public PermissionService commonBaseService() {
        return permissionService;
    }

    @Override
    @GetMapping()
    @Operation(summary = "查询所有权限", operationId = "findAllPermission")
    @SaCheckPermission(value = "permission:findAll", orRole = "admin")
    public BaseVo<List<PermissionEntity>> findAll() {
        return super.findAll();
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除权限", operationId = "deletePermission")
    @SaCheckPermission(value = "permission:delete", orRole = "admin")
    public BaseVo<Integer[]> delete(@PathVariable("ids") String ids) {
        return super.delete(ids);
    }

    @PostMapping("/init")
    @Operation(summary = "批量初始化系统的权限信息", operationId = "initPermission")
    @SaCheckPermission(value = "permission:init", orRole = "admin")
    public BaseVo<PermissionEntity> init(@RequestBody() PermissionEntity entity) {
        PermissionEntity save = permissionService.init(entity);
        return BaseVo.success(save, "保存成功");
    }

    @Override
    @GetMapping("/ids/{ids}")
    @Operation(summary = "根据id批量查询权限", operationId = "findPermissionByIds")
    @SaCheckPermission(value = "permission:findByIds", orRole = "admin")
    public BaseVo<List<PermissionEntity>> findByIds(@PathVariable String ids) {
        return super.findByIds(ids);
    }

}
