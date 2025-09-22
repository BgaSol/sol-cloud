package com.bgasol.common.core.base.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.bgasol.common.core.base.vo.BaseVo;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 异常处理
 */
@RestControllerAdvice
@Slf4j
public class SaExceptionHandler {
    /**
     * 处理未登录异常
     */
    @ExceptionHandler(value = NotLoginException.class)
    @ApiResponse(description = "未登录异常", responseCode = "401")
    public BaseVo<Void> notLoginExceptionHandler(NotLoginException e) {
        log.error("未登录异常", e);
        return BaseVo.code401();
    }

    /**
     * 处理无权限异常
     */
    @ExceptionHandler(value = NotPermissionException.class)
    @ApiResponse(description = "无权限异常", responseCode = "403")
    public BaseVo<Void> notPermissionExceptionHandler(NotPermissionException e) {
        log.error("无权限异常", e);
        return BaseVo.code403();
    }
}
