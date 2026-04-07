package com.bgasol.web.system.permission.service;

import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseTreeService;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.web.system.permission.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService extends BaseTreeService<PermissionEntity, BasePageDto<PermissionEntity>> {
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionMapper commonBaseMapper() {
        return permissionMapper;
    }

}
