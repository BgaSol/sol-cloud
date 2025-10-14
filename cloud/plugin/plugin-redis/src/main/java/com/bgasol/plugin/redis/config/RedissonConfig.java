package com.bgasol.plugin.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Redisson 配置
 * 支持单机、集群、哨兵三种模式
 */
@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.mode:single}")
    private String mode;

    // 单机模式配置
    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.database:0}")
    private int database;

    // 集群模式配置
    @Value("${spring.data.redis.cluster.nodes:}")
    private String clusterNodes;

    @Value("${spring.data.redis.cluster.scan-interval:2000}")
    private int scanInterval;

    @Value("${spring.data.redis.cluster.read-mode:SLAVE}")
    private String readMode;

    // 哨兵模式配置
    @Value("${spring.data.redis.sentinel.master:}")
    private String sentinelMaster;

    @Value("${spring.data.redis.sentinel.nodes:}")
    private String sentinelNodes;

    // 连接池配置
    @Value("${spring.data.redis.pool.connection-pool-size:64}")
    private int connectionPoolSize;

    @Value("${spring.data.redis.pool.connection-minimum-idle-size:10}")
    private int connectionMinimumIdleSize;

    @Value("${spring.data.redis.pool.idle-connection-timeout:10000}")
    private int idleConnectionTimeout;

    @Value("${spring.data.redis.pool.connect-timeout:10000}")
    private int connectTimeout;

    @Value("${spring.data.redis.pool.timeout:3000}")
    private int timeout;

    @Bean(destroyMethod = "shutdown")
    @Primary
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());

        switch (mode.toLowerCase()) {
            case "cluster":
                setupClusterConfig(config);
                break;
            case "sentinel":
                setupSentinelConfig(config);
                break;
            case "single":
            default:
                setupSingleConfig(config);
                break;
        }

        log.info("Redisson 配置完成: 模式={}, 编解码器=JsonJacksonCodec", mode);
        return Redisson.create(config);
    }

    /**
     * 配置单机模式
     */
    private void setupSingleConfig(Config config) {
        log.info("配置 Redis 单机模式: {}:{}", host, port);

        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(String.format("redis://%s:%d", host, port))
                .setDatabase(database)
                .setConnectionPoolSize(connectionPoolSize)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setIdleConnectionTimeout(idleConnectionTimeout)
                .setConnectTimeout(connectTimeout)
                .setTimeout(timeout);

        if (StringUtils.isNotBlank(password)) {
            serverConfig.setPassword(password);
        }
    }

    /**
     * 配置集群模式
     */
    private void setupClusterConfig(Config config) {
        if (StringUtils.isBlank(clusterNodes)) {
            throw new IllegalArgumentException("Redis 集群模式需要配置 spring.data.redis.cluster.nodes");
        }

        String[] nodes = clusterNodes.split(",");
        String[] addresses = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            addresses[i] = nodes[i].startsWith("redis://") ? nodes[i] : "redis://" + nodes[i].trim();
        }

        log.info("配置 Redis 集群模式: 节点数={}, 扫描间隔={}ms, 读模式={}",
                addresses.length, scanInterval, readMode);

        ClusterServersConfig clusterConfig = config.useClusterServers()
                .addNodeAddress(addresses)
                .setScanInterval(scanInterval)
                .setReadMode(org.redisson.config.ReadMode.valueOf(readMode))
                .setMasterConnectionPoolSize(connectionPoolSize)
                .setSlaveConnectionPoolSize(connectionPoolSize)
                .setMasterConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setSlaveConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setIdleConnectionTimeout(idleConnectionTimeout)
                .setConnectTimeout(connectTimeout)
                .setTimeout(timeout);

        if (StringUtils.isNotBlank(password)) {
            clusterConfig.setPassword(password);
        }
    }

    /**
     * 配置哨兵模式
     */
    private void setupSentinelConfig(Config config) {
        if (StringUtils.isBlank(sentinelMaster)) {
            throw new IllegalArgumentException("Redis 哨兵模式需要配置 spring.data.redis.sentinel.master");
        }
        if (StringUtils.isBlank(sentinelNodes)) {
            throw new IllegalArgumentException("Redis 哨兵模式需要配置 spring.data.redis.sentinel.nodes");
        }

        String[] nodes = sentinelNodes.split(",");
        String[] addresses = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            addresses[i] = nodes[i].startsWith("redis://") ? nodes[i] : "redis://" + nodes[i].trim();
        }

        log.info("配置 Redis 哨兵模式: master={}, 哨兵节点数={}", sentinelMaster, addresses.length);

        SentinelServersConfig sentinelConfig = config.useSentinelServers()
                .setMasterName(sentinelMaster)
                .addSentinelAddress(addresses)
                .setDatabase(database)
                .setMasterConnectionPoolSize(connectionPoolSize)
                .setSlaveConnectionPoolSize(connectionPoolSize)
                .setMasterConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setSlaveConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setIdleConnectionTimeout(idleConnectionTimeout)
                .setConnectTimeout(connectTimeout)
                .setTimeout(timeout);

        if (StringUtils.isNotBlank(password)) {
            sentinelConfig.setPassword(password);
        }
    }
}
