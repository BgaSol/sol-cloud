package com.bgasol.web.system.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BasePoiController;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BasePoiService;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.ImportResult;
import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.dto.RoleUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.web.system.role.service.RolePoiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "角色管理-poi")
@RequestMapping("/role-poi")
@Validated
public class RolePoiController extends BasePoiController<
        RoleEntity,
        BasePageDto<RoleEntity>,
        RoleCreateDto,
        RoleUpdateDto> {

    private final RolePoiService rolePoiService;

    @Override
    public BasePoiService<RoleEntity, BasePageDto<RoleEntity>, RoleCreateDto, RoleUpdateDto> commonBaseService() {
        return rolePoiService;
    }

    @GetMapping("/template-download")
    @Operation(summary = "下载角色导入模板", operationId = "downloadRoleImportTemplate")
    @SaCheckPermission(value = "role:downloadImportTemplate", orRole = "admin")
    public ResponseEntity<InputStreamResource> downloadImportTemplate() {
        return super.downloadImportTemplate();
    }

    @PostMapping(value = "/import", consumes = {"multipart/form-data"})
    @Operation(summary = "导入角色", operationId = "importRole")
    @SaCheckPermission(value = "role:importExcel", orRole = "admin")
    public BaseVo<ImportResult> importExcel(@RequestPart("file") MultipartFile file) {
        return super.importFromExcel(file);
    }

}
