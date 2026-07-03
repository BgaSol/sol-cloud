package com.bgasol.gateway.config;

import com.bgasol.common.constant.value.GatewayConfigValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class GatewayConfig {
    private static final String SERVICE_VALUE_PACKAGE = "com.bgasol.common.constant.value";
    private static final String SERVICE_NAME_FIELD = "SERVICE_NAME";
    private static final String SERVICE_ROUTE_ID_PREFIX = "dynamic-service-";
    private static final String INSTANCE_ROUTE_ID_PREFIX = "dynamic-instance-";
    private static final String ROUTE_URI_PREFIX = "lb://";
    private static final String ROUTE_PATH_SUFFIX = "/**";
    private static final Duration ROUTE_WRITE_TIMEOUT = Duration.ofSeconds(10);
    private static final int SERVICE_ROUTE_ORDER = 0;
    private static final int INSTANCE_ROUTE_ORDER = -100;
    private static final Set<String> ROUTABLE_SERVICE_NAMES = loadRoutableServiceNames();

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final DiscoveryClient discoveryClient;
    private final ApplicationEventPublisher eventPublisher;

    // 使用线程安全的Set来记录已注册的服务
    private final Set<String> registeredServices = ConcurrentHashMap.newKeySet();
    private final Set<String> registeredInstanceRoutes = ConcurrentHashMap.newKeySet();

    /**
     * 应用启动完成后同步一次，避免只依赖定时任务的初始延迟。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        syncRoutes();
    }

    /**
     * 服务注册事件监听器
     */
    @EventListener(InstanceRegisteredEvent.class)
    public void onInstanceRegistered() {
        syncRoutes();
    }

    /**
     * 定时同步路由（作为兜底机制）
     */
    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    public void scheduledSyncRoutes() {
        log.debug("定时同步路由任务执行");
        syncRoutes();
    }

    /**
     * 同步路由逻辑
     */
    private synchronized void syncRoutes() {
        DiscoverySnapshot discoverySnapshot;
        try {
            discoverySnapshot = discoverOnlineRoutes();
        } catch (Exception e) {
            log.error("获取注册中心服务列表失败，跳过本次路由同步", e);
            return;
        }

        RouteSnapshot routeSnapshot = currentRouteSnapshot();
        registeredServices.clear();
        registeredServices.addAll(routeSnapshot.services());
        registeredInstanceRoutes.clear();
        registeredInstanceRoutes.addAll(routeSnapshot.instanceRouteIds());

        Set<String> servicesToRegister = difference(discoverySnapshot.services(), routeSnapshot.services());
        Set<String> servicesToRemove = difference(routeSnapshot.services(), discoverySnapshot.services());
        Set<String> instanceRoutesToRegister = difference(discoverySnapshot.instanceRoutes().keySet(), routeSnapshot.instanceRouteIds());
        Set<String> instanceRoutesToUpdate = changedInstanceRoutes(discoverySnapshot.instanceRoutes(), routeSnapshot.instanceRouteUris());
        Set<String> instanceRoutesToRemove = difference(routeSnapshot.instanceRouteIds(), discoverySnapshot.instanceRoutes().keySet());
        if (servicesToRegister.isEmpty()
                && servicesToRemove.isEmpty()
                && instanceRoutesToRegister.isEmpty()
                && instanceRoutesToUpdate.isEmpty()
                && instanceRoutesToRemove.isEmpty()) {
            log.debug("网关动态路由已是最新：services={}, instanceRoutes={}", registeredServices, registeredInstanceRoutes);
            return;
        }

        boolean changed = false;
        Set<String> registeredServicesLog = new LinkedHashSet<>();
        for (String service : servicesToRegister) {
            if (registerServiceRoute(service)) {
                registeredServices.add(service);
                registeredServicesLog.add(service);
                changed = true;
            }
        }

        Set<String> registeredInstanceRoutesLog = new LinkedHashSet<>();
        for (String routeId : instanceRoutesToRegister) {
            InstanceRoute instanceRoute = discoverySnapshot.instanceRoutes().get(routeId);
            if (instanceRoute != null && registerInstanceRoute(instanceRoute)) {
                registeredInstanceRoutes.add(routeId);
                registeredInstanceRoutesLog.add(routeId);
                changed = true;
            }
        }

        Set<String> updatedInstanceRoutesLog = new LinkedHashSet<>();
        for (String routeId : instanceRoutesToUpdate) {
            InstanceRoute instanceRoute = discoverySnapshot.instanceRoutes().get(routeId);
            if (instanceRoute != null && registerInstanceRoute(instanceRoute)) {
                updatedInstanceRoutesLog.add(routeId);
                changed = true;
            }
        }

        Set<String> removedServicesLog = new LinkedHashSet<>();
        for (String service : servicesToRemove) {
            if (removeServiceRoute(service)) {
                registeredServices.remove(service);
                removedServicesLog.add(service);
                changed = true;
            }
        }

        Set<String> removedInstanceRoutesLog = new LinkedHashSet<>();
        for (String routeId : instanceRoutesToRemove) {
            if (removeRoute(routeId, "实例直连")) {
                registeredInstanceRoutes.remove(routeId);
                removedInstanceRoutesLog.add(routeId);
                changed = true;
            }
        }

        if (changed) {
            eventPublisher.publishEvent(new RefreshRoutesEvent(this));
        }

        log.info("网关动态路由同步完成：onlineServices={}, serviceRoutes={}, instanceRoutes={}, "
                        + "addedServices={}, removedServices={}, addedInstanceRoutes={}, "
                        + "updatedInstanceRoutes={}, removedInstanceRoutes={}",
                discoverySnapshot.services(), registeredServices, registeredInstanceRoutes,
                registeredServicesLog, removedServicesLog, registeredInstanceRoutesLog,
                updatedInstanceRoutesLog, removedInstanceRoutesLog);
    }

    private DiscoverySnapshot discoverOnlineRoutes() {
        Set<String> onlineServices = new LinkedHashSet<>();
        Map<String, InstanceRoute> onlineInstanceRoutes = new LinkedHashMap<>();

        for (String service : discoveryClient.getServices()) {
            if (!isRoutableService(service)) {
                continue;
            }

            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            if (instances.isEmpty()) {
                log.debug("服务没有在线实例，跳过路由注册：{}", service);
                continue;
            }

            onlineServices.add(service);
            for (ServiceInstance instance : instances) {
                InstanceRoute instanceRoute = createInstanceRoute(service, instance);
                InstanceRoute previous = onlineInstanceRoutes.put(instanceRoute.routeId(), instanceRoute);
                if (previous != null) {
                    log.warn("发现重复实例路由ID，使用最新实例信息：routeId={}, oldUri={}, newUri={}",
                            instanceRoute.routeId(), previous.uri(), instanceRoute.uri());
                }
            }
        }

        log.debug("注册中心在线可路由服务：services={}, instanceRoutes={}", onlineServices, onlineInstanceRoutes.keySet());
        return new DiscoverySnapshot(onlineServices, onlineInstanceRoutes);
    }

    private RouteSnapshot currentRouteSnapshot() {
        try {
            List<RouteDefinition> routeDefinitions = routeDefinitionLocator.getRouteDefinitions()
                    .collectList()
                    .block(ROUTE_WRITE_TIMEOUT);
            if (routeDefinitions == null) {
                return new RouteSnapshot(Set.of(), Set.of(), Map.of());
            }

            Set<String> services = routeDefinitions.stream()
                    .map(RouteDefinition::getId)
                    .filter(routeId -> routeId != null && routeId.startsWith(SERVICE_ROUTE_ID_PREFIX))
                    .map(this::serviceNameFromRouteId)
                    .filter(this::isRoutableService)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            Set<String> instanceRouteIds = routeDefinitions.stream()
                    .map(RouteDefinition::getId)
                    .filter(routeId -> routeId != null && routeId.startsWith(INSTANCE_ROUTE_ID_PREFIX))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            Map<String, String> instanceRouteUris = routeDefinitions.stream()
                    .filter(routeDefinition -> routeDefinition.getId() != null
                            && routeDefinition.getId().startsWith(INSTANCE_ROUTE_ID_PREFIX))
                    .collect(Collectors.toMap(
                            RouteDefinition::getId,
                            routeDefinition -> routeDefinition.getUri().toString(),
                            (oldValue, newValue) -> newValue,
                            LinkedHashMap::new
                    ));

            return new RouteSnapshot(services, instanceRouteIds, instanceRouteUris);
        } catch (Exception e) {
            log.warn("读取当前网关路由失败，使用本地缓存继续同步", e);
            return new RouteSnapshot(Set.copyOf(registeredServices), Set.copyOf(registeredInstanceRoutes), Map.of());
        }
    }

    private boolean isRoutableService(String service) {
        return service != null && ROUTABLE_SERVICE_NAMES.contains(service);
    }

    private Set<String> difference(Set<String> source, Set<String> target) {
        return source.stream()
                .filter(service -> !target.contains(service))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> changedInstanceRoutes(Map<String, InstanceRoute> onlineRoutes, Map<String, String> currentRouteUris) {
        return onlineRoutes.entrySet().stream()
                .filter(entry -> {
                    String currentUri = currentRouteUris.get(entry.getKey());
                    return currentUri != null && !currentUri.equals(entry.getValue().uri());
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean registerServiceRoute(String service) {
        String routeId = routeIdForService(service);
        String routeUri = ROUTE_URI_PREFIX + service;
        String routePath = "/" + service + ROUTE_PATH_SUFFIX;

        try {
            RouteDefinition definition = createRouteDefinition(routeId, routeUri, routePath, SERVICE_ROUTE_ORDER);
            routeDefinitionWriter.save(Mono.just(definition)).block(ROUTE_WRITE_TIMEOUT);
            log.info("注册服务路由成功：service={}, routeId={}, uri={}, path={}", service, routeId, routeUri, routePath);
            return true;
        } catch (Exception e) {
            log.error("注册服务路由失败：service={}, routeId={}, uri={}, path={}", service, routeId, routeUri, routePath, e);
            return false;
        }
    }

    private boolean registerInstanceRoute(InstanceRoute instanceRoute) {
        try {
            RouteDefinition definition = createRouteDefinition(
                    instanceRoute.routeId(),
                    instanceRoute.uri(),
                    instanceRoute.path(),
                    INSTANCE_ROUTE_ORDER
            );
            definition.getFilters().add(createInstanceRewritePathFilter(instanceRoute.instanceId(), instanceRoute.service()));
            routeDefinitionWriter.save(Mono.just(definition)).block(ROUTE_WRITE_TIMEOUT);
            log.info("注册实例直连路由成功：service={}, instanceId={}, routeId={}, uri={}, path={}",
                    instanceRoute.service(), instanceRoute.instanceId(), instanceRoute.routeId(),
                    instanceRoute.uri(), instanceRoute.path());
            return true;
        } catch (Exception e) {
            log.error("注册实例直连路由失败：service={}, instanceId={}, routeId={}, uri={}, path={}",
                    instanceRoute.service(), instanceRoute.instanceId(), instanceRoute.routeId(),
                    instanceRoute.uri(), instanceRoute.path(), e);
            return false;
        }
    }

    private RouteDefinition createRouteDefinition(String id, String uri, String path, int order) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(id);
        definition.setUri(URI.create(uri));
        definition.setOrder(order);
        definition.getPredicates().add(new PredicateDefinition("Path=" + path));
        return definition;
    }

    private FilterDefinition createInstanceRewritePathFilter(String instanceId, String service) {
        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setName("RewritePath");
        filterDefinition.setArgs(Map.of(
                "regexp", "^/" + Pattern.quote(instanceId) + "/?(?<remaining>.*)",
                "replacement", "/" + service + "/${remaining}"
        ));
        return filterDefinition;
    }

    private boolean removeServiceRoute(String service) {
        String routeId = routeIdForService(service);
        return removeRoute(routeId, "服务");
    }

    private boolean removeRoute(String routeId, String routeType) {
        try {
            routeDefinitionWriter.delete(Mono.just(routeId)).block(ROUTE_WRITE_TIMEOUT);
            log.info("移除{}路由成功：routeId={}", routeType, routeId);
            return true;
        } catch (NotFoundException e) {
            log.warn("{}路由已不存在，清理本地状态：routeId={}", routeType, routeId);
            return true;
        } catch (Exception e) {
            log.error("移除{}路由失败：routeId={}", routeType, routeId, e);
            return false;
        }
    }

    private InstanceRoute createInstanceRoute(String service, ServiceInstance instance) {
        String instanceId = instanceId(instance);
        return new InstanceRoute(
                service,
                instanceId,
                routeIdForInstance(instanceId),
                uriForInstance(instance),
                "/" + instanceId + ROUTE_PATH_SUFFIX
        );
    }

    private String instanceId(ServiceInstance instance) {
        String instanceId = instance.getInstanceId();
        if (instanceId != null && !instanceId.isBlank()) {
            return instanceId;
        }
        return instance.getServiceId() + "-" + instance.getHost() + "-" + instance.getPort();
    }

    private String uriForInstance(ServiceInstance instance) {
        URI uri = instance.getUri();
        if (uri != null && uri.getScheme() != null) {
            return uri.toString();
        }
        String scheme = instance.isSecure() ? "https" : "http";
        return scheme + "://" + instance.getHost() + ":" + instance.getPort();
    }

    private String routeIdForService(String service) {
        return SERVICE_ROUTE_ID_PREFIX + service;
    }

    private String routeIdForInstance(String instanceId) {
        return INSTANCE_ROUTE_ID_PREFIX + instanceId;
    }

    private String serviceNameFromRouteId(String routeId) {
        return routeId.substring(SERVICE_ROUTE_ID_PREFIX.length());
    }

    private record DiscoverySnapshot(Set<String> services, Map<String, InstanceRoute> instanceRoutes) {
    }

    private record RouteSnapshot(Set<String> services, Set<String> instanceRouteIds, Map<String, String> instanceRouteUris) {
    }

    private record InstanceRoute(String service, String instanceId, String routeId, String uri, String path) {
    }

    private static Set<String> loadRoutableServiceNames() {
        Set<String> serviceNames = new LinkedHashSet<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);

        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(SERVICE_VALUE_PACKAGE)) {
            String className = beanDefinition.getBeanClassName();
            if (className == null) {
                continue;
            }
            try {
                Class<?> clazz = Class.forName(className);
                Field field = clazz.getDeclaredField(SERVICE_NAME_FIELD);
                if (field.getType() != String.class || !Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                String serviceName = (String) field.get(null);
                if (serviceName != null && !serviceName.isBlank()
                        && !serviceName.equals(GatewayConfigValues.SERVICE_NAME)) {
                    serviceNames.add(serviceName);
                }
            } catch (NoSuchFieldException ignored) {
            } catch (Exception e) {
                log.warn("读取服务名常量失败：class={}, field={}", className, SERVICE_NAME_FIELD, e);
            }
        }

        if (serviceNames.isEmpty()) {
            log.warn("未加载到可路由服务名常量，网关不会自动注册服务路由");
        } else {
            log.info("加载可路由服务名常量：{}", serviceNames);
        }
        return Set.copyOf(serviceNames);
    }
}
