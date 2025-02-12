package com.bgasol.gateway.config;

import com.bgasol.common.constant.config.ClientServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
public class GatewayConfig {

    private static final String BASE_SERVICE_NAME_PACKAGE = "com.bgasol.common.constant.value";
    private static final String BASE_SERVICE_NAME_CONST = "SERVICE_NAME";

    private List<String> serviceNames;

    /**
     * 在应用启动时初始化服务名称列表，避免每次请求都扫描类路径。
     */
    public GatewayConfig() {
        try {
            this.serviceNames = getServiceNames();
            log.info("Successfully loaded service names: {}", serviceNames);
        } catch (Exception e) {
            log.error("Error loading service names", e);
            this.serviceNames = new ArrayList<>();
        }
    }

    /**
     * 获取所有标注了 @ClientServer 注解的类的 SERVICE_NAME 字段值
     *
     * @return 服务名称列表
     * @throws ClassNotFoundException 如果类无法找到
     * @throws NoSuchFieldException   如果找不到字段
     * @throws IllegalAccessException 如果无法访问字段
     */
    private List<String> getServiceNames() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        List<String> serviceNameList = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ClientServer.class));

        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(BASE_SERVICE_NAME_PACKAGE);
        for (BeanDefinition candidateComponent : candidateComponents) {
            String className = candidateComponent.getBeanClassName();
            serviceNameList.add(getServiceNameFromClass(className));
        }
        return serviceNameList;
    }

    /**
     * 从类中获取 SERVICE_NAME 字段值
     *
     * @param className 类的全名
     * @return 服务名称
     * @throws ClassNotFoundException 如果类无法找到
     * @throws NoSuchFieldException   如果找不到字段
     * @throws IllegalAccessException 如果无法访问字段
     */
    private String getServiceNameFromClass(String className) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = Class.forName(className);
        Field field = clazz.getField(BASE_SERVICE_NAME_CONST);
        return (String) field.get(clazz);
    }

    /**
     * 根据服务名称生成路由
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();

        if (serviceNames.isEmpty()) {
            log.warn("No service names found, skipping route creation.");
            return routes.build();
        }

        // 通过服务名，批量生成路由
        for (String serviceName : serviceNames) {
            routes.route(serviceName, r -> r.path("/" + serviceName + "/**")
                    .uri("lb://" + serviceName));
            log.debug("Route added for service: {}", serviceName);
        }

        return routes.build();
    }
}
