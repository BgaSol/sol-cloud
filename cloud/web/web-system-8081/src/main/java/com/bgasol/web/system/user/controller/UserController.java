package com.bgasol.web.system.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.system.user.dto.*;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.model.system.user.vo.VerificationVo;
import com.bgasol.web.system.user.service.LoginService;
import com.bgasol.web.system.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "用户管理")
@RequestMapping("/user")
@Validated
public class UserController extends BaseController<
        UserEntity,
        UserPageDto,
        UserCreateDto,
        UserUpdateDto> {
    private final UserService userService;
    private final LoginService loginService;

    @Override
    public UserService commonBaseService() {
        return userService;
    }

    @Override
    @PostMapping("/insert")
    @SaCheckPermission(value = "UserController:insert")
    @Operation(summary = "新增用户", operationId = "insertUserController")
    public BaseVo<UserEntity> insert(@RequestBody UserCreateDto createDto) {
        return super.insert(createDto);
    }

    @Override
    @PostMapping("/apply")
    @SaCheckPermission(value = "UserController:apply")
    @Operation(summary = "更新用户", operationId = "applyUserController")
    public BaseVo<UserEntity> apply(@RequestBody UserUpdateDto updateDto) {
        return super.apply(updateDto);
    }

    @Override
    @PostMapping("/delete")
    @SaCheckPermission(value = "UserController:delete")
    @Operation(summary = "删除用户", operationId = "deleteUserController")
    public BaseVo<Integer> delete(@RequestBody Set<String> ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}/{otherData}")
    @SaCheckPermission(value = "UserController:findById")
    @Operation(summary = "根据ID查询用户", operationId = "findByIdUserController")
    public BaseVo<UserEntity> findById(@PathVariable String id, @PathVariable Boolean otherData) {
        return super.findById(id, otherData);
    }

    @Override
    @PostMapping("/get/{otherData}")
    @SaCheckPermission(value = "UserController:findByIds")
    @Operation(summary = "根据ID批量查询用户", operationId = "findByIdsUserController")
    public BaseVo<List<UserEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable Boolean otherData) {
        return super.findByIds(ids, otherData);
    }

    @Override
    @PostMapping("/page/{otherData}")
    @SaCheckPermission(value = "UserController:findByPage")
    @Operation(summary = "分页查询用户", operationId = "findByPageUserController")
    public BaseVo<PageVo<UserEntity>> findByPage(@RequestBody UserPageDto pageDto, @PathVariable Boolean otherData) {
        return super.findByPage(pageDto, otherData);
    }

    @Override
    @GetMapping("/all/{otherData}")
    @SaCheckPermission(value = "UserController:findAll")
    @Operation(summary = "查询所有用户", operationId = "findAllUserController")
    public BaseVo<List<UserEntity>> findAll(@PathVariable Boolean otherData) {
        return super.findAll(otherData);
    }

    @SaIgnore
    @GetMapping("/get/verification/code")
    @Operation(summary = "获取验证码", operationId = "getVerificationCodeUserController")
    public BaseVo<VerificationVo> getVerificationCode() {
        return BaseVo.success(this.loginService.getVerificationCode());
    }

    @SaIgnore
    @PostMapping("/login")
    @Operation(summary = "用户登录", operationId = "loginUserController")
    public BaseVo<SaTokenInfo> login(@RequestBody @Valid UserLoginDto userLoginDto) {
        return BaseVo.success(this.loginService.login(userLoginDto));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", operationId = "logoutUserController")
    public BaseVo<String> logout() {
        this.loginService.logout();
        return BaseVo.success("登出成功", "登出成功");
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息", operationId = "getInfoUserController")
    public BaseVo<UserEntity> getInfo() {
        UserEntity user = userService.findById(StpUtil.getLoginIdAsString(), true);
        return BaseVo.success(user);
    }

    @PutMapping("/update-password")
    @Operation(summary = "修改用户密码", operationId = "updateUserPasswordUserController")
    @SaCheckPermission(value = "UserController:updatePassword")
    public BaseVo<Void> updatePassword(@RequestBody @Valid UserPasswordUpdateDto userPasswordUpdateDto) {
        this.userService.updatePassword(userPasswordUpdateDto);
        return BaseVo.success(null, "修改密码成功");
    }

    @PutMapping("/reset-password")
    @Operation(summary = "重置用户密码", operationId = "resetUserPasswordUserController")
    @SaCheckPermission(value = "UserController:resetPassword")
    public BaseVo<Void> resetPassword(@RequestBody @Valid UserPasswordResetDto userPasswordResetDto) {
        this.userService.resetPassword(userPasswordResetDto);
        return BaseVo.success(null, "重置密码成功");
    }

    @GetMapping("/all/online/{otherData}")
    @Operation(summary = "查询所有在线用户", operationId = "findAllOnlineUserUserController")
    @SaCheckPermission(value = "UserController:findOnlineUser")
    public BaseVo<List<UserEntity>> findOnlineUser(@PathVariable Boolean otherData) {
        return BaseVo.success(this.loginService.findOnlineUser(otherData));
    }
}
