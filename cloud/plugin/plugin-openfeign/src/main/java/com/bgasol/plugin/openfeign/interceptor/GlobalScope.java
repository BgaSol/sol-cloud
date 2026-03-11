package com.bgasol.plugin.openfeign.interceptor;

import java.lang.annotation.*;

/**
 * 数据权限字段注解
 */
@Target(ElementType.METHOD) // 只能标记函数
@Retention(RetentionPolicy.RUNTIME) // 运行时可用
@Documented
public @interface GlobalScope {
}
