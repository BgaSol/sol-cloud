package com.bgasol.common.core.base.handler.dynamictable;

public interface DynamicTableRuleHandler {
    String dynamicTableName(String sql, String tableName);

    boolean use(String tableName);
}
