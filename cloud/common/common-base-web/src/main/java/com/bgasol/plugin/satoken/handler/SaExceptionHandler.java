package com.bgasol.plugin.satoken.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.bgasol.common.core.base.vo.BaseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Sa-Token 异常处理
 */
@ControllerAdvice
@Slf4j
public class SaExceptionHandler {
    /**
     * 处理未登录异常
     */
    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public BaseVo<?> notLoginExceptionHandler(NotLoginException e) {
        log.error("未登录异常", e);
        return BaseVo.code401();
    }

    /**
     * 处理无权限异常
     */
    @ExceptionHandler(value = NotPermissionException.class)
    @ResponseBody
    public BaseVo<?> notPermissionExceptionHandler(NotPermissionException e) {
        log.error("无权限异常", e);
        return BaseVo.code403();
    }
}
