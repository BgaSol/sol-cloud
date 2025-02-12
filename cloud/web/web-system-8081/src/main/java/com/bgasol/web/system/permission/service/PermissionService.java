package com.bgasol.web.system.permission.service;

import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.web.system.permission.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService extends BaseService<PermissionEntity, BasePageDto<PermissionEntity>> {
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionMapper commonBaseMapper() {
        return permissionMapper;
    }

    public PermissionEntity init(PermissionEntity parentPermission) {
        if (permissionMapper.selectById(parentPermission.getId()) == null) {
            this.save(parentPermission);
        } else {
            this.update(parentPermission);
        }
        for (PermissionEntity permission : parentPermission.getChildren()) {
            if (permissionMapper.selectById(permission.getId()) == null) {
                this.save(permission);
            } else {
                this.update(permission);
            }
        }
        return this.findById(parentPermission.getId());
    }
}
