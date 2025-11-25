package com.bgasol.common.core.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThreadPoolConfig {

    /**
     * CPU 密集型线程池
     * 核心线程 = 物理核心数 + 1
     * 队列适中，适合计算类任务
     */
    @Bean("cpuThreadPool")
    public ExecutorService cpuThreadPool() {
        int cores = Runtime.getRuntime().availableProcessors() / 2;
        int corePoolSize = cores + 1;
        return new ThreadPoolExecutor(
                corePoolSize,
                corePoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(500),
                new NamedThreadFactory("cpu-pool"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * IO 密集型线程池
     * 线程数 = CPU物理核心 * 2~3
     */
    @Bean("ioThreadPool")
    public ExecutorService ioThreadPool() {
        int cores = Runtime.getRuntime().availableProcessors() / 2;
        int corePoolSize = cores * 2;

        return new ThreadPoolExecutor(
                corePoolSize,
                corePoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2000),
                new NamedThreadFactory("io-pool"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public static class NamedThreadFactory implements ThreadFactory {
        private final String baseName;
        private final AtomicInteger threadNum = new AtomicInteger(1);

        public NamedThreadFactory(String baseName) {
            this.baseName = baseName;
        }

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            return new Thread(runnable, baseName + "-" + threadNum.getAndIncrement());
        }
    }
}
