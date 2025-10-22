package com.bgasol.common.core.base.handler;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类字段信息缓存，启动时扫描所有实体类并缓存字段描述
 */
@Slf4j
@Component
public class EntityFieldCache {
    private static final String SCAN_PACKAGE = "com.bgasol.model";
    
    private final Map<String, Map<String, FieldInfo>> tableFieldCache = new ConcurrentHashMap<>();
    private final Map<String, String> tableNameCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("开始扫描实体类...");
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(TableName.class));

        scanner.findCandidateComponents(SCAN_PACKAGE).parallelStream().forEach(bd -> {
            try {
                scanEntityClass(Class.forName(bd.getBeanClassName()));
            } catch (Exception e) {
                log.warn("扫描实体类失败: {}", bd.getBeanClassName(), e);
            }
        });
        log.info("实体类扫描完成，共 {} 个表", tableFieldCache.size());
    }

    public void scanEntityClass(Class<?> entityClass) {
        TableName annotation = entityClass.getAnnotation(TableName.class);
        if (annotation == null || annotation.value().isEmpty()) {
            return;
        }
        Schema tableSchema = entityClass.getAnnotation(Schema.class);
        if (tableSchema!=null){
            tableNameCache.put(annotation.value(), tableSchema.description());
        }
        String tableName = annotation.value();
        Map<String, FieldInfo> fieldMap = new ConcurrentHashMap<>();

        // 扫描字段，包括父类字段
        Class<?> currentClass = entityClass;
        while (currentClass != null && currentClass != Object.class) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            for (Field field : declaredFields) {
                TableField tableField = field.getAnnotation(TableField.class);
                if (tableField != null && tableField.exist()) {
                    Schema schema = field.getAnnotation(Schema.class);
                    String description = schema != null ? schema.description() : field.getName();
                    fieldMap.put(tableField.value(),
                            new FieldInfo(tableField.value(), field.getName(), description));
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        tableFieldCache.put(tableName, fieldMap);
        log.debug("缓存表: {} ({} 个字段)", tableName, fieldMap.size());
    }

    public String getFieldDescription(String tableName, String dbFieldName) {
        if (tableName == null || dbFieldName == null) {
            return null;
        }
        Map<String, FieldInfo> fieldMap = tableFieldCache.get(tableName);
        if (fieldMap != null) {
            FieldInfo fieldInfo = fieldMap.get(dbFieldName);
            return fieldInfo != null ? fieldInfo.description : dbFieldName;
        }

        return dbFieldName;
    }

    public String getTableDescription(String tableName) {
        if (StringUtils.isBlank(tableName)){
            return null;
        }
        return tableNameCache.getOrDefault(tableName,tableName);
    }

    public record FieldInfo(String dbFieldName, String javaFieldName, String description) {
    }
}

