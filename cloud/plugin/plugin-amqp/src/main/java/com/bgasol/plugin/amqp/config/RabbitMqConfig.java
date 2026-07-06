package com.bgasol.plugin.amqp.config;

import com.bgasol.common.core.base.model.NodeConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    @Bean
    @ConditionalOnMissingBean(ConnectionNameStrategy.class)
    public ConnectionNameStrategy connectionNameStrategy(Environment environment, NodeConfig nodeConfig) {
        AtomicInteger connectionIndex = new AtomicInteger();
        return connectionFactory ->
                String.format("%s.%s.amqp-%d", nodeConfig.getAppName(), nodeConfig.getName(), connectionIndex.incrementAndGet());
    }

    @Bean
    @ConditionalOnMissingBean(MessageConverter.class)
    public MessageConverter jackson2JsonMessageConverter(@NonNull ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
