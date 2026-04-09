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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "权限管理")
@RequestMapping("/permission")
@Validated
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
    @PostMapping("/insert")
    @SaCheckPermission(value = "PermissionController:insert")
    @Operation(summary = "新增权限", operationId = "insertPermissionController")
    public BaseVo<PermissionEntity> insert(@RequestBody PermissionCreateDto createDto) {
        return super.insert(createDto);
    }

    @Override
    @PostMapping("/apply")
    @SaCheckPermission(value = "PermissionController:apply")
    @Operation(summary = "更新权限", operationId = "applyPermissionController")
    public BaseVo<PermissionEntity> apply(@RequestBody PermissionUpdateDto updateDto) {
        return super.apply(updateDto);
    }

    @Override
    @PostMapping("/delete")
    @SaCheckPermission(value = "PermissionController:delete")
    @Operation(summary = "删除权限", operationId = "deletePermissionController")
    public BaseVo<Integer> delete(@RequestBody Set<String> ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}/{otherData}")
    @SaCheckPermission(value = "PermissionController:findById")
    @Operation(summary = "根据ID查询权限", operationId = "findByIdPermissionController")
    public BaseVo<PermissionEntity> findById(@PathVariable String id, @PathVariable Boolean otherData) {
        return super.findById(id, otherData);
    }

    @Override
    @PostMapping("/get/{otherData}")
    @SaCheckPermission(value = "PermissionController:findByIds")
    @Operation(summary = "根据ID批量查询权限", operationId = "findByIdsPermissionController")
    public BaseVo<List<PermissionEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable Boolean otherData) {
        return super.findByIds(ids, otherData);
    }

    @Override
    @GetMapping("/all/{otherData}")
    @SaCheckPermission(value = "PermissionController:findAll")
    @Operation(summary = "查询所有权限", operationId = "findAllPermissionController")
    public BaseVo<List<PermissionEntity>> findAll(@PathVariable Boolean otherData) {
        return super.findAll(otherData);
    }
}
