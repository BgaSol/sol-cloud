package com.bgasol.common.core.base.handler.dynamictable;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostgresDynamicTableDialectProvider extends AbstractJdbcDynamicTableDialectProvider {
    public PostgresDynamicTableDialectProvider(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public String dialect() {
        return "postgres";
    }

    @Override
    public void ensureTableExists(String sourceTable, String targetTable) {
        String createSql = "CREATE TABLE IF NOT EXISTS %s (LIKE %s INCLUDING ALL)"
                .formatted(targetTable, sourceTable);
        jdbcTemplate.execute(createSql);
    }
}
