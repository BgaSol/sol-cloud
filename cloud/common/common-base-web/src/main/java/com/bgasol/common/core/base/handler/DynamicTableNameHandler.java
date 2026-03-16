package com.bgasol.common.core.base.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class DynamicTableNameHandler implements TableNameHandler {
    public final static String DynamicTableNameUnderscore = "_d_";
    private final List<BaseTableNameHandler> baseTableNameHandlers;
    private final ConcurrentHashMap<String, Boolean> createdTableNameMap = new ConcurrentHashMap<>();
    private final JdbcTemplate jdbcTemplate;

    @Override
    public String dynamicTableName(String sql, String tableName) {
        for (BaseTableNameHandler baseTableNameHandler : baseTableNameHandlers) {

            if (!baseTableNameHandler.use(tableName)) {
                continue;
            }

            String targetTable = tableName + DynamicTableNameUnderscore + baseTableNameHandler.dynamicTableName(sql, tableName);

            if (sql.startsWith("INSERT")) {
                // 检查表是否缓存过
                if (createdTableNameMap.getOrDefault(targetTable, false)) {
                    return targetTable;
                }

                // 建表并缓存表名
                String createSql = "CREATE TABLE IF NOT EXISTS %s (LIKE %s INCLUDING ALL)".formatted(targetTable, tableName);
                jdbcTemplate.execute(createSql);
                createdTableNameMap.put(tableName, true);

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
                    Boolean exists = jdbcTemplate.queryForObject("SELECT to_regclass(?) IS NOT NULL", Boolean.class, targetTable);
                    if (exists) {
                        // 表存在 缓存表名
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
