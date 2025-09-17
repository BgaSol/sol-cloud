package com.bgasol.gateway.config;

import com.bgasol.common.constant.value.GatewayConfigValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class GatewayConfig {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final DiscoveryClient discoveryClient;
    private final ApplicationEventPublisher eventPublisher;

    // 使用线程安全的Set来记录已注册的服务
    private final Set<String> registeredServices = ConcurrentHashMap.newKeySet();

    {
        // 预先添加网关自身服务，避免自己注册自己
        registeredServices.add(GatewayConfigValues.SERVICE_NAME);
    }

    /**
     * 添加路由定义
     */
    public void addRoute(String id, String uri, String path) {
        try {
            RouteDefinition definition = createRouteDefinition(id, uri, path);

            routeDefinitionWriter.save(Mono.just(definition))
                    .doOnSuccess(unused -> {
                        log.info("成功添加路由：id={}, uri={}, path={}", id, uri, path);
                        // 发布路由刷新事件
                        eventPublisher.publishEvent(new RefreshRoutesEvent(this));
                    })
                    .doOnError(error -> log.error("添加路由失败：id={}, error={}", id, error.getMessage()))
                    .subscribe();

        } catch (Exception e) {
            log.error("创建路由定义失败：id={}, uri={}, path={}, error={}", id, uri, path, e.getMessage());
        }
    }

    /**
     * 创建路由定义
     */
    private RouteDefinition createRouteDefinition(String id, String uri, String path) throws URISyntaxException {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(id);
        definition.setUri(new URI(uri));

        PredicateDefinition predicateDefinition = new PredicateDefinition();
        predicateDefinition.setName("Path");
        predicateDefinition.setArgs(Map.of("pattern", path));
        definition.getPredicates().add(predicateDefinition);

        return definition;
    }

    /**
     * 服务注册事件监听器
     */
    @EventListener(InstanceRegisteredEvent.class)
    public void onInstanceRegistered(InstanceRegisteredEvent<?> event) {
        log.debug("接收到服务注册事件：{}", event.getConfig());
        syncRoutes();
    }

    /**
     * 定时同步路由（作为兜底机制）
     */
    @Scheduled(fixedDelay = 30000, initialDelay = 10000) // 30秒执行一次，初始延迟10秒
    public void scheduledSyncRoutes() {
        log.debug("定时同步路由任务执行");
        syncRoutes();
    }

    /**
     * 同步路由逻辑
     */
    private synchronized void syncRoutes() {
        try {
            Set<String> currentServices = Set.copyOf(discoveryClient.getServices());
            log.debug("当前发现的服务：{}", currentServices);

            for (String service : currentServices) {
                if (!registeredServices.contains(service)) {
                    registerServiceRoute(service);
                }
            }

            // 可选：检查已注册的服务是否还存在，如果不存在则移除路由
            checkAndRemoveInactiveRoutes(currentServices);

        } catch (Exception e) {
            log.error("同步路由时发生异常", e);
        }
    }

    /**
     * 注册服务路由
     */
    private void registerServiceRoute(String service) {
        try {
            String routeUri = "lb://" + service;
            String routePath = "/" + service + "/**";

            addRoute(service, routeUri, routePath);
            registeredServices.add(service);
            log.info("新增服务路由：service={}, uri={}, path={}", service, routeUri, routePath);

        } catch (Exception e) {
            log.error("注册服务路由失败：service={}, error={}", service, e.getMessage());
        }
    }

    /**
     * 检查并移除不活跃的路由（可选功能）
     */
    private void checkAndRemoveInactiveRoutes(Set<String> currentServices) {
        Set<String> inactiveServices = registeredServices.stream()
                .filter(service -> !currentServices.contains(service))
                .filter(service -> !GatewayConfigValues.SERVICE_NAME.equals(service))
                .collect(java.util.stream.Collectors.toSet());

        for (String inactiveService : inactiveServices) {
            removeRoute(inactiveService);
            registeredServices.remove(inactiveService);
            log.info("移除不活跃服务路由：{}", inactiveService);
        }
    }

    /**
     * 移除路由
     */
    private void removeRoute(String routeId) {
        routeDefinitionWriter.delete(Mono.just(routeId))
                .doOnSuccess(unused -> {
                    log.info("成功移除路由：{}", routeId);
                    eventPublisher.publishEvent(new RefreshRoutesEvent(this));
                })
                .doOnError(error -> log.error("移除路由失败：routeId={}, error={}", routeId, error.getMessage()))
                .subscribe();
    }
}