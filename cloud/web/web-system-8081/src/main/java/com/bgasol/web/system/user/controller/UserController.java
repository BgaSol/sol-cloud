package com.bgasol.web.system.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.SaTokenInfo;
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

    @GetMapping("/get-verification-code")
    @Operation(summary = "获取验证码", operationId = "getVerificationCode")
    public BaseVo<VerificationVo> getVerificationCode() {
        return BaseVo.success(this.loginService.getVerificationCode());
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", operationId = "login")
    public BaseVo<SaTokenInfo> login(@RequestBody @Valid UserLoginDto userLoginDto) {
        return BaseVo.success(this.loginService.login(userLoginDto));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", operationId = "logout")
    @SaCheckPermission({"user:logout"})
    public BaseVo<?> logout() {
        this.loginService.logout();
        return BaseVo.success("登出成功");
    }

    @GetMapping("/user-info")
    @Operation(summary = "获取用户信息", operationId = "getUserInfo")
    public BaseVo<UserEntity> getUserInfo() {
        UserEntity user = this.userService.getUserInfo();
        return BaseVo.success(user);
    }

    @PutMapping("/update-password")
    @Operation(summary = "修改用户密码", operationId = "updateUserPassword")
    @SaCheckPermission("user:updatePassword")
    public BaseVo<UserEntity> updatePassword(@RequestBody @Valid UserPasswordUpdateDto userPasswordUpdateDto) {
        UserEntity userEntity = this.userService.updatePassword(userPasswordUpdateDto);
        return BaseVo.success(userEntity, "修改密码成功");
    }

    @PutMapping("/reset-password")
    @Operation(summary = "重置用户密码", operationId = "resetUserPassword")
    @SaCheckPermission("user:resetPassword")
    public BaseVo<UserEntity> resetPassword(@RequestBody @Valid UserPasswordResetDto userPasswordResetDto) {
        UserEntity userEntity = this.userService.resetPassword(userPasswordResetDto);
        return BaseVo.success(userEntity, "重置密码成功");
    }

    @Override
    @PostMapping("/page")
    @Operation(summary = "分页查询用户", operationId = "findPageUser")
    @SaCheckPermission("user:findByPage")
    public BaseVo<PageVo<UserEntity>> findByPage(@RequestBody @Valid UserPageDto pageDto) {
        return super.findByPage(pageDto);
    }

    @Override
    @PostMapping
    @Operation(summary = "保存用户", operationId = "saveUser")
    @SaCheckPermission("user:save")
    public BaseVo<UserEntity> save(@RequestBody @Valid UserCreateDto entity) {
        return super.save(entity);
    }

    @Override
    @PutMapping
    @Operation(summary = "更新用户", operationId = "updateUser")
    @SaCheckPermission("user:update")
    public BaseVo<UserEntity> update(@RequestBody @Valid UserUpdateDto entity) {
        return super.update(entity);
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除用户并强制退出登录", operationId = "deleteUser")
    @SaCheckPermission("user:delete")
    public BaseVo<Integer[]> delete(@PathVariable("ids") String ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询用户", operationId = "findUserById")
    @SaCheckPermission("user:findById")
    public BaseVo<UserEntity> findById(@PathVariable("id") String id) {
        return super.findById(id);
    }

    @GetMapping("findAllOnlineUser")
    @Operation(summary = "查询所有在线用户", operationId = "findAllOnlineUser")
    @SaCheckPermission("user:findOnlineUser")
    public BaseVo<List<UserEntity>> findOnlineUser() {
        return BaseVo.success(this.loginService.findOnlineUser());
    }
}
