package com.bgasol.plugin.loadbalancer.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

public class BaseLoadBalancerClientConfiguration {

    @Bean
    @ConditionalOnBean(ReactiveDiscoveryClient.class)
    @ConditionalOnMissingBean
    public ServiceInstanceListSupplier reactiveBaseServiceInstanceListSupplier(ConfigurableApplicationContext context) {
        return ServiceInstanceListSupplier.builder()
                .withDiscoveryClient()
                .withCaching()
                .with((builderContext, delegate) -> new NodeNameServiceInstanceListSupplier(delegate))
                .withHints()
                .build(context);
    }

    @Bean
    @ConditionalOnBean(DiscoveryClient.class)
    @ConditionalOnMissingBean
    public ServiceInstanceListSupplier blockingBaseServiceInstanceListSupplier(ConfigurableApplicationContext context) {
        return ServiceInstanceListSupplier.builder()
                .withBlockingDiscoveryClient()
                .withCaching()
                .with((builderContext, delegate) -> new NodeNameServiceInstanceListSupplier(delegate))
                .withHints()
                .build(context);
    }
}
