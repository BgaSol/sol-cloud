package com.bgasol.common.core.base.handler;

import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.ResponseType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * PostgreSQL 异常处理，转换为友好的中文提示
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class PostgreSqlExceptionHandler {
    private final EntityFieldCache entityFieldCache;

    @ExceptionHandler(PSQLException.class)
    @ApiResponse(description = "PostgreSQL异常", responseCode = "500")
    public BaseVo<String> handlePSQLException(PSQLException ex) {
        logDatabaseError(ex);

        String errorMsg = ex.getMessage();
        ServerErrorMessage serverError = ex.getServerErrorMessage();

        String tableName = serverError != null ? serverError.getTable() : null;
        String detail = serverError != null ? serverError.getDetail() : null;
        String column = serverError != null ? serverError.getColumn() : null;

        String message = handleMessage(errorMsg, tableName, detail, column);
        return BaseVo.error(message, ResponseType.ERROR);
    }

    private String handleMessage(String errorMsg, String tableName, String detail, String column) {
        if (errorMsg == null) {
            return "数据库操作失败";
        }

        if (errorMsg.contains("duplicate key")) {
            return parseDuplicateKey(tableName, column, detail);
        }
        if (errorMsg.contains("violates foreign key constraint")) {
            return parseForeignKey(tableName, column, detail, errorMsg);
        }
        if (errorMsg.contains("violates not-null constraint")) {
            return parseNotNull(tableName, column);
        }
        if (errorMsg.contains("value too long")) {
            return parseValueTooLong(errorMsg);
        }

        return "数据库操作失败";
    }

    // duplicate key: 唯一键冲突
    private String parseDuplicateKey(String tableName, String field, String detail) {
        if (field == null && detail != null) {
            field = extractBetween(detail, "Key (", ")");
        }

        if (field == null || tableName == null) {
            return "该数据已存在，请勿重复添加";
        }

        String fieldDesc = entityFieldCache.getFieldDescription(tableName, field);

        String value = detail != null ? extractBetween(detail, ")=(", ")") : null;

        if (value != null && !value.trim().isEmpty() && value.length() < 50) {
            return String.format("[%s] 的值「%s」已存在，请勿重复添加", fieldDesc, value);
        }
        if (value != null && value.trim().isEmpty()) {
            return String.format("[%s] 的空值已存在，请勿重复添加", fieldDesc);
        }
        return String.format("[%s] 已存在重复数据，请修改后重试", fieldDesc);
    }

    // foreign key: 外键约束
    private String parseForeignKey(String tableName, String field, String detail, String errorMsg) {
        // 删除时被其他数据引用
        if (errorMsg != null && errorMsg.toLowerCase().contains("is still referenced")) {
            return tableName != null
                    ? String.format("无法删除，该数据正被 [%s] 引用，请先删除关联数据", entityFieldCache.getTableDescription(tableName))
                    : "无法删除，该数据正被其他数据引用，请先删除关联数据";
        }

        // 新增或更新时，关联的数据不存在
        if (field == null && detail != null) {
            field = extractBetween(detail, "Key (", ")");
        }

        if (field != null && tableName != null) {
            String fieldDesc = entityFieldCache.getFieldDescription(tableName, field);
            return String.format("关联的 [%s] 数据不存在，请检查输入", fieldDesc);
        }
        return "关联数据不存在或已被删除，请检查输入";
    }

    // not-null: 必填字段为空
    private String parseNotNull(String tableName, String column) {
        if (column != null && tableName != null) {
            String fieldDesc = entityFieldCache.getFieldDescription(tableName, column);
            return String.format("[%s] 不能为空", fieldDesc);
        }
        return "必填字段不能为空，请检查输入";
    }

    // value too long: 字符串长度超限
    private String parseValueTooLong(String errorMsg) {
        String lengthStr = extractBetween(errorMsg, "varying(", ")");
        if (lengthStr != null) {
            return String.format("输入内容过长，最多允许 %s 个字符", lengthStr);
        }
        return "输入内容过长，请缩短后重试";
    }

    private String extractBetween(String text, String start, String end) {
        if (text == null) {
            return null;
        }

        int startIdx = text.indexOf(start);
        if (startIdx == -1) {
            return null;
        }
        startIdx += start.length();

        int endIdx = text.indexOf(end, startIdx);
        if (endIdx == -1) {
            return null;
        }

        return text.substring(startIdx, endIdx);
    }

    private void logDatabaseError(PSQLException ex) {
        log.error("消息: {}", ex.getMessage());
        log.error("SQL状态: {}", ex.getSQLState());

        ServerErrorMessage serverError = ex.getServerErrorMessage();
        if (serverError != null) {
            log.error("表名: {}", serverError.getTable());
            log.error("列名: {}", serverError.getColumn());
            log.error("约束名: {}", serverError.getConstraint());
            log.error("详细信息: {}", serverError.getDetail());
            log.error("提示: {}", serverError.getHint());
        }

        log.error("ex: ", ex);
    }
}
