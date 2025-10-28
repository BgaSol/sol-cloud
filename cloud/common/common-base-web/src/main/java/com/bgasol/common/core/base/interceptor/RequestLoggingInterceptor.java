package com.bgasol.common.core.base.interceptor;

import com.bgasol.plugin.websocket.dto.WsSendMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.bgasol.common.util.WSUtils.GetWSTopic;

/**
 * 请求日志拦截器
 * 记录每个请求的开始时间、结束时间、耗时等信息
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "REQUEST_START_TIME";
    private static final String REQUEST_HANDLER_ATTRIBUTE = "REQUEST_HANDLER";
    private static final String REQUEST_LOG = "REQUEST_LOG";
    @Value("${spring.application.name}")
    private String serviceName;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;
    @Value("${system.ws.open-request-log}")
    private Boolean openWsRequestLog;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        long startTime = System.currentTimeMillis();
        String handlerInfo = getHandlerInfo(handler);

        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        request.setAttribute(REQUEST_HANDLER_ATTRIBUTE, handlerInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime == null) {
            return;
        }

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String handlerInfo = (String) request.getAttribute(REQUEST_HANDLER_ATTRIBUTE);

        long endTime = System.currentTimeMillis();
        int status = response.getStatus();

        // 获取当前线程id
        long threadId = Thread.currentThread().getId();
        if (!openWsRequestLog) {
            return;
        }
        try {
            RTopic ws = redissonClient.getTopic(GetWSTopic(serviceName));
            String logString = objectMapper.writeValueAsString(new RequestLog(threadId,
                    serviceName,
                    method,
                    uri,
                    startTime,
                    endTime,
                    queryString,
                    handlerInfo,
                    status,
                    ex != null ? ex.getMessage() : ""));
            ws.publish(WsSendMessageDto.builder()
                    .json(logString)
                    .type(REQUEST_LOG).build());
        } catch (Exception e) {
            log.error("日志消息广播异常", e);
        }
    }

    private record RequestLog(Long threadId,
                              String serviceName,
                              String method,
                              String uri,
                              Long startTime,
                              Long endTime,
                              String queryString,
                              String handlerInfo,
                              Integer status,
                              String errorMessage) {
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

