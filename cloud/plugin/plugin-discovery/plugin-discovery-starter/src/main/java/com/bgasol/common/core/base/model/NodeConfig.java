package com.bgasol.common.core.base.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "system.node")
public class NodeConfig {
    /**
     * 节点名称
     */
    private String name;
    /**
     * 节点ip
     */
    private String ip;

    /**
     * 节点角色
     */
    private String role;

    /**
     * 服务名
     */
    private String appName;
}
