package com.bgasol.web.system.department.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.department.dto.DepartmentCreateDto;
import com.bgasol.model.system.department.dto.DepartmentUpdateDto;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.web.system.department.service.DepartmentService;
import com.bgasol.web.system.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bgasol.common.constant.value.SystemConfigValues.ADMIN_USER_ID;
import static com.bgasol.common.constant.value.SystemConfigValues.DEFAULT_DEPARTMENT_ID;

@RestController
@RequiredArgsConstructor
@Tag(name = "部门管理")
@RequestMapping("/department")
@Validated
public class DepartmentController extends BaseController<
        DepartmentEntity,
        BasePageDto<DepartmentEntity>,
        DepartmentCreateDto,
        DepartmentUpdateDto> {
    private final DepartmentService departmentService;
    private final UserService userService;

    @Override
    public DepartmentService commonBaseService() {
        return departmentService;
    }

    @Override
    @PostMapping
    @SaCheckPermission(value = "department:save", orRole = "admin")
    @Operation(summary = "新增部门", operationId = "saveDepartment")
    public BaseVo<DepartmentEntity> save(@RequestBody @Valid DepartmentCreateDto createDto) {
        return super.save(createDto);
    }

    @Override
    @PutMapping
    @SaCheckPermission(value = "department:update", orRole = "admin")
    @Operation(summary = "更新部门", operationId = "updateDepartment")
    public BaseVo<DepartmentEntity> update(@RequestBody @Valid DepartmentUpdateDto updateDto) {
        return super.update(updateDto);
    }

    @Override
    @DeleteMapping("/{ids}")
    @SaCheckPermission(value = "department:delete", orRole = "admin")
    @Operation(summary = "删除部门", operationId = "deleteDepartment")
    public BaseVo<Integer[]> delete(@PathVariable("ids") String ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}")
    @SaCheckPermission(value = "department:findById", orRole = "admin")
    @Operation(summary = "根据ID查询部门", operationId = "findDepartmentById")
    public BaseVo<DepartmentEntity> findById(@PathVariable("id") String id) {
        return super.findById(id);
    }

    @Override
    @GetMapping("/ids/{ids}")
    @Operation(summary = "根据id批量查询部门", operationId = "findDepartmentByIds")
    @SaCheckPermission(value = "department:findByIds", orRole = "admin")
    public BaseVo<List<DepartmentEntity>> findByIds(@PathVariable String ids) {
        return super.findByIds(ids);
    }

    @Override
    @GetMapping()
    @SaCheckPermission(value = "department:findAll", orRole = "admin")
    @Operation(summary = "查询所有部门", operationId = "findAllDepartment")
    public BaseVo<List<DepartmentEntity>> findAll() {
        String userId = StpUtil.getLoginIdAsString();
        if (ADMIN_USER_ID.equals(userId)) {
            // 管理员不需要做数据范围限制
            return super.findAll();
        }
        DepartmentEntity department = userService.findById(userId).getDepartment();
        return BaseVo.success(List.of(department));
    }

    @GetMapping("/find-default")
    @Operation(summary = "查询默认部门", operationId = "findDefaultDepartment")
    @SaIgnore
    public BaseVo<DepartmentEntity> findDefaultDepartment() {
        return super.findById(DEFAULT_DEPARTMENT_ID);
    }
}
