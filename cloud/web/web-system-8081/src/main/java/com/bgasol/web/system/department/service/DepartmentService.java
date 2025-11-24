package com.bgasol.web.system.department.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseTreeService;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.web.system.department.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService extends BaseTreeService<DepartmentEntity, BasePageDto<DepartmentEntity>> {
    private final DepartmentMapper departmentMapper;
    private final RedissonClient redissonClient;

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
        LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DepartmentEntity::getDomain, domain);
        return this.departmentMapper.selectList(queryWrapper).stream().findFirst().orElse(null);
    }
}
