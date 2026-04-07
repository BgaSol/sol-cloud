package com.bgasol.web.system.department.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseTreeService;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.web.system.department.mapper.DepartmentMapper;
import com.bgasol.web.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.bgasol.common.constant.value.SystemConfigValues.ADMIN_ROLE_ID;
import static com.bgasol.common.constant.value.SystemConfigValues.DEFAULT_DEPARTMENT_ID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class DepartmentService extends BaseTreeService<DepartmentEntity, BasePageDto<DepartmentEntity>> {
    private final DepartmentMapper departmentMapper;
    private final RedissonClient redissonClient;

    @Lazy
    private final UserService userService;

    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

    @Override
    public DepartmentMapper commonBaseMapper() {
        return departmentMapper;
    }

    @Transactional(readOnly = true)
    public DepartmentEntity findByDomain(String domain) {
        LambdaQueryWrapper<DepartmentEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DepartmentEntity::getDomain, domain);
        return this.departmentMapper.selectList(queryWrapper).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentEntity> findAll(boolean otherData) {
        List<String> roleList = StpUtil.getRoleList();
        if (roleList.contains(ADMIN_ROLE_ID)) {
            return super.findAll(otherData);
        }
        String departmentId = userService.findById(StpUtil.getLoginIdAsString(), false).getDepartmentId();
        return this.findById(Set.of(departmentId), otherData);
    }

    @Transactional(readOnly = true)
    public DepartmentEntity findDefault(String xForwardedHost, boolean otherData) {
        if (StpUtil.isLogin()) {
            // 获取当前登录用户的部门
            String userId = StpUtil.getLoginIdAsString();
            String departmentId = userService.findById(userId, false).getDepartmentId();
            return findById(departmentId, otherData);
        }

        DepartmentEntity department = this.findByDomain(xForwardedHost);
        if (ObjectUtils.isNotEmpty(department)) {
            return department;
        }

        return this.findById(DEFAULT_DEPARTMENT_ID, otherData);
    }
}
