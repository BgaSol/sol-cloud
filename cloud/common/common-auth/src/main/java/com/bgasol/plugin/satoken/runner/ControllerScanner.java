package com.bgasol.plugin.satoken.runner;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.model.system.permission.api.PermissionApi;
import com.bgasol.model.system.permission.dto.PermissionCreateDto;
import com.bgasol.model.system.permission.dto.PermissionUpdateDto;
import com.bgasol.model.system.permission.entity.PermissionEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class ControllerScanner {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final PermissionApi permissionApi;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @EventListener(InstanceRegisteredEvent.class)
    public void scanControllers() throws InterruptedException {

        Thread.sleep(TimeUnit.SECONDS.toMillis(5));

        log.info("Scanning controllers...");

        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {

            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            Class<?> controllerClass = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();

            scanMethod(controllerClass, method, mappingInfo);
        }
    }

    private void scanMethod(
            Class<?> controllerClass,
            Method method,
            RequestMappingInfo mappingInfo) {

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

        PermissionEntity parentPermission =
                permissionApi.findById(controllerName, false).getData();

        if (ObjectUtils.isEmpty(parentPermission)) {

            parentPermission = permissionApi.insert(
                    PermissionCreateDto.builder()
                            .parentId(null)
                            .microService(contextPath)
                            .path(controllerPath)
                            .id(controllerName)
                            .name(controllerName)
                            .description(controllerDescription)
                            .build()
            ).getData();

            log.info("insert parent permission:{}", parentPermission.getName());

        } else {

            parentPermission = permissionApi.apply(
                    PermissionUpdateDto.builder()
                            .microService(contextPath)
                            .path(controllerPath)
                            .id(controllerName)
                            .name(controllerName)
                            .description(controllerDescription)
                            .build()
            ).getData();

            log.info("apply parent permission:{}", parentPermission.getName());
        }

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

        // 权限
        String permissionValue =
                method.getAnnotation(SaCheckPermission.class).value()[0];

        String id = parentPermission.getId() + permissionValue;

        PermissionEntity permission =
                permissionApi.findById(id, false).getData();

        if (ObjectUtils.isEmpty(permission)) {

            permission = permissionApi.insert(
                    PermissionCreateDto.builder()
                            .microService(contextPath)
                            .parentId(parentPermission.getId())
                            .id(id)
                            .name(method.getName())
                            .code(permissionValue)
                            .path(path)
                            .description(description)
                            .type(httpMethod)
                            .build()
            ).getData();

            log.info("insert children permission:{}", permission.getName());

        } else {

            permission = permissionApi.apply(
                    PermissionUpdateDto.builder()
                            .microService(contextPath)
                            .parentId(parentPermission.getId())
                            .id(id)
                            .name(method.getName())
                            .code(permissionValue)
                            .path(path)
                            .description(description)
                            .type(httpMethod)
                            .build()
            ).getData();

            log.info("apply children permission:{}", permission.getName());
        }
    }
}