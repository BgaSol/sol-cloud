package com.bgasol.plugin.openfeign.config;

import cn.dev33.satoken.same.SaSameUtil;
import com.bgasol.common.constant.value.GatewayConfigValues;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import org.springframework.stereotype.Component;

/**
 * feign拦截器, 在feign请求发出之前，加入一些操作
 */
@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 为 Feign 的 RCP调用 添加请求头Same-Token
        requestTemplate.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        // 添加请求头，标识请求不是来自网关
        requestTemplate.header(GatewayConfigValues.XFromGateway, "false");
//        requestTemplate.header(StpUtil.getTokenName(), StpUtil.getTokenValue());
        // 添加Seata的XID
        requestTemplate.header(RootContext.KEY_XID, RootContext.getXID());
    }
}