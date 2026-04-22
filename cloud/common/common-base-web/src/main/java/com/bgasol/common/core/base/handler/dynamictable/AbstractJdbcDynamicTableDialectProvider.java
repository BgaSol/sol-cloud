package com.bgasol.common.core.base.handler.dynamictable;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

@RequiredArgsConstructor
public abstract class AbstractJdbcDynamicTableDialectProvider implements DynamicTableDialectProvider {
    protected final JdbcTemplate jdbcTemplate;

    @Override
    public boolean tableExists(String tableName) {
        ConnectionCallback<Boolean> callback = connection -> {
            TableIdentifier tableIdentifier = TableIdentifier.parse(tableName);
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = tableIdentifier.schema() != null ? tableIdentifier.schema() : connection.getSchema();

            if (exists(metaData, connection.getCatalog(), schema, tableIdentifier.table())) {
                return true;
            }

            return exists(metaData, connection.getCatalog(), null, tableIdentifier.table());
        };
        return Boolean.TRUE.equals(jdbcTemplate.execute(callback));
    }

    protected boolean exists(DatabaseMetaData metaData, String catalog, String schema, String tableName) throws SQLException {
        try (ResultSet resultSet = metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    protected record TableIdentifier(String schema, String table) {
        protected static TableIdentifier parse(String tableName) {
            String normalized = tableName == null ? "" : tableName.trim();
            int dotIndex = normalized.indexOf('.');
            if (dotIndex < 0) {
                return new TableIdentifier(null, normalized);
            }

            String schema = normalizePart(normalized.substring(0, dotIndex));
            String table = normalizePart(normalized.substring(dotIndex + 1));
            return new TableIdentifier(schema, table);
        }

        private static String normalizePart(String value) {
            String normalized = value == null ? "" : value.trim();
            if (normalized.length() >= 2 && normalized.startsWith("\"") && normalized.endsWith("\"")) {
                return normalized.substring(1, normalized.length() - 1);
            }
            return normalized.toLowerCase(Locale.ROOT);
        }
    }
}
