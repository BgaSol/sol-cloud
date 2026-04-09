package com.bgasol.web.system.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.capability.PoiCapability;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.ImportResult;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.dto.RoleUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.web.system.role.service.RoleService;
import com.bgasol.web.system.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

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
    private final UserService userService;
    private final PoiCapability poiCapability;

    @Override
    public PoiCapability getPoiCapability() {
        return poiCapability;
    }

    @Override
    public RoleService commonBaseService() {
        return roleService;
    }

    @Override
    @PostMapping("/insert")
    @SaCheckPermission(value = "RoleController:insert")
    @Operation(summary = "新增角色", operationId = "insertRoleController")
    public BaseVo<RoleEntity> insert(@RequestBody RoleCreateDto createDto) {
        return super.insert(createDto);
    }

    @Override
    @PostMapping("/apply")
    @SaCheckPermission(value = "RoleController:apply")
    @Operation(summary = "更新角色", operationId = "applyRoleController")
    public BaseVo<RoleEntity> apply(@RequestBody RoleUpdateDto updateDto) {
        return super.apply(updateDto);
    }

    @Override
    @PostMapping("/delete")
    @SaCheckPermission(value = "RoleController:delete")
    @Operation(summary = "删除角色", operationId = "deleteRoleController")
    public BaseVo<Integer> delete(@RequestBody Set<String> ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}/{otherData}")
    @SaCheckPermission(value = "RoleController:findById")
    @Operation(summary = "根据ID查询角色", operationId = "findByIdRoleController")
    public BaseVo<RoleEntity> findById(@PathVariable String id, @PathVariable Boolean otherData) {
        return super.findById(id, otherData);
    }

    @Override
    @PostMapping("/get/{otherData}")
    @SaCheckPermission(value = "RoleController:findByIds")
    @Operation(summary = "根据ID批量查询角色", operationId = "findByIdsRoleController")
    public BaseVo<List<RoleEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable Boolean otherData) {
        return super.findByIds(ids, otherData);
    }

    @Override
    @PostMapping("/page/{otherData}")
    @SaCheckPermission(value = "RoleController:findByPage")
    @Operation(summary = "分页查询角色", operationId = "findByPageRoleController")
    public BaseVo<PageVo<RoleEntity>> findByPage(@RequestBody BasePageDto<RoleEntity> pageDto, @PathVariable Boolean otherData) {
        return super.findByPage(pageDto, otherData);
    }

    @Override
    @GetMapping("/all/{otherData}")
    @SaCheckPermission(value = "RoleController:findAll")
    @Operation(summary = "查询所有角色", operationId = "findAllRoleController")
    public BaseVo<List<RoleEntity>> findAll(@PathVariable Boolean otherData) {
        return super.findAll(otherData);
    }

    @GetMapping("/all")
    @SaCheckPermission(value = "RoleController:findAll")
    @Operation(summary = "查询所有角色(支持设置otherData)", operationId = "findAllWithParamRoleController")
    public BaseVo<List<RoleEntity>> findAllByParam(@RequestParam(defaultValue = "false") Boolean otherData) {
        return super.findAll(otherData);
    }

    @GetMapping("/template-download")
    @Operation(summary = "下载角色导入模板", operationId = "downloadRoleImportTemplate")
    @SaCheckPermission(value = "role:downloadImportTemplate")
    public ResponseEntity<InputStreamResource> downloadImportTemplate() {
        return super.downloadImportTemplate();
    }

    @PostMapping(value = "/import", consumes = {"multipart/form-data"})
    @Operation(summary = "导入角色", operationId = "importRole")
    @SaCheckPermission(value = "role:importExcel")
    public BaseVo<ImportResult> importExcel(@RequestPart("file") MultipartFile file) {
        return super.importFromExcel(file);
    }

}
