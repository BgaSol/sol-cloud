package com.bgasol.common.core.base.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.bgasol.common.core.base.handler.dynamictable.DynamicTableDialectManager;
import com.bgasol.common.core.base.handler.dynamictable.DynamicTableRuleHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicTableNameHandler implements TableNameHandler {
    public final static String DynamicTableNameUnderscore = "_d_";
    private final List<DynamicTableRuleHandler> dynamicTableRuleHandlers;
    private final ConcurrentHashMap<String, Boolean> createdTableNameMap = new ConcurrentHashMap<>();
    private final DynamicTableDialectManager dynamicTableDialectManager;

    @Override
    public String dynamicTableName(String sql, String tableName) {
        for (DynamicTableRuleHandler dynamicTableRuleHandler : dynamicTableRuleHandlers) {

            if (!dynamicTableRuleHandler.use(tableName)) {
                continue;
            }

            String targetTable = tableName + DynamicTableNameUnderscore + dynamicTableRuleHandler.dynamicTableName(sql, tableName);

            if (sql.startsWith("INSERT")) {
                // 检查表是否缓存过
                if (createdTableNameMap.getOrDefault(targetTable, false)) {
                    return targetTable;
                }

                // 建表
                dynamicTableDialectManager.ensureTableExists(tableName, targetTable);

                createdTableNameMap.put(targetTable, true);
                return targetTable;
            } else {
                // 如果目标表是缓存过的，直接用缓存的结果
                if (createdTableNameMap.containsKey(targetTable)) {
                    if (createdTableNameMap.get(targetTable)) {
                        return targetTable;
                    } else {
                        return tableName;
                    }
                } else {
                    // 去数据库检查一下表是不是存在
                    if (dynamicTableDialectManager.tableExists(targetTable)) {
                        createdTableNameMap.put(targetTable, true);
                        return targetTable;
                    } else {
                        // 表不存在 缓存表名
                        createdTableNameMap.put(targetTable, false);
                        return tableName;
                    }
                }
            }
        }
        return tableName;
    }
}
