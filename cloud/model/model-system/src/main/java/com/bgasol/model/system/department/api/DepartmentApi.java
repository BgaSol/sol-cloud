package com.bgasol.model.system.department.api;

import com.bgasol.common.constant.value.SystemConfigValues;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        path = "/" + SystemConfigValues.SERVICE_NAME + "/department",
        name = SystemConfigValues.SERVICE_NAME,
        contextId = SystemConfigValues.SERVICE_NAME + "-DepartmentApi"
)
public interface DepartmentApi {
    @GetMapping("/{id}")
    BaseVo<DepartmentEntity> findById(@PathVariable("id") String id);
}
