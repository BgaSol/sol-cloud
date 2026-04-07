package com.bgasol.web.system.user.service;

import cn.dev33.satoken.stp.StpUtil;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.role.entity.RoleEntity;
import com.bgasol.model.system.user.dto.UserPageDto;
import com.bgasol.model.system.user.dto.UserPasswordResetDto;
import com.bgasol.model.system.user.dto.UserPasswordUpdateDto;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.model.system.user.entity.UserRoleTable;
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

@Service
@RequiredArgsConstructor
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
    public Integer delete(Set<String> ids) {
        ids.forEach(StpUtil::logout);
        return super.delete(ids);
    }

    @Transactional
    @Override
    public void insert(UserEntity entity) {
        String password = entity.getPassword();
        if (ObjectUtils.isNotEmpty(password)) {
            entity.setPassword(this.encodePassword(password));
        }
        super.insert(entity);
    }

    @Override
    public void apply(UserEntity entity) {
        String password = entity.getPassword();
        if (ObjectUtils.isNotEmpty(password)) {
            entity.setPassword(this.encodePassword(password));
        }
        super.apply(entity);
    }

    @Transactional
    public void updatePassword(UserPasswordUpdateDto userPasswordUpdateDto) {
        String userid = StpUtil.getLoginIdAsString();
        UserEntity userEntity = this.findById(userid, false);
        String userInputOldPassword = userPasswordUpdateDto.getOldPassword();
        // 对比新旧密码
        if (!userEntity.getPassword().equals(this.encodePassword(userInputOldPassword))) {
            throw new BaseException("原密码错误");
        }

        String newPassword = this.encodePassword(userPasswordUpdateDto.getNewPassword());
        this.apply(UserEntity.builder()
                .id(userid)
                .password(newPassword)
                .build());
    }

    @Transactional
    public void resetPassword(UserPasswordResetDto dto) {
        this.apply(UserEntity.builder()
                .id(dto.getId())
                .password(this.encodePassword(dto.getPassword()))
                .build());
    }

    @Transactional(readOnly = true)
    @Override
    public void findOtherTable(List<UserEntity> list) {
        List<String> userIds = list.stream()
                .map(UserEntity::getId)
                .filter(ObjectUtils::isNotEmpty)
                .toList();
        Map<String, List<String>> roleIdGroup = this.findFromTableBatch(
                UserRoleTable.NAME, UserRoleTable.USER_ID, userIds, UserRoleTable.ROLE_ID);

        Set<String> roleIds = roleIdGroup
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        Map<String, RoleEntity> roleMap = roleService
                .findById(roleIds, true)
                .stream()
                .collect(Collectors.toMap(RoleEntity::getId, Function.identity()));

        Set<String> departmentIds = list.stream().map(UserEntity::getDepartmentId).collect(Collectors.toSet());
        Map<String, DepartmentEntity> collect = departmentService
                .findById(departmentIds, true)
                .stream()
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
}
