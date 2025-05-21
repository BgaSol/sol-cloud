package com.bgasol.plugin.openfeign.config;

import feign.FeignException;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static feign.FeignException.errorStatus;

@Slf4j
@Configuration
public class OpenFeignConfig {

    @Bean
    public Retryer retrier() {
        return new Retryer.Default(100L, 300L, 3);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            FeignException exception = errorStatus(methodKey, response);
            if (status == 503) {
                return new RetryableException(
                        status,
                        "远程调用服务暂时不可用，将进行重试",
                        response.request().httpMethod(),
                        exception,
                        (Long) null,
                        response.request());
            }
            // 默认方式处理
            return exception;
        };
    }

}