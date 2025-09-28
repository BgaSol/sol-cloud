package com.bgasol.web.system.user.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.user.dto.UserPageDto;
import com.bgasol.model.system.user.dto.UserPasswordResetDto;
import com.bgasol.model.system.user.dto.UserPasswordUpdateDto;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.web.system.department.service.DepartmentService;
import com.bgasol.web.system.role.service.RoleService;
import com.bgasol.web.system.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bgasol.common.constant.value.SystemConfigValues.DEFAULT_DEPARTMENT_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService extends BaseService<UserEntity, UserPageDto> {
    private final UserMapper userMapper;
    private final DepartmentService departmentService;
    private final RoleService roleService;
    private final RedissonClient redissonClient;

    @Value("${system.password.plaintext}")
    private boolean plaintextPassword;

    @Override
    public UserMapper commonBaseMapper() {
        return userMapper;
    }


    @Override
    public RedissonClient commonBaseRedissonClient() {
        return redissonClient;
    }

    @Override
    public Integer delete(String id) {
        // 退出用户
        StpUtil.logout(id);
        // 删除用户关联角色-中间表
        this.userMapper.deleteFromTable("system_c_user_role", "user_id", id);
        return super.delete(id);
    }

    @Override
    public UserEntity save(UserEntity entity) {
        // 检查用户名是否存在
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, entity.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BaseException("用户名已存在");
        }
        String password = entity.getPassword();
        if (ObjectUtils.isNotEmpty(password)) {
            entity.setPassword(this.encodePassword(password));
        }
        return super.save(entity);
    }

    @Override
    public UserEntity update(UserEntity entity) {
        String password = entity.getPassword();
        if (ObjectUtils.isNotEmpty(password)) {
            entity.setPassword(this.encodePassword(password));
        }
        return super.update(entity);
    }

    public UserEntity updatePassword(UserPasswordUpdateDto userPasswordUpdateDto) {
        String userid = StpUtil.getLoginIdAsString();
        UserEntity userEntity = this.cacheSearch(userid);
        String userInputOldPassword = userPasswordUpdateDto.getOldPassword();
        // 对比新旧密码
        if (!userEntity.getPassword().equals(this.encodePassword(userInputOldPassword))) {
            throw new BaseException("原密码错误");
        }
        String newPassword = this.encodePassword(userPasswordUpdateDto.getNewPassword());
        return this.update(UserEntity.builder()
                .id(userid)
                .password(newPassword)
                .build());
    }

    public UserEntity resetPassword(UserPasswordResetDto userPasswordResetDto) {
        UserEntity entity = userPasswordResetDto.toEntity();
        entity.setPassword(this.encodePassword(entity.getPassword()));
        return this.update(entity);
    }

    @Transactional(readOnly = true)
    public UserEntity getUserInfo() {
        return this.getUserInfo(StpUtil.getLoginIdAsString());
    }

    @Transactional(readOnly = true)
    public UserEntity getUserInfo(String id) {
        return this.findById(id);
    }

    /**
     * @param password 明文密码
     * @return 密文密码
     */
    public String encodePassword(String password) {
        if (plaintextPassword) {
            return password;
        } else {
            // 使用MD5加密
            return DigestUtils.md5Hex(password);
        }
    }

    @Override
    public void findOtherTable(List<UserEntity> list) {
        List<String> userIds = list.stream().map(UserEntity::getId).toList();
        Map<String, List<String>> roleIdGroup = this.findFromTableBatch(
                "system_c_user_role",
                "user_id",
                userIds,
                "role_id");

        Set<String> roleIds = roleIdGroup
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        Map<String, RoleEntity> roleMap = roleService
                .findByIds(roleIds.toArray(String[]::new))
                .stream()
                .collect(Collectors.toMap(RoleEntity::getId, Function.identity()));

        Set<String> departmentIds = list.stream().map(UserEntity::getDepartmentId).collect(Collectors.toSet());
        Map<String, DepartmentEntity> collect = departmentService.findByIds(departmentIds.toArray(String[]::new)).stream()
                .collect(Collectors.toMap(DepartmentEntity::getId, Function.identity()));
        for (UserEntity userEntity : list) {
            userEntity.setRoles(roleIdGroup
                    .getOrDefault(userEntity.getId(), List.of())
                    .stream()
                    .map(roleMap::get)
                    .toList());
            if (ObjectUtils.isNotEmpty(userEntity.getDepartmentId())) {
                userEntity.setDepartment(collect.get(userEntity.getDepartmentId()));
            }
        }
    }

    /// 获取当前访问者所属的用户的部门
    @Transactional(readOnly = true)
    public DepartmentEntity getMyDepartment(String domain) {
        if (StpUtil.isLogin()) {
            // 获取当前登录用户的部门
            String userId = StpUtil.getLoginIdAsString();
            return this.findById(userId).getDepartment();
        } else if (ObjectUtils.isNotEmpty(domain)) {
            // 获取当前域的部门
            return departmentService.findByDomain(domain);
        }
        // 若都找不到则返回默认部门
        return departmentService.findById(DEFAULT_DEPARTMENT_ID);
    }
}
