package com.bgasol.plugin.openfeign.interceptor;

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
        // 添加Same-Token请求头
        requestTemplate.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        // 添加来自网关的标识
        requestTemplate.header(GatewayConfigValues.XFromGateway, "false");
        // 添加Seata的XID
        requestTemplate.header(RootContext.KEY_XID, RootContext.getXID());
    }
}
