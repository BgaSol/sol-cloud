package com.bgasol.common.core.base.handler.dynamictable;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostgresDynamicTableDialectProvider implements DynamicTableDialectProvider {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean supports(DbType dbType) {
        return dbType != null && dbType.postgresqlSameType();
    }

    @Override
    public boolean tableExists(String tableName) {
        return jdbcTemplate.queryForObject(
                "SELECT to_regclass(?) IS NOT NULL",
                Boolean.class,
                tableName
        );
    }

    @Override
    public void ensureTableExists(String sourceTable, String targetTable) {
        String createSql = "CREATE TABLE IF NOT EXISTS %s (LIKE %s INCLUDING ALL)"
                .formatted(targetTable, sourceTable);
        jdbcTemplate.execute(createSql);
    }
}
