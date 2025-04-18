package com.bgasol.plugin.satoken.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bgasol.model.system.permission.api.PermissionApi;
import com.bgasol.model.system.permission.entity.PermissionEntity;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ControllerScanner {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final PermissionApi permissionApi;

    private final static String controllerPackage = "com.bgasol.web.**.controller";

    /**
     * 扫描controller类和方法，将权限信息存入数据库
     */
    public void scanController(Class<?> controllerClass) {
        // 只扫描controller的类
        PermissionEntity parentPermissionEntity = new PermissionEntity();
        // 获取当前服务名
        parentPermissionEntity.setMicroService(contextPath);
        // 获取controller的路径
        String annotation = controllerClass.getAnnotation(RequestMapping.class).value()[0];
        parentPermissionEntity.setPath(annotation);
        // 获取controller的名称
        String controllerName = annotation.startsWith("/") ? annotation.substring(1) : annotation;
        parentPermissionEntity.setId(controllerName);
        parentPermissionEntity.setName(controllerName);

        log.info("正在扫描Controller: {}", controllerName);
        // 获取controller的描述
        String controllerDescription = controllerName + "Controller";
        if (controllerClass.isAnnotationPresent(Tag.class)) {
            Tag tag = controllerClass.getAnnotation(Tag.class);
            controllerDescription = tag.name();
        }
        parentPermissionEntity.setDescription(controllerDescription);
        // 开始遍历每一个接口
        List<PermissionEntity> children = new ArrayList<>();
        Method[] methods = controllerClass.getMethods();
        for (Method method : methods) {
            PermissionEntity permissionEntity = new PermissionEntity();
            // 获取当前服务名
            permissionEntity.setMicroService(contextPath);

            permissionEntity.setParentId(parentPermissionEntity.getId());
            // 获取方法名
            permissionEntity.setName(method.getName());
            // 获取请求路径和请求方式
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String path = ArrayUtils.get(requestMapping.value(), 0);
                permissionEntity.setPath(path);
                permissionEntity.setType("ALL");
            } else if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping getMapping = method.getAnnotation(GetMapping.class);
                String path = ArrayUtils.get(getMapping.value(), 0);
                permissionEntity.setPath(path);
                permissionEntity.setType("GET");
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping postMapping = method.getAnnotation(PostMapping.class);
                String path = ArrayUtils.get(postMapping.value(), 0);
                permissionEntity.setPath(path);
                permissionEntity.setType("POST");
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                PutMapping putMapping = method.getAnnotation(PutMapping.class);
                String path = ArrayUtils.get(putMapping.value(), 0);
                permissionEntity.setPath(path);
                permissionEntity.setType("PUT");
            } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
                String path = ArrayUtils.get(deleteMapping.value(), 0);
                permissionEntity.setPath(path);
                permissionEntity.setType("DELETE");
            }
            // 获取方法的描述
            if (method.isAnnotationPresent(Operation.class)) {
                Operation operation = method.getAnnotation(Operation.class);
                permissionEntity.setDescription(operation.summary());
            }
            // 获取方法的权限
            if (method.isAnnotationPresent(SaCheckPermission.class)) {
                String permissionValue = method.getAnnotation(SaCheckPermission.class).value()[0];
                permissionEntity.setId(parentPermissionEntity.getId() + permissionValue);
                permissionEntity.setCode(permissionValue);
                // 插入数据库
                children.add(permissionEntity);
            }
        }
        parentPermissionEntity.setChildren(children);
        permissionApi.init(parentPermissionEntity);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void scanAllControllers() throws ClassNotFoundException {
        log.info("开始扫描所有Controller");
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RequestMapping.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(controllerPackage);
        
        // 重试相关参数
        int maxRetries = 5;
        int retryCount = 0;
        int initialDelay = 3000; // 初始延迟3秒
        boolean success = false;
        
        while (!success && retryCount < maxRetries) {
            try {
                for (BeanDefinition candidateComponent : candidateComponents) {
                    String className = candidateComponent.getBeanClassName();
                    Class<?> clazz = Class.forName(className);
                    scanController(clazz);
                }
                success = true;
                log.info("Controller扫描完成，权限信息已更新");
            } catch (Exception e) {
                retryCount++;
                if (retryCount < maxRetries) {
                    int delay = initialDelay * (1 << (retryCount - 1)); // 指数退避策略
                    log.warn("权限信息更新失败，上一个节点未正确下线，将在{}ms后进行第{}次重试。错误信息: {}", delay, retryCount + 1, e.getMessage());
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("重试等待被中断", ie);
                    }
                } else {
                    log.error("权限信息更新失败，已达到最大重试次数({}次)。错误信息: {}", maxRetries, e.getMessage(), e);
                }
            }
        }
    }
}