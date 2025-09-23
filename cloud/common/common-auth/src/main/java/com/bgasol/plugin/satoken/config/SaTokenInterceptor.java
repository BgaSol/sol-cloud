package com.bgasol.plugin.satoken.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import com.bgasol.common.constant.value.GatewayConfigValues;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/// 重写sa-token的注解拦截器
public class SaTokenInterceptor extends SaInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(GatewayConfigValues.XFromGateway);
        if ("false".equals(header)) {
            // 若是服务内部调用，忽略SA-token的注解校验。直接返回true
            return true;
        }
        // 否则调用父类的SA-token的正常校验。
        return super.preHandle(request, response, handler);
    }
}
