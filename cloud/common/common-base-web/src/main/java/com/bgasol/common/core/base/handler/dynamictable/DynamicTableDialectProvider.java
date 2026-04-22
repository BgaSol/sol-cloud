package com.bgasol.common.core.base.handler.dynamictable;

import com.baomidou.mybatisplus.annotation.DbType;

public interface DynamicTableDialectProvider {
    boolean supports(DbType dbType);

    boolean tableExists(String tableName);

    void ensureTableExists(String sourceTable, String targetTable);
}
