package com.bgasol.plugin.loadbalancer.config;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static com.bgasol.common.constant.value.SystemConfigValues.NODE_NAME_KEY;

public class NodeNameServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {

    public NodeNameServiceInstanceListSupplier(ServiceInstanceListSupplier delegate) {
        super(delegate);
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return delegate.get();
    }

    @Override
    public Flux<List<ServiceInstance>> get(Request request) {
        return delegate.get(request).map(instances -> filterByNodeName(instances, getNodeName(request.getContext())));
    }

    private String getNodeName(Object requestContext) {
        if (requestContext == null) {
            return null;
        }
        if (!(requestContext instanceof RequestDataContext requestDataContext)) {
            return null;
        }
        RequestData clientRequest = requestDataContext.getClientRequest();
        if (ObjectUtils.isEmpty(clientRequest)) {
            return null;
        }
        HttpHeaders headers = clientRequest.getHeaders();
        if (ObjectUtils.isEmpty(headers)) {
            return null;
        }
        return headers.getFirst(NODE_NAME_KEY);
    }

    private List<ServiceInstance> filterByNodeName(List<ServiceInstance> instances, String nodeName) {
        if (StringUtils.isBlank(nodeName)) {
            return instances;
        }

        for (ServiceInstance serviceInstance : instances) {
            Map<String, String> metadata = serviceInstance.getMetadata();
            if (metadata != null && nodeName.equals(metadata.get(NODE_NAME_KEY))) {
                return List.of(serviceInstance);
            }
        }
        return List.of();
    }
}
