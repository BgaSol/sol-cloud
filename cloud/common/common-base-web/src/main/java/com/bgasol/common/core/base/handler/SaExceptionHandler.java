package com.bgasol.common.core.base.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.bgasol.common.core.base.vo.BaseVo;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.bgasol.common.constant.value.SystemConfigValues.REQUEST_EXCEPTION;

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
    public BaseVo<Void> notLoginExceptionHandler(NotLoginException e, HttpServletRequest request) {
        request.setAttribute(REQUEST_EXCEPTION, "未登录异常");
        return BaseVo.code401();
    }

    /**
     * 处理无权限异常
     */
    @ExceptionHandler(value = NotPermissionException.class)
    @ApiResponse(description = "无权限异常", responseCode = "403")
    public BaseVo<Void> notPermissionExceptionHandler(NotPermissionException e, HttpServletRequest request) {
        request.setAttribute(REQUEST_EXCEPTION, "无权限异常");
        return BaseVo.code403();
    }
}
