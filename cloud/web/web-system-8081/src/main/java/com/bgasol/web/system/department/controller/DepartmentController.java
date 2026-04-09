package com.bgasol.web.system.department.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
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
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
    @PostMapping("/insert")
    @SaCheckPermission(value = "DepartmentController:insert")
    @Operation(summary = "新增部门", operationId = "insertDepartmentController")
    public BaseVo<DepartmentEntity> insert(@RequestBody DepartmentCreateDto createDto) {
        return super.insert(createDto);
    }

    @Override
    @PostMapping("/apply")
    @SaCheckPermission(value = "DepartmentController:apply")
    @Operation(summary = "更新部门", operationId = "applyDepartmentController")
    public BaseVo<DepartmentEntity> apply(@RequestBody DepartmentUpdateDto updateDto) {
        return super.apply(updateDto);
    }

    @Override
    @PostMapping("/delete")
    @SaCheckPermission(value = "DepartmentController:delete")
    @Operation(summary = "删除部门", operationId = "deleteDepartmentController")
    public BaseVo<Integer> delete(@RequestBody Set<String> ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}/{otherData}")
    @SaCheckPermission(value = "DepartmentController:findById")
    @Operation(summary = "根据ID查询部门", operationId = "findByIdDepartmentController")
    public BaseVo<DepartmentEntity> findById(@PathVariable String id, @PathVariable Boolean otherData) {
        return super.findById(id, otherData);
    }

    @Override
    @PostMapping("/get/{otherData}")
    @SaCheckPermission(value = "DepartmentController:findByIds")
    @Operation(summary = "根据ID批量查询部门", operationId = "findByIdsDepartmentController")
    public BaseVo<List<DepartmentEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable Boolean otherData) {
        return super.findByIds(ids, otherData);
    }

    @Override
    @GetMapping("/all/{otherData}")
    @SaCheckPermission(value = "DepartmentController:findAll")
    @Operation(summary = "查询所有部门", operationId = "findAllDepartmentController")
    public BaseVo<List<DepartmentEntity>> findAll(@PathVariable Boolean otherData) {
        return super.findAll(otherData);
    }

    @SaIgnore
    @GetMapping("/get/default/{otherData}")
    @Operation(summary = "查询默认部门", operationId = "findDefaultDepartmentController")
    public BaseVo<DepartmentEntity> findDefault(@PathVariable Boolean otherData, HttpServletRequest request) {
        String host = request.getHeader("X-Forwarded-Host");
        if (StringUtils.isBlank(host)) {
            host = request.getHeader("Host");
        }
        if (StringUtils.isBlank(host)) {
            host = request.getServerName();
        }
        if (StringUtils.contains(host, ",")) {
            host = StringUtils.substringBefore(host, ",").trim();
        }
        if (StringUtils.contains(host, ":")) {
            host = StringUtils.substringBefore(host, ":");
        }
        return BaseVo.success(this.departmentService.findDefault(host, BooleanUtils.isTrue(otherData)));
    }

}
