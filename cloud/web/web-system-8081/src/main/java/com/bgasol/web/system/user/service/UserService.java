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

import java.util.ArrayList;
import java.util.List;

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
        String newPassword = userPasswordUpdateDto.getNewPassword();
        userEntity.setPassword(this.encodePassword(newPassword));
        return this.update(userEntity);
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
    @Transactional(readOnly = true)
    public void findOtherTable(UserEntity userEntity) {
        // 获取关联角色
        List<String> roleIds = this.userMapper.findFromTable("system_c_user_role",
                "user_id",
                userEntity.getId(),
                "role_id");
        List<RoleEntity> roleEntities = new ArrayList<>();
        for (String id : roleIds) {
            roleEntities.add(roleService.findById(id));
        }
        userEntity.setRoles(roleEntities);

        // 获取关联的部门
        DepartmentEntity departmentEntity = departmentService.findById(userEntity.getDepartmentId());
        userEntity.setDepartment(departmentEntity);
        super.findOtherTable(userEntity);
    }
}
