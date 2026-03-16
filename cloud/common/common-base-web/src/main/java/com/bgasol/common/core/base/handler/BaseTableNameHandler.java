package com.bgasol.common.core.base.handler;

public interface BaseTableNameHandler {
    String dynamicTableName(String sql, String tableName);

    boolean use(String tableName);
}
