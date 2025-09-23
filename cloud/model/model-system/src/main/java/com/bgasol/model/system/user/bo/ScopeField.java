package com.bgasol.model.system.user.bo;

import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.user.entity.UserEntity;

import java.lang.annotation.*;
import java.util.List;


/**
 * 数据权限字段注解
 * <p>
 * 只能修饰于:
 * {@link UserEntity}
 * {@link DepartmentEntity}
 * 以及其对应的列表类型:
 * {@link List}
 * <p>
 */
@Target(ElementType.FIELD) // 只能标记字段
@Retention(RetentionPolicy.RUNTIME) // 运行时可用
@Documented
public @interface ScopeField {
}
