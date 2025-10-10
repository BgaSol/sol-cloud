package com.bgasol.common.core.base.handler;

import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.ResponseType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.function.Function;

@Slf4j
@RestControllerAdvice
public class PostgreSqlExceptionHandler {

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    @ApiResponse(description = "数据操作异常", responseCode = "500")
    public BaseVo<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = "操作失败 数据已被引用";
        log.error(message, ex);
        return BaseVo.error(message, ResponseType.ERROR);
    }

    /**
     * 处理PostgreSQL异常
     */
    @ExceptionHandler(PSQLException.class)
    @ApiResponse(description = "PostgreSQL异常", responseCode = "500")
    public BaseVo<String> handlePSQLException(PSQLException ex) {
        log.error("数据库异常: ", ex);

        String message = "数据库操作失败";
        String errorMsg = ex.getMessage();

        if (errorMsg != null) {
            if (errorMsg.contains("duplicate key")) {
                message = parseError(errorMsg, this::parseDuplicateKeyLogic,
                        "重复键", "该数据已存在，请勿重复添加或修改后重试");
            } else if (errorMsg.contains("violates foreign key constraint")) {
                message = parseError(errorMsg, this::parseForeignKeyLogic,
                        "外键约束", "数据关联错误，请检查相关数据");
            } else if (errorMsg.contains("violates not-null constraint")) {
                message = parseError(errorMsg, this::parseNotNullLogic,
                        "非空约束", "必填字段不能为空，请检查输入");
            } else if (errorMsg.contains("violates check constraint")) {
                message = parseError(errorMsg, this::parseCheckConstraintLogic,
                        "检查约束", "数据格式不符合要求，请检查输入");
            } else if (errorMsg.contains("value too long")) {
                message = parseError(errorMsg, this::parseValueTooLongLogic,
                        "字符串长度", "输入内容过长，请缩短后重试");
            }
        }

        return BaseVo.error(message, ResponseType.ERROR);
    }

    /**
     * 通用错误解析模板方法
     *
     * @param errorMsg   错误消息
     * @param parser     具体的解析逻辑
     * @param errorType  错误类型（用于日志）
     * @param defaultMsg 默认消息
     * @return 解析后的用户友好消息
     */
    private String parseError(String errorMsg,
                              Function<String, String> parser,
                              String errorType,
                              String defaultMsg) {
        try {
            String result = parser.apply(errorMsg);
            return result != null ? result : defaultMsg;
        } catch (Exception e) {
            log.warn("解析{}错误失败，原始消息: {}", errorType, errorMsg, e);
            return defaultMsg;
        }
    }

    /**
     * 解析重复键错误逻辑，提取具体的字段信息
     */
    private String parseDuplicateKeyLogic(String errorMsg) {
        // 提取Key信息：如 "Key (email)=(test@example.com) already exists"
        if (errorMsg.contains("Key (") && errorMsg.contains(")=(")) {
            String field = extractBetween(errorMsg, "Key (", ")");
            String value = extractBetween(errorMsg, ")=(", ")");
            if (field != null) {
                // 判断是否需要显示具体值
                if (value != null && !value.trim().isEmpty() && value.length() < 50) {
                    return String.format("字段 [%s] 的值「%s」已存在，请勿重复添加", field, value);
                } else if (value != null && value.trim().isEmpty()) {
                    // 值为空字符串的情况
                    return String.format("字段 [%s] 的空值已存在，请勿重复添加", field);
                } else {
                    // 值过长的情况
                    return String.format("字段 [%s] 已存在重复数据，请修改后重试", field);
                }
            }
        }

        // 提取约束名：如 "users_email_key" -> "email"
        if (errorMsg.contains("constraint")) {
            String constraint = extractBetween(errorMsg, "constraint \"", "\"");
            if (constraint != null) {
                String fieldName = parseConstraintName(constraint);
                return String.format("字段 [%s] 已存在重复数据，请修改后重试", fieldName);
            }
        }

        return null;
    }

    /**
     * 解析非空约束错误逻辑
     */
    private String parseNotNullLogic(String errorMsg) {
        String column = extractBetween(errorMsg, "column \"", "\"");
        if (column != null) {
            return String.format("字段 [%s] 不能为空", column);
        }
        return null;
    }

    /**
     * 解析外键约束错误逻辑
     * 例如: violates foreign key constraint "fk_user_dept" on table "users"
     */
    private String parseForeignKeyLogic(String errorMsg) {
        // 判断是插入/更新错误还是删除错误
        if (errorMsg.toLowerCase().contains("is still referenced")) {
            // 删除操作：数据被其他表引用
            String table = extractBetween(errorMsg, "table \"", "\"");
            if (table != null) {
                return String.format("无法删除，该数据正被 [%s] 引用，请先删除关联数据", table);
            }
            return "无法删除，该数据正被其他数据引用，请先删除关联数据";
        } else {
            // 插入/更新操作：引用的数据不存在
            String constraint = extractBetween(errorMsg, "constraint \"", "\"");
            if (constraint != null) {
                String fieldName = parseConstraintName(constraint);
                return String.format("关联的 [%s] 数据不存在，请检查输入", fieldName);
            }
            return "关联数据不存在或已被删除，请检查输入";
        }
    }

    /**
     * 解析检查约束错误逻辑
     * 例如: violates check constraint "check_age"
     */
    private String parseCheckConstraintLogic(String errorMsg) {
        String constraint = extractBetween(errorMsg, "constraint \"", "\"");
        if (constraint != null) {
            String fieldName = parseConstraintName(constraint);
            return String.format("字段 [%s] 的值不符合要求，请检查输入", fieldName);
        }
        return null;
    }

    /**
     * 解析字符串过长错误逻辑
     * 例如: value too long for type character varying(50)
     */
    private String parseValueTooLongLogic(String errorMsg) {
        // 提取最大长度
        String lengthStr = extractBetween(errorMsg, "varying(", ")");
        if (lengthStr != null) {
            return String.format("输入内容过长，最多允许 %s 个字符", lengthStr);
        }
        // 提取 character(n) 格式
        lengthStr = extractBetween(errorMsg, "character(", ")");
        if (lengthStr != null) {
            return String.format("输入内容过长，最多允许 %s 个字符", lengthStr);
        }
        return null;
    }

    /**
     * 从约束名中提取字段名
     * 例如: users_email_key -> email, uk_username -> username
     */
    private String parseConstraintName(String constraint) {
        constraint = constraint.toLowerCase();

        // 移除常见前缀
        constraint = constraint.replaceFirst("^(uk_|uq_|unique_|fk_|check_)", "");

        // 移除常见后缀
        constraint = constraint.replaceFirst("_(key|idx|index|fkey|check)$", "");

        // 移除表名前缀（假设格式为 tablename_fieldname）
        int lastUnderscore = constraint.lastIndexOf('_');
        if (lastUnderscore > 0) {
            constraint = constraint.substring(lastUnderscore + 1);
        }

        return constraint;
    }

    /**
     * 提取两个字符串之间的内容
     */
    private String extractBetween(String text, String start, String end) {
        int startIdx = text.indexOf(start);
        if (startIdx == -1) return null;
        startIdx += start.length();

        int endIdx = text.indexOf(end, startIdx);
        if (endIdx == -1) return null;

        return text.substring(startIdx, endIdx);
    }

}
