package com.bgasol.common.core.base.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.bgasol.common.requestLog.service.RequestLogService;
import com.bgasol.model.system.requestLog.entity.RequestLogEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import static com.bgasol.common.constant.value.SystemConfigValues.REQUEST_EXCEPTION;
import static com.bgasol.common.constant.value.SystemConfigValues.REQUEST_EXCEPTION_PRIMARY;
import static com.bgasol.plugin.openfeign.interceptor.FeignInterceptor.*;

/**
 * 请求日志拦截器
 * 记录每个请求的开始时间、结束时间、耗时等信息
 */
@Slf4j
@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${system.node-name}")
    private String nodeName;
    @Value("${system.node-ip}")
    private String nodeIp;

    private static final String BUSINESS_CONTROLLER = "BUSINESS_CONTROLLER";
    private static final String BUSINESS_METHOD = "BUSINESS_METHOD";
    private static final String START_TIME_ATTRIBUTE = "REQUEST_START_TIME";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd");

    @Lazy
    private final RequestLogService requestLogService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        getHandlerInfo(request, handler);

        Date now = new Date();
        request.setAttribute(START_TIME_ATTRIBUTE, now.getTime());

        String traceId = request.getHeader(TRACE_ID);
        String traceStartTime = request.getHeader(TRACE_START_TIME);

        if (ObjectUtils.isEmpty(traceId)) {

            traceId = UUID.randomUUID()
                    .toString()
                    .replace("-", "");
            request.setAttribute(TRACE_ID, traceId);

            traceStartTime = now.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DATE_FORMAT);
            request.setAttribute(TRACE_START_TIME, traceStartTime);

        } else {
            request.setAttribute(TRACE_ID, traceId);
            request.setAttribute(TRACE_START_TIME, traceStartTime);
        }

        String spanId = traceStartTime + "_" + UUID.randomUUID()
                .toString()
                .replace("-", "");
        request.setAttribute(SPAN_ID, spanId);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {

        String parentSpanId = request.getHeader(SPAN_ID);
        String spanId = (String) request.getAttribute(SPAN_ID);
        String traceId = (String) request.getAttribute(TRACE_ID);

        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long endTime = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        int status = response.getStatus();
        long threadId = Thread.currentThread().getId();

        String businessMethod = (String) request.getAttribute(BUSINESS_METHOD);
        String businessController = (String) request.getAttribute(BUSINESS_CONTROLLER);

        String errorLog = null;
        boolean isPrimaryErr = false;
        if (ex != null) {
            isPrimaryErr = true;
            errorLog = ExceptionUtils.getStackTrace(ex);
            log.error(ex.getMessage(), ex);
        } else {
            if (BooleanUtils.isTrue((Boolean) request.getAttribute(REQUEST_EXCEPTION_PRIMARY))) {
                isPrimaryErr = true;
                errorLog = (String) request.getAttribute(REQUEST_EXCEPTION);
            }
        }
        try {
            requestLogService.insert(RequestLogEntity.builder()
                    .id(spanId)
                    .parentId(parentSpanId)
                    .createTime(new Date(startTime))
                    .updateTime(new Date(endTime))
                    .traceId(traceId)
                    .serviceName(serviceName)
                    .nodeName(nodeName)
                    .nodeIp(nodeIp)
                    .method(method)
                    .uri(uri)
                    .queryString(queryString)
                    .status(status)
                    .threadId(threadId)
                    .errorLog(errorLog)
                    .isPrimaryErr(isPrimaryErr)
                    .businessMethod(businessMethod)
                    .businessController(businessController)
                    .userId(StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : null)
                    .build());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void getHandlerInfo(HttpServletRequest request, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            Class<?> controllerClass = handlerMethod.getBeanType();
            Method method = handlerMethod.getMethod();
            Operation operation = method.getAnnotation(Operation.class);
            if (operation != null) {
                String summary = operation.summary();

                request.setAttribute(BUSINESS_METHOD, summary);
            }
            Tag tag = controllerClass.getAnnotation(Tag.class);
            if (tag != null) {
                request.setAttribute(BUSINESS_CONTROLLER, tag.name());
            }
        }
    }
}

