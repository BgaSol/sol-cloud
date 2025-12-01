package com.bgasol.common.core.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    /**
     * CPU 密集型线程池
     * 核心线程 = 物理核心数 + 1
     * 队列适中，适合计算类任务
     */
    @Bean("cpuThreadPool")
    public ThreadPoolTaskExecutor cpuThreadPool() {
        int cores = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cores + 1;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("cpu-pool-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }

    /**
     * IO 密集型线程池
     * 线程数 = CPU物理核心 * 2~3
     */
    @Bean("ioThreadPool")
    public ThreadPoolTaskExecutor ioThreadPool() {
        int cores = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cores * 2;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize);
        executor.setQueueCapacity(2000);
        executor.setThreadNamePrefix("io-pool-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }
}
