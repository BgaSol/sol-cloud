package com.bgasol.plugin.loadbalancer.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

@LoadBalancerClients(defaultConfiguration = BaseLoadBalancerClientConfiguration.class)
public class LoadBalancerConfiguration {

}
