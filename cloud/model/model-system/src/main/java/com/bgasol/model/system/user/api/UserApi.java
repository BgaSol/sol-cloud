package com.bgasol.model.system.user.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.user.dto.UserCreateDto;
import com.bgasol.model.system.user.dto.UserUpdateDto;
import com.bgasol.model.system.user.entity.UserEntity;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/user",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-UserApi"
)
public interface UserApi {
    @GetMapping("/user-info")
    BaseVo<UserEntity> getUserInfo();

    @PostMapping
    BaseVo<UserEntity> save(@RequestBody @Valid UserCreateDto entity);

    @PutMapping
    BaseVo<UserEntity> update(@RequestBody @Valid UserUpdateDto entity);

    @DeleteMapping("/{ids}")
    BaseVo<Integer[]> delete(@PathVariable("ids") String ids);

    @GetMapping("/{id}")
    BaseVo<UserEntity> findById(@PathVariable("id") String id);

    @GetMapping("findAllOnlineUser")
    BaseVo<List<UserEntity>> findOnlineUser();

    @GetMapping("/ids/{ids}")
    BaseVo<List<UserEntity>> findByIds(@PathVariable String ids);
}
