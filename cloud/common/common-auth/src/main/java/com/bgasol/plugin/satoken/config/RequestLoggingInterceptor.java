package com.bgasol.plugin.satoken.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 请求日志拦截器
 * 记录每个请求的开始时间、结束时间、耗时等信息
 */
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "REQUEST_START_TIME";
    private static final String REQUEST_HANDLER_ATTRIBUTE = "REQUEST_HANDLER";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        String handlerInfo = getHandlerInfo(handler);

        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        request.setAttribute(REQUEST_HANDLER_ATTRIBUTE, handlerInfo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime == null) {
            return;
        }

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String handlerInfo = (String) request.getAttribute(REQUEST_HANDLER_ATTRIBUTE);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        int status = response.getStatus();

        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("[请求] ");
        logBuilder.append(method).append(" ").append(uri);

        if (queryString != null && !queryString.isEmpty()) {
            logBuilder.append("?").append(queryString);
        }

        if (handlerInfo != null && !handlerInfo.isEmpty()) {
            logBuilder.append(" | ").append(handlerInfo);
        }

        logBuilder.append(" | 状态:").append(status);
        logBuilder.append(" | 耗时:").append(duration).append("ms");

        if (ex != null) {
            logBuilder.append(" ").append(ex.getClass().getSimpleName()).append(": ").append(ex.getMessage());
        }

        if (ex != null) {
            log.error(logBuilder.toString());
        } else {
            log.info(logBuilder.toString());
        }
    }

    private String getHandlerInfo(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            String className = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();
            return className + "." + methodName + "()";
        }
        return null;
    }


}

