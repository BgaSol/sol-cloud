package com.bgasol.plugin.micrometer.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import com.github.loki4j.logback.Loki4jAppender;
import com.github.loki4j.logback.PipelineConfigAppenderBase;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class LokiAppenderConfig implements ApplicationListener<ApplicationStartedEvent> {
    @Value("${management.logging.endpoint}")
    private String logEndpoint;

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        Loki4jAppender lokiAppender = new Loki4jAppender();
        lokiAppender.setName("LOKI");
        lokiAppender.setContext(context);
        {
            // 使用 PatternLayout 设置日志格式
            PatternLayout layout = new PatternLayout();
            layout.setContext(context);
            layout.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{traceId:-}] - %msg%n%throwable");
            layout.start();
            lokiAppender.setMessage(layout);
        }
        {
            // 设置 HTTP 配置
            PipelineConfigAppenderBase.HttpCfg httpCfg = new Loki4jAppender.HttpCfg();
            httpCfg.setUrl(logEndpoint);
            lokiAppender.setHttp(httpCfg);
        }
        lokiAppender.start();

        Logger rootLogger = context.getLogger("ROOT");
        rootLogger.addAppender(lokiAppender);

    }
}