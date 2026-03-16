package com.bgasol.plugin.mybatisPlus.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.bgasol.common.core.base.handler.DataScopeHandler;
import com.bgasol.common.core.base.handler.DynamicTableNameHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MybatisPlusInterceptorConfig {
    private final DataScopeHandler dataScopeHandler;

    private final DynamicTableNameHandler dynamicTableNameHandler;

    @Bean()
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 初始化MybatisPlus拦截器
        MybatisPlusInterceptor interceptors = new MybatisPlusInterceptor();

        // 数据权限插件 *数据权限插件需要在分页插件之前添加
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
        dataPermissionInterceptor.setDataPermissionHandler(dataScopeHandler);
        interceptors.addInnerInterceptor(dataPermissionInterceptor);

        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setMaxLimit(10000L);
        interceptors.addInnerInterceptor(paginationInnerInterceptor);

        // 动态表名插件
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor(dynamicTableNameHandler);
        interceptors.addInnerInterceptor(dynamicTableNameInnerInterceptor);

        return interceptors;
    }
}
