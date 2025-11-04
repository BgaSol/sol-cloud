package com.bgasol.plugin.micrometer.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.CoreConstants;
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

    @Value("${spring.application.name}")
    private String serviceName;

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        Loki4jAppender lokiAppender = new Loki4jAppender();
        lokiAppender.setName("LOKI");
        lokiAppender.setContext(context);
        lokiAppender.setMetricsEnabled(true);
        lokiAppender.setReadMarkers(false);
        lokiAppender.setVerbose(false);

        // 标签：只使用低基数的静态标签（官方推荐）
        lokiAppender.setLabels(String.format(
                "app=%s\nhost=%s",
                serviceName,
                context.getProperty(CoreConstants.HOSTNAME_KEY)
        ));

        // 结构化元数据：包含 trace_id 和 span_id，支持 Tempo 关联
        lokiAppender.setStructuredMetadata(
                "level=%level\n" +
                        "thread=%thread\n" +
                        "logger=%logger\n" +
                        "trace_id=%mdc{traceId}\n" +
                        "span_id=%mdc{spanId}\n" +
                        "*=%%mdc\n" +
                        "*=%%kvp"
        );

        // 日志消息格式：包含 traceId、spanId 和完整异常堆栈
        PatternLayout messageLayout = new PatternLayout();
        messageLayout.setContext(context);
        messageLayout.setPattern(
                "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} " +
                        "[traceId=%X{traceId:-}, spanId=%X{spanId:-}] - %msg%n%ex{full}"
        );
        messageLayout.setOutputPatternAsHeader(false);
        messageLayout.start();
        lokiAppender.setMessage(messageLayout);

        // HTTP 配置
        PipelineConfigAppenderBase.HttpCfg httpCfg = new Loki4jAppender.HttpCfg();
        httpCfg.setUrl(logEndpoint);
        httpCfg.setConnectionTimeoutMs(10000);
        httpCfg.setRequestTimeoutMs(60000);
        lokiAppender.setHttp(httpCfg);

        // 批处理配置：防止日志被截断（关键配置）
        try {
            // 官方文档参数: maxItems, maxBytes, timeoutMs
            PipelineConfigAppenderBase.BatchCfg batchCfg = new Loki4jAppender.BatchCfg();
            batchCfg.setMaxItems(1000);
            batchCfg.setMaxBytes(4194304); // 4MB，足够容纳完整堆栈
            batchCfg.setTimeoutMs(1000);
            lokiAppender.setBatch(batchCfg);
        } catch (Exception e) {
            // 如果 BatchCfg API 不存在，使用默认配置并记录警告
            System.err.println("Warning: Cannot configure batch settings, using defaults. " + e.getMessage());
        }

        lokiAppender.start();

        Logger rootLogger = context.getLogger("ROOT");
        rootLogger.addAppender(lokiAppender);
    }
}