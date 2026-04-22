package com.bgasol.common.core.base.handler.dynamictable;

public interface DynamicTableDialectProvider {
    String dialect();

    boolean tableExists(String tableName);

    void ensureTableExists(String sourceTable, String targetTable);
}
