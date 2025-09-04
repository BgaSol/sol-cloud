package com.bgasol.web.system.department.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import com.bgasol.common.core.base.service.BaseService;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bgasol.common.constant.value.SystemConfigValues.DEFAULT_DEPARTMENT_ID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Transactional
@Slf4j
public class DepartmentService extends BaseService<DepartmentEntity, BasePageDto<DepartmentEntity>> {
    private final DepartmentMapper departmentMapper;

    @Lazy
    private final UserService userService;

    private final RedissonClient redissonClient;

    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

    @Override
    public DepartmentMapper commonBaseMapper() {
        return departmentMapper;
    }

    /**
     * 获取当前访问者所属的用户的部门
     *
     * @param domain 访问者所在域
     * @return 部门实体
     */
    @Transactional(readOnly = true)
    public DepartmentEntity getMyDepartment(String domain) {
        if (StpUtil.isLogin()) {
            // 获取当前登录用户的部门
            String userId = StpUtil.getLoginIdAsString();
            return userService.findById(userId).getDepartment();
        } else if (ObjectUtils.isNotEmpty(domain)) {
            // 获取当前域的部门
            LambdaQueryWrapper<DepartmentEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DepartmentEntity::getDomain, domain);
            DepartmentEntity departmentEntity = this.departmentMapper.selectOne(queryWrapper);
            // 当前域下不一定有部门
            if (ObjectUtils.isNotEmpty(departmentEntity)) {
                return departmentEntity;
            }
        }
        return this.findById(DEFAULT_DEPARTMENT_ID);
    }

    @Override
    public Integer delete(String id) {
        DepartmentEntity department = this.findById(id);
        if (ObjectUtils.isEmpty(department)){
            return 1;
        }
        HashSet<String> deptIds = new HashSet<>();
        deptIds.add(id);

        List<DepartmentEntity> departments = findTreeAll(id, null);
        collectDeleteIds(departments, deptIds);

        deptIds.forEach(super::delete);
        return 1;
    }

    private void collectDeleteIds(List<DepartmentEntity> departments, HashSet<String> ids) {
        List<DepartmentEntity> departmentEntityList = departments.stream()
                .filter(e -> !e.getChildren().isEmpty())
                .flatMap(e -> e.getChildren().stream())
                .toList();

        // 将当前层级子部门的ID添加到删除集合中
        departmentEntityList.forEach(dept -> ids.add(dept.getId()));
        departments.forEach(dept -> ids.add(dept.getId()));

        if (!departmentEntityList.isEmpty()) {
            // 递归处理下一层级的子部门
            this.collectDeleteIds(departmentEntityList, ids);
        }
    }
}
