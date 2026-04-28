package com.bgasol.plugin.satoken.runner;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.model.system.menu.entity.MenuEntity;
import com.bgasol.model.system.permission.api.PermissionApi;
import com.bgasol.model.system.permission.dto.PermissionCreateDto;
import com.bgasol.model.system.permission.dto.PermissionUpdateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import com.bgasol.model.system.role.api.RoleApi;
import com.bgasol.model.system.role.dto.RoleCreateDto;
import com.bgasol.model.system.role.dto.RoleUpdateDto;
import com.bgasol.model.system.role.entity.RoleEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ControllerScanner {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final PermissionApi permissionApi;
    private final RoleApi roleApi;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @EventListener(InstanceRegisteredEvent.class)
    public void scanControllers() throws InterruptedException {
        Thread.sleep(TimeUnit.SECONDS.toMillis(5));

        log.info("Scanning controllers...");

        Map<String, RoleEntity> roles = new HashMap<>();
        Map<String, PermissionEntity> permissions = new HashMap<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {

            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            Class<?> controllerClass = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();

            scanMethod(controllerClass, method, mappingInfo, permissions, roles);
        }
        Map<String, PermissionEntity> oldPermissions = permissionApi.findByIds(permissions.keySet(), false)
                .getData()
                .stream()
                .collect(Collectors.toMap(PermissionEntity::getId, Function.identity()));

        // 老数据中有的则更新，没有的新增
        permissions.forEach((key, value) -> {
            if (oldPermissions.containsKey(key)) {
                PermissionEntity permission = permissionApi.apply(
                        PermissionUpdateDto.builder()
                                .id(value.getId())
                                .type(value.getType())
                                .description(value.getDescription())
                                .parentId(value.getParentId())
                                .name(value.getName())
                                .code(value.getCode())
                                .path(value.getPath())
                                .microService(value.getMicroService())
                                .build()
                ).getData();
                log.info("apply permission:{}", permission.getName());

            } else {
                PermissionEntity permission = permissionApi.insert(
                        PermissionCreateDto.builder()
                                .id(value.getId())
                                .type(value.getType())
                                .description(value.getDescription())
                                .parentId(value.getParentId())
                                .name(value.getName())
                                .code(value.getCode())
                                .path(value.getPath())
                                .microService(value.getMicroService())
                                .build()
                ).getData();
                log.info("insert permission:{}", permission.getName());

            }
        });
        Map<String, RoleEntity> oldRoles = roleApi.findByIds(roles.keySet(), true)
                .getData()
                .stream()
                .collect(Collectors.toMap(RoleEntity::getId, Function.identity()));

        roles.forEach((key, value) -> {
            if (oldRoles.containsKey(key)) {
                RoleEntity oldRole = oldRoles.get(key);

                oldRole.getPermissions().removeIf(permission -> contextPath.equals(permission.getMicroService()));
                oldRole.getPermissions().addAll(value.getPermissions());

                RoleEntity role = roleApi.apply(RoleUpdateDto.builder()
                        .id(value.getId())
                        .code(value.getId())
                        .name(oldRole.getName())
                        .menuIds(oldRole.getMenus()
                                .stream()
                                .map(MenuEntity::getId)
                                .collect(Collectors.toList()))
                        .permissionIds(oldRole.getPermissions()
                                .stream()
                                .map(PermissionEntity::getId)
                                .collect(Collectors.toList()))
                        .build()).getData();
                log.info("apply role:{}", role.getCode());

            } else {
                RoleEntity role = roleApi.insert(RoleCreateDto.builder()
                        .code(value.getId())
                        .name(value.getId())
                        .permissionIds(value.getPermissions()
                                .stream()
                                .map(PermissionEntity::getId)
                                .collect(Collectors.toList()))
                        .menuIds(List.of())
                        .build()).getData();
                log.info("insert role:{}", role.getCode());

            }
        });
    }

    private void scanMethod(
            Class<?> controllerClass,
            Method method,
            RequestMappingInfo mappingInfo,
            Map<String, PermissionEntity> permissions,
            Map<String, RoleEntity> roles) {

        if (!method.isAnnotationPresent(SaCheckPermission.class)) {
            return;
        }

        // Controller路径
        String controllerPath = "";

        RequestMapping requestMapping =
                controllerClass.getAnnotation(RequestMapping.class);

        if (requestMapping != null && requestMapping.value().length > 0) {
            controllerPath = requestMapping.value()[0];
        }

        String controllerName =
                controllerPath.startsWith("/") ? controllerPath.substring(1) : controllerPath;

        // Controller描述
        String controllerDescription = controllerName + "Controller";

        if (controllerClass.isAnnotationPresent(Tag.class)) {
            controllerDescription =
                    controllerClass.getAnnotation(Tag.class).name();
        }
        PermissionEntity parentPermission = PermissionEntity.builder()
                .parentId(null)
                .microService(contextPath)
                .path(controllerPath)
                .id(controllerName)
                .name(controllerName)
                .description(controllerDescription)
                .build();
        permissions.put(parentPermission.getId(), parentPermission);

        // Method描述
        String description = "";

        if (method.isAnnotationPresent(Operation.class)) {
            description = method.getAnnotation(Operation.class).summary();
        }

        // HTTP Method
        String httpMethod = mappingInfo.getMethodsCondition()
                .getMethods()
                .stream()
                .findFirst()
                .map(Enum::name)
                .orElse("ALL");

        // Path
        String path = mappingInfo.getPatternValues()
                .stream()
                .findFirst()
                .orElse("");

        SaCheckPermission saCheckPermission = method.getAnnotation(SaCheckPermission.class);
        // 权限
        String permissionValue = saCheckPermission.value()[0];

        PermissionEntity childPermission = PermissionEntity.builder()
                .microService(contextPath)
                .parentId(parentPermission.getId())
                .id(parentPermission.getId() + permissionValue)
                .name(method.getName())
                .code(permissionValue)
                .path(path)
                .description(description)
                .type(httpMethod)
                .build();

        permissions.put(childPermission.getId(), childPermission);
        // 角色
        if (ObjectUtils.isEmpty(saCheckPermission.orRole())) {
            return;
        }
        for (String roleId : saCheckPermission.orRole()) {
            if (roles.containsKey(roleId)) {
                roles.get(roleId).getPermissions().add(childPermission);
            } else {
                List<PermissionEntity> rolePermissions = new ArrayList<>();
                rolePermissions.add(childPermission);
                roles.put(roleId, RoleEntity.builder()
                        .id(roleId)
                        .permissions(rolePermissions)
                        .build());
            }
        }
    }
}