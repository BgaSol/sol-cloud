package com.bgasol.common.requestLog.handler;

import com.bgasol.common.core.base.handler.BaseTableNameHandler;
import com.bgasol.model.system.requestLog.entity.RequestLogTable;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RequestLogTableNameHandler implements BaseTableNameHandler {
    public static final ThreadLocal<String> TABLE_SUFFIX = new ThreadLocal<>();

    @Override
    public String dynamicTableName(String sql, String tableName) {
        return tableName + "_" + TABLE_SUFFIX.get();
    }

    @Override
    public boolean use(String tableName) {
        return RequestLogTable.TableName.equals(tableName);
    }
}
