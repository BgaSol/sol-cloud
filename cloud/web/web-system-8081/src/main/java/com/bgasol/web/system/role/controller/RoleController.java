package com.bgasol.web.system.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.dto.RoleUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.web.system.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "角色管理")
@RequestMapping("/role")
@Validated
public class RoleController extends BaseController<
        RoleEntity,
        BasePageDto<RoleEntity>,
        RoleCreateDto,
        RoleUpdateDto> {
    private final RoleService roleService;

    @Override
    public RoleService commonBaseService() {
        return roleService;
    }

    @Override
    @PostMapping
    @Operation(summary = "新增角色", operationId = "saveRole")
    @SaCheckPermission(value = "role:save", orRole = "admin")
    public BaseVo<RoleEntity> save(@RequestBody @Valid RoleCreateDto createDto) {
        return super.save(createDto);
    }

    @Override
    @PutMapping
    @Operation(summary = "更新角色", operationId = "updateRole")
    @SaCheckPermission(value = "role:update", orRole = "admin")
    public BaseVo<RoleEntity> update(@RequestBody @Valid RoleUpdateDto updateDto) {
        return super.update(updateDto);
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除角色", operationId = "deleteRole")
    @SaCheckPermission(value = "role:delete", orRole = "admin")
    public BaseVo<Integer[]> delete(@PathVariable("ids") String ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询角色", operationId = "findRoleById")
    @SaCheckPermission(value = "role:findById", orRole = "admin")
    public BaseVo<RoleEntity> findById(@PathVariable("id") String id) {
        return super.findById(id);
    }

    @Override
    @GetMapping("/ids/{ids}")
    @Operation(summary = "根据id批量查询角色", operationId = "findRoleByIds")
    @SaCheckPermission(value = "role:findByIds", orRole = "admin")
    public BaseVo<List<RoleEntity>> findByIds(@PathVariable String ids) {
        return super.findByIds(ids);
    }

    @Override
    @GetMapping()
    @Operation(summary = "查询所有角色", operationId = "findAllRole")
    @SaCheckPermission(value = "role:findAll", orRole = "admin")
    public BaseVo<List<RoleEntity>> findAll() {
        return super.findAll();
    }
}
