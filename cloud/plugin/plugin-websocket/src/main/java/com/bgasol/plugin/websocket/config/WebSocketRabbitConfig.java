package com.bgasol.plugin.websocket.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.bgasol.common.util.WSUtils.GetWSTopic;

@Configuration
public class WebSocketRabbitConfig {

    public static final String WEBSOCKET_EXCHANGE_NAME = "webSocketExchange";
    public static final String WEBSOCKET_QUEUE_NAME = "webSocketQueue";

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean(WEBSOCKET_EXCHANGE_NAME)
    public FanoutExchange webSocketExchange() {
        return ExchangeBuilder.fanoutExchange(GetWSTopic(serviceName))
                .durable(true)
                .build();
    }

    @Bean(WEBSOCKET_QUEUE_NAME)
    public Queue webSocketQueue() {
        return new AnonymousQueue();
    }

    @Bean
    public Binding webSocketBinding(
            @Qualifier(WEBSOCKET_QUEUE_NAME) Queue webSocketQueue,
            @Qualifier(WEBSOCKET_EXCHANGE_NAME) FanoutExchange webSocketExchange
    ) {
        return BindingBuilder
                .bind(webSocketQueue)
                .to(webSocketExchange);
    }
}
