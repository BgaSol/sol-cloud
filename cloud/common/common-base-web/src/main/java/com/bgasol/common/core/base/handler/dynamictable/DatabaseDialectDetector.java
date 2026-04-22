package com.bgasol.common.core.base.handler.dynamictable;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseDialectDetector {
    private final JdbcTemplate jdbcTemplate;

    private volatile DbType dbType;

    /**
     * 解析数据库类型（使用双重检查锁定缓存）
     */
    public DbType resolveDbType() {
        DbType cached = dbType;
        if (cached != null) {
            return cached;
        }

        synchronized (this) {
            if (dbType == null) {
                dbType = detectDbType();
                log.info("检测到数据库类型: {}", dbType.getDb());
            }
            return dbType;
        }
    }

    private DbType detectDbType() {
        ConnectionCallback<String> callback = connection -> {
            DatabaseMetaData metaData = connection.getMetaData();
            return extractDialect(metaData.getDatabaseProductName(), metaData.getURL());
        };
        String dialect = jdbcTemplate.execute(callback);
        return toDbType(dialect);
    }

    private DbType toDbType(String dialect) {
        String normalized = normalizeDialect(dialect);
        return switch (normalized) {
            case "postgres", "postgresql" -> DbType.POSTGRE_SQL;
            case "kingbase", "kingbase8", "kingbasees" -> DbType.KINGBASE_ES;
            case "mysql" -> DbType.MYSQL;
            case "mariadb" -> DbType.MARIADB;
            case "oceanbase", "oceanbasemysql" -> DbType.OCEAN_BASE;
            case "oracle" -> DbType.ORACLE;
            case "sqlserver", "microsoftsqlserver" -> DbType.SQL_SERVER;
            case "db2" -> DbType.DB2;
            case "h2" -> DbType.H2;
            case "sqlite" -> DbType.SQLITE;
            case "dm", "dameng" -> DbType.DM;
            default -> DbType.getDbType(normalized);
        };
    }

    private String extractDialect(String databaseProductName, String jdbcUrl) {
        String fromProductName = normalizeDialect(databaseProductName);
        if (!fromProductName.isBlank()) {
            return fromProductName;
        }
        return extractDialectFromJdbcUrl(jdbcUrl);
    }

    private String extractDialectFromJdbcUrl(String jdbcUrl) {
        if (jdbcUrl == null) {
            return "";
        }
        String normalized = jdbcUrl.trim().toLowerCase(Locale.ROOT);
        if (!normalized.startsWith("jdbc:")) {
            return "";
        }
        String value = normalized.substring("jdbc:".length());
        int separatorIndex = value.indexOf(':');
        if (separatorIndex >= 0) {
            return value.substring(0, separatorIndex);
        }
        separatorIndex = value.indexOf(';');
        if (separatorIndex >= 0) {
            return value.substring(0, separatorIndex);
        }
        return value;
    }

    private String normalizeDialect(String dialect) {
        if (dialect == null) {
            return "";
        }
        String normalized = dialect.trim().toLowerCase(Locale.ROOT);
        return normalized
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "");
    }
}
