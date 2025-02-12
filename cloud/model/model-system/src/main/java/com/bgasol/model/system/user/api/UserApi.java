package com.bgasol.model.system.user.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.user.dto.UserCreateDto;
import com.bgasol.model.system.user.entity.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/user",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-UserApi"
)
public interface UserApi {
    @GetMapping("/{id}")
    BaseVo<UserEntity> findById(@PathVariable("id") String id);

    @PostMapping
    BaseVo<UserEntity> save(@RequestBody UserCreateDto createDto);
}
