package com.bgasol.plugin.loadbalancer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.bgasol.common.constant.value.SystemConfigValues.NODE_NAME_KEY;

@Slf4j
@RequiredArgsConstructor
public abstract class HeaderRoutingLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final DiscoveryClient discoveryClient;

    abstract String serviceName();

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        return Mono.defer(() -> {

            if (!(request.getContext() instanceof RequestDataContext context)) {
                return Mono.empty();
            }

            RequestData clientRequest = context.getClientRequest();
            if (clientRequest == null) {
                return Mono.empty();
            }

            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName());

            if (instances.isEmpty()) {
                log.warn("没有找到服务实例: {}", serviceName());
                return Mono.empty();
            }

            String nodeName = clientRequest.getHeaders().getFirst(NODE_NAME_KEY);
            log.info("service: {}, nodeName: {}", serviceName(), nodeName);

            if (StringUtils.isBlank(nodeName)) {
                return randomSelect(instances);
            }

            for (ServiceInstance instance : instances) {
                if (nodeName.equals(instance.getMetadata().get(NODE_NAME_KEY))) {
                    return Mono.just(new DefaultResponse(instance));
                }
            }

            return Mono.empty();
        });
    }

    private Mono<Response<ServiceInstance>> randomSelect(List<ServiceInstance> instances) {
        int index = ThreadLocalRandom.current().nextInt(instances.size());
        return Mono.just(new DefaultResponse(instances.get(index)));
    }
}