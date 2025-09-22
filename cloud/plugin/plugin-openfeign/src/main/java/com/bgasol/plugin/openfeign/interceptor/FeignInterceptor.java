package com.bgasol.plugin.openfeign.interceptor;

import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.bgasol.common.constant.value.GatewayConfigValues;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Component
public class FeignInterceptor implements RequestInterceptor {

    /**
     * feign拦截器, 在feign请求发出之前，加入一些操作
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {

        // 添加来自网关的标识
        requestTemplate.header(GatewayConfigValues.XFromGateway, "false");
        // 添加Same-Token请求头
        requestTemplate.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        // 添加用户身份令牌
        if (InWebRequest()) {
            // 如果请求来自"UserApi#findById" 则不处理
            if ("UserApi#findById(String)".equals(requestTemplate.methodMetadata().configKey())) {
                return;
            }
            if (StpUtil.isLogin()) {
                requestTemplate.header(StpUtil.getTokenName(), StpUtil.getTokenValue());
            }
        }
    }

    /**
     * 判断当前请求是否在web请求的下文中
     */
    public static boolean InWebRequest() {
        return RequestContextHolder.getRequestAttributes() != null;
    }
}
