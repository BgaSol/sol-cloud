package com.bgasol.plugin.openfeign.interceptor;

import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.bgasol.common.constant.value.GatewayConfigValues;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeignInterceptor implements RequestInterceptor {

    public static final String TRACE_ID = "TRACE_ID";
    public static final String SPAN_ID = "SPAN_ID";
    public static final String TRACE_START_TIME = "TRACE_START_TIME";

    /**
     * feign拦截器, 在feign请求发出之前，加入一些操作
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {

        // 添加来自网关的标识
        requestTemplate.header(GatewayConfigValues.XFromGateway, Boolean.FALSE.toString());
        // 添加Same-Token请求头
        requestTemplate.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        // 添加用户身份令牌
        boolean useToken = InWebRequest()
                && StpUtil.isLogin()
                && BooleanUtils.isFalse(requestTemplate.methodMetadata().method().isAnnotationPresent(GlobalScope.class));
        if (useToken) {
            requestTemplate.header(StpUtil.getTokenName(), StpUtil.getTokenValue());
        }

        // 添加链路信息
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            HttpServletRequest request = servletAttributes.getRequest();
            String spanId = (String) request.getAttribute(SPAN_ID);
            String traceId = (String) request.getAttribute(TRACE_ID);
            String traceStartTime = (String) request.getAttribute(TRACE_START_TIME);
            requestTemplate.header(SPAN_ID, spanId);
            requestTemplate.header(TRACE_ID, traceId);
            requestTemplate.header(TRACE_START_TIME, traceStartTime);
        }
    }

    /**
     * 判断当前请求是否在web请求的下文中
     */
    public static boolean InWebRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes;
    }
}
