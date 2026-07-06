package com.bgasol.common.core.base.scheduled;

import com.bgasol.common.core.base.model.NodeConfig;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistryRabbitConfig {

    public static final String SYSTEM_SERVICE_REGISTRY = "system.service.registry";

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${system.node.name}")
    private String nodeName;

    @Bean("serviceRegistryExchange")
    public FanoutExchange serviceRegistryExchange() {
        return ExchangeBuilder.fanoutExchange(SYSTEM_SERVICE_REGISTRY)
                .durable(true)
                .build();
    }

    @Bean("serviceRegistryQueue")
    public Queue serviceRegistryQueue(NodeConfig nodeConfig) {
        String queueName = new Base64UrlNamingStrategy(
                nodeConfig.getName() + "." + nodeConfig.getAppName() + "." + SYSTEM_SERVICE_REGISTRY + "."
        ).generateName();
        return new Queue(queueName, false, true, true);
    }

    @Bean("serviceRegistryBinding")
    public Binding serviceRegistryBinding(Queue serviceRegistryQueue, FanoutExchange serviceRegistryExchange) {
        return BindingBuilder
                .bind(serviceRegistryQueue)
                .to(serviceRegistryExchange);
    }
}
