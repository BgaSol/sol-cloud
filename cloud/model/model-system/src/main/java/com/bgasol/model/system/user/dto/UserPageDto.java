package com.bgasol.model.system.user.dto;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.model.system.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "用户分页查询参数")
public class UserPageDto extends BasePageDto<UserEntity> {
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "账户锁定")
    private Boolean locked;

    @Schema(description = "部门id")
    String departmentId;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public AbstractLambdaWrapper<UserEntity, LambdaQueryWrapper<UserEntity>> getQueryWrapper() {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ObjectUtils.isNotEmpty(username), UserEntity::getUsername, username);
        queryWrapper.like(ObjectUtils.isNotEmpty(nickname), UserEntity::getNickname, nickname);
        queryWrapper.like(ObjectUtils.isNotEmpty(email), UserEntity::getEmail, email);
        queryWrapper.like(ObjectUtils.isNotEmpty(phone), UserEntity::getPhone, phone);
        queryWrapper.like(ObjectUtils.isNotEmpty(status), UserEntity::getStatus, status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(locked), UserEntity::getLocked, locked);
        queryWrapper.eq(ObjectUtils.isNotEmpty(departmentId), UserEntity::getDepartmentId, departmentId);
        return queryWrapper;
    }
}
