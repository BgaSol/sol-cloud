package com.bgasol.plugin.websocket.config;

import com.bgasol.common.core.base.model.NodeConfig;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.bgasol.common.util.WSUtils.GetWSTopic;

@Configuration
public class WebSocketRabbitConfig {

    @Bean("webSocketExchange")
    public FanoutExchange webSocketExchange(NodeConfig nodeConfig) {
        return ExchangeBuilder.fanoutExchange(GetWSTopic(nodeConfig.getAppName()))
                .durable(true)
                .build();
    }

    @Bean("webSocketQueue")
    public Queue webSocketQueue(NodeConfig nodeConfig) {
        String queueName = new Base64UrlNamingStrategy(
                nodeConfig.getName() + "." + nodeConfig.getAppName() + "." + GetWSTopic(nodeConfig.getAppName()) + "."
        ).generateName();
        return new Queue(queueName, false, true, true);
    }

    @Bean("webSocketBinding")
    public Binding webSocketBinding(Queue webSocketQueue, FanoutExchange webSocketExchange) {
        return BindingBuilder
                .bind(webSocketQueue)
                .to(webSocketExchange);
    }
}
