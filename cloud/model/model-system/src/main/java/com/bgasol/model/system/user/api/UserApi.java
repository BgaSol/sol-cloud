package com.bgasol.model.system.user.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.model.system.user.dto.UserCreateDto;
import com.bgasol.model.system.user.dto.UserPageDto;
import com.bgasol.model.system.user.dto.UserUpdateDto;
import com.bgasol.model.system.user.entity.UserEntity;
import com.bgasol.plugin.openfeign.interceptor.GlobalScope;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/user",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-UserApi"
)
public interface UserApi {
    @GetMapping("/user-info")
    BaseVo<UserEntity> getUserInfo();

    @PostMapping("/insert")
    BaseVo<UserEntity> insert(@RequestBody @Valid UserCreateDto createDto);

    @PostMapping("/apply")
    BaseVo<UserEntity> apply(@RequestBody @Valid UserUpdateDto updateDto);

    @PostMapping("/delete")
    BaseVo<Integer> delete(@RequestBody Set<String> ids);

    @GetMapping("/{id}/{otherData}")
    @GlobalScope
    BaseVo<UserEntity> findById(@PathVariable("id") String id, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/get/{otherData}")
    BaseVo<List<UserEntity>> findByIds(@RequestBody Set<String> ids, @PathVariable("otherData") Boolean otherData);

    @PostMapping("/page/{otherData}")
    BaseVo<PageVo<UserEntity>> findByPage(@RequestBody UserPageDto pageDto, @PathVariable("otherData") Boolean otherData);

    @GetMapping("/all/{otherData}")
    BaseVo<List<UserEntity>> findAll(@PathVariable("otherData") Boolean otherData);

    @GetMapping("/all/online/{otherData}")
    BaseVo<List<UserEntity>> findOnlineUser(@PathVariable("otherData") Boolean otherData);
}
