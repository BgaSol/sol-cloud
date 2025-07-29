package com.bgasol.web.system.department.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.department.dto.DepartmentCreateDto;
import com.bgasol.model.system.department.dto.DepartmentUpdateDto;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.web.system.department.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Override
    public DepartmentService commonBaseService() {
        return departmentService;
    }

    @Override
    @PostMapping
    @SaCheckPermission("department:save")
    @Operation(summary = "新增部门", operationId = "saveDepartment")
    public BaseVo<DepartmentEntity> save(@RequestBody @Valid DepartmentCreateDto createDto) {
        return super.save(createDto);
    }

    @Override
    @PutMapping
    @SaCheckPermission("department:update")
    @Operation(summary = "更新部门", operationId = "updateDepartment")
    public BaseVo<DepartmentEntity> update(@RequestBody @Valid DepartmentUpdateDto updateDto) {
        return super.update(updateDto);
    }

    @Override
    @DeleteMapping("/{ids}")
    @SaCheckPermission("department:delete")
    @Operation(summary = "删除部门", operationId = "deleteDepartment")
    public BaseVo<Integer[]> delete(@PathVariable("ids") String ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}")
    @SaCheckPermission("department:findById")
    @Operation(summary = "根据ID查询部门", operationId = "findDepartmentById")
    public BaseVo<DepartmentEntity> findById(@PathVariable("id") String id) {
        return super.findById(id);
    }

    @Override
    @GetMapping()
    @SaCheckPermission("department:findAll")
    @Operation(summary = "查询所有部门", operationId = "findAllDepartment")
    public BaseVo<List<DepartmentEntity>> findAll() {
        return super.findAll();
    }

    @GetMapping("/find-by-id-is-default")
    @Operation(summary = "查询默认部门", operationId = "findDefaultDepartment")
    public BaseVo<DepartmentEntity> findById() {
        return super.findById(DEFAULT_DEPARTMENT_ID);
    }

    @GetMapping("/get-my-department")
    @Operation(summary = "获取我的部门", operationId = "getMyDepartment")
    public BaseVo<DepartmentEntity> getMyDepartment(HttpServletRequest request) {
        // 获取域名
        String domain = request.getServerName();
        return BaseVo.success(departmentService.getMyDepartment(domain));
    }
}
