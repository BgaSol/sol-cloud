package com.bgasol.common.core.base.handler.dynamictable;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class RoutingDynamicTableDialectManager implements DynamicTableDialectManager {
    private final List<DynamicTableDialectProvider> providers;
    private final DynamicTableDialectProperties dynamicTableDialectProperties;

    @Override
    public boolean tableExists(String tableName) {
        return current().tableExists(tableName);
    }

    @Override
    public void ensureTableExists(String sourceTable, String targetTable) {
        current().ensureTableExists(sourceTable, targetTable);
    }

    private DynamicTableDialectProvider current() {
        String currentDialect = normalize(dynamicTableDialectProperties.getDialect());
        return providers.stream()
                .filter(provider -> normalize(provider.dialect()).equals(currentDialect))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("未找到数据库方言支持实现: " + currentDialect));
    }

    private String normalize(String dialect) {
        return dialect == null ? "" : dialect.trim().toLowerCase(Locale.ROOT);
    }
}
