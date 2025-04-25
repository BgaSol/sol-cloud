package com.bgasol.gateway.config;

import com.bgasol.common.constant.value.GatewayConfigValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class GatewayConfig {

    private final RouteDefinitionWriter routeDefinitionWriter;

    private final RouteDefinitionLocator routeDefinitionLocator;

    private final DiscoveryClient discoveryClient;

    private final List<String> routeIdList = new ArrayList<>();

    {
        // 不需要注册自己 提前添加进来 GatewayConfigValues.SERVICE_NAME
        routeIdList.add(GatewayConfigValues.SERVICE_NAME);
    }

    public void addRoute(String id, String uri, String path) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(id);
        try {
            definition.setUri(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        PredicateDefinition predicateDefinition = new PredicateDefinition();
        predicateDefinition.setName("Path");
        predicateDefinition.setArgs(Map.of("pattern", path));
        definition.getPredicates().add(predicateDefinition);
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
    }

    public Flux<RouteDefinition> getAllRoutes() {
        return routeDefinitionLocator.getRouteDefinitions();
    }

    @EventListener(InstanceRegisteredEvent.class)
    @Scheduled(cron = "0/5 * * * * ?")
    public void customRouteLocator() {
        for (String service : discoveryClient.getServices()) {
            if (!routeIdList.contains(service)) {
                routeIdList.add(service);
                addRoute(service, "lb://" + service, "/" + service + "/**");
                log.info("添加路由：{}", service);
            }
        }
    }
}
