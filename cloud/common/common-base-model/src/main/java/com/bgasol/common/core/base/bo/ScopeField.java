package com.bgasol.common.core.base.bo;

import java.lang.annotation.*;

/**
 * 数据权限字段注解
 */
@Target(ElementType.FIELD) // 只能标记字段
@Retention(RetentionPolicy.RUNTIME) // 运行时可用
@Documented
public @interface ScopeField {
}
