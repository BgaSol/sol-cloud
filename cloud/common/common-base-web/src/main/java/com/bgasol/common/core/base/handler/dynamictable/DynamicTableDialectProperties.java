package com.bgasol.common.core.base.handler.dynamictable;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.database")
public class DynamicTableDialectProperties {
    private String dialect = "postgres";
}
