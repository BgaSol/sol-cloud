package com.bgasol.common.core.base.handler.dynamictable;

public interface DynamicTableDialectManager {
    boolean tableExists(String tableName);

    void ensureTableExists(String sourceTable, String targetTable);
}
