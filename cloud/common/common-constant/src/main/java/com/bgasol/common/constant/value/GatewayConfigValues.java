package com.bgasol.common.constant.value;

import org.springframework.stereotype.Component;

@Component
public class GatewayConfigValues {
    public final static String SERVICE_NAME = "gateway";
    // 来自网关的标识
    public final static String XFromGateway = "X-From-Gateway";
}
