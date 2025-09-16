package com.bgasol.plugin.satoken.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.same.SaSameUtil;
import com.bgasol.common.core.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 权限认证 配置类
 */
@Configuration
@Slf4j
public class SaTokenConfigure implements WebMvcConfigurer {

    @Value("${system.auth.enabled}")
    private Boolean systemAuthEnabled;

    // 注册 Sa-Token 全局过滤器
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 放行普罗米修斯
                .addExclude("/actuator/**")
                .addExclude("/ws/**")
                .addExclude("/ws")
                .addInclude("/**")
                .setAuth(obj -> {
                    SaRequest request = SaHolder.getRequest();
                    String sameToken = request.getHeader(SaSameUtil.SAME_TOKEN);
                    if (systemAuthEnabled) {
                        SaSameUtil.checkToken(sameToken);
                    }
                })
                .setError(e -> {
                    log.error("鉴权失败", e);
                    throw new BaseException("鉴权失败");
                });
    }

    // 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，
        if (!systemAuthEnabled) {
            log.warn("未启用权限认证");
            return;
        }
        registry.addInterceptor(new SaTokenInterceptor())
                .addPathPatterns("/**");
    }
}