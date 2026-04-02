package com.bgasol.common.buildinfo;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class BuildInfoEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final ZoneId SHANGHAI_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String PROPERTY_SOURCE_NAME = "buildInfoDisplayProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("build.time.display", formatTime(environment.getProperty("build.time")));
        properties.put("git.commit.time.display", formatTime(environment.getProperty("git.commit.time")));
        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private String formatTime(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return "unknown";
        }

        try {
            return FORMATTER.format(OffsetDateTime.parse(rawValue).atZoneSameInstant(SHANGHAI_ZONE));
        } catch (Exception ignored) {
            try {
                return FORMATTER.format(Instant.parse(rawValue).atZone(SHANGHAI_ZONE));
            } catch (Exception ex) {
                return rawValue;
            }
        }
    }
}
