package com.bgasol.web.system.role.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.service.BasePoiService;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.dto.RoleUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.web.system.menu.service.MenuService;
import com.bgasol.web.system.permission.service.PermissionService;
import com.bgasol.web.system.role.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RolePoiService extends BasePoiService<RoleEntity,
        BasePageDto<RoleEntity>,
        RoleCreateDto,
        RoleUpdateDto> {
    private final RoleMapper roleMapper;
    private final RoleService roleService;
    private final MenuService menuService;
    private final PermissionService permissionService;

    @Override
    protected BaseService<RoleEntity, BasePageDto<RoleEntity>> getService() {
        return roleService;
    }

    @Override
    protected boolean validateImportedEntity(RoleEntity entity, int rowIndex, List<String> errors) {
        List<String> permissionIds = toIds(entity.getPermissions());
        List<String> menuIds = toIds(entity.getMenus());
        return validateDtoFields(entity.getName(), entity.getCode(), permissionIds, menuIds, rowIndex, errors);
    }

    private List<String> toIds(List<? extends BaseEntity> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(BaseEntity::getId).collect(Collectors.toList());
    }

    private boolean addErrorAndFail(List<String> errors, int rowIndex, String message) {
        errors.add("第" + rowIndex + "行错误: " + message);
        return false;
    }

    private boolean validateDtoFields(String name,
                                      String code,
                                      List<String> permissionIds,
                                      List<String> menuIds,
                                      int rowIndex,
                                      List<String> errors) {
        if (StringUtils.isBlank(name)) {
            return addErrorAndFail(errors, rowIndex, "角色名不能为空");
        }
        if (StringUtils.isBlank(code)) {
            return addErrorAndFail(errors, rowIndex, "角色编码不能为空");
        }

        // code 唯一校验
        LambdaQueryWrapper<RoleEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(RoleEntity::getCode, code);
        RoleEntity exists = roleMapper.selectOne(qw);
        if (exists != null) {
            return addErrorAndFail(errors, rowIndex, "角色编码已存在: " + code);
        }

        // 关联ID有效性校验（可选，存在即通过）
        if (permissionIds != null) {
            for (String pid : permissionIds) {
                PermissionEntity p = permissionService.findById(pid);
                if (p == null) {
                    return addErrorAndFail(errors, rowIndex, "权限ID不存在: " + pid);
                }
            }
        }
        if (menuIds != null) {
            for (String mid : menuIds) {
                MenuEntity m = menuService.findById(mid);
                if (m == null) {
                    return addErrorAndFail(errors, rowIndex, "菜单ID不存在: " + mid);
                }
            }
        }
        return true;
    }
}
