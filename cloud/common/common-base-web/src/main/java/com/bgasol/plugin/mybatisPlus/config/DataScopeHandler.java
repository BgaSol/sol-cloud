package com.bgasol.plugin.mybatisPlus.config;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.user.entity.UserEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.bgasol.common.constant.value.SystemConfigValues.ADMIN_USER_ID;
import static com.bgasol.plugin.openfeign.interceptor.FeignInterceptor.InWebRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataScopeHandler implements MultiDataPermissionHandler {

    List<String> getMyBaseMapperMethodNameList() {
        return Arrays.stream(MyBaseMapper.class.getMethods()).map(Method::getName).toList();
    }

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // 获取 MyBaseMapper 的所有方法名
        boolean isMyBaseMapperMethod = getMyBaseMapperMethodNameList().stream().anyMatch(mappedStatementId::endsWith);
        if (isMyBaseMapperMethod) {
            // 忽略 MyBaseMapper 的自定义方法
            return null;
        }
        ScopeOptions scopeOption;
        try {
            Class<? extends BaseEntity> entityClass = getEntityClassByMapper(mappedStatementId);
            scopeOption = getScopeOption(entityClass);
        } catch (RuntimeException e) {
            log.warn("无法获取实体类: {}", mappedStatementId, e);
            return null;
        }
        if (!scopeOption.hasTrue()) {
            // 当前数据操作，没有范围查询功能
            return null;
        }
        if (!InWebRequest()) {
            // 不在请求上下文中
            return null;
        }
        if (!StpUtil.isLogin()) {
            // 当前上下文没有用户信息
            return null;
        }
        String userId = StpUtil.getLoginIdAsString();
        if (ADMIN_USER_ID.equals(userId)) {
            // 管理员不需要做数据范围限制
            return null;
        }
        // todo 判断是否需要做数据范围限制
        boolean isNeedDataScope = false;
        if (!isNeedDataScope) {
            // 不需要做数据范围限制
            return null;
        }
//        String sql = """
//                EXISTS (
//                    SELECT 1
//                    FROM %s tn
//                    WHERE tn.%s = u.id
//                    AND tn.%s IN (%s)
//                )""";
        return null;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends BaseEntity> getEntityClassByMapper(String mappedStatementId) {
        int lastDot = mappedStatementId.lastIndexOf('.');
        String className = mappedStatementId.substring(0, lastDot);
        Class<?> mapperClass;
        try {
            mapperClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("无法解析实体类: " + mappedStatementId, e);
        }
        // 直接获取 BaseMapper<T> 的泛型映射
        Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(mapperClass, BaseMapper.class);
        Type entityType = typeArguments.values().stream().findFirst().orElse(null);
        if (!(entityType instanceof Class<?>)) {
            throw new IllegalArgumentException("无法解析实体类: " + mappedStatementId);
        }
        return (Class<? extends BaseEntity>) entityType;
    }

    private ScopeOptions getScopeOption(Class<? extends BaseEntity> entityClass) {
        ScopeOptions scopeOptions = new ScopeOptions();

        Field[] fields = FieldUtils.getAllFields(entityClass);
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (ClassUtils.isAssignable(fieldType, List.class)) {
                // 获取泛型参数
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType parameterizedType) {
                    Type[] typeArgs = parameterizedType.getActualTypeArguments();
                    if (ArrayUtils.isNotEmpty(typeArgs)
                            && typeArgs[0] instanceof Class<?> listType) {
                        if (ClassUtils.isAssignable(listType, DepartmentEntity.class)) {
                            JoinTable joinTable = field.getAnnotation(JoinTable.class);
                            scopeOptions.departmentListTableName = joinTable.name();
                            scopeOptions.departmentListJoinColumnName = joinTable.joinColumns()[0].name();
                            scopeOptions.departmentListInverseJoinColumnName = joinTable.inverseJoinColumns()[0].name();
                            scopeOptions.departmentList = true;
                        } else if (ClassUtils.isAssignable(listType, UserEntity.class)) {
                            JoinTable joinTable = field.getAnnotation(JoinTable.class);
                            scopeOptions.userListTableName = joinTable.name();
                            scopeOptions.userListJoinColumnName = joinTable.joinColumns()[0].name();
                            scopeOptions.userListInverseJoinColumnName = joinTable.inverseJoinColumns()[0].name();
                            scopeOptions.userList = true;
                        }
                    }
                }
            } else {
                if (ClassUtils.isAssignable(fieldType, DepartmentEntity.class)) {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    scopeOptions.departmentColumnName = joinColumn.name();
                    scopeOptions.department = true;
                } else if (ClassUtils.isAssignable(fieldType, UserEntity.class)) {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    scopeOptions.userColumnName = joinColumn.name();
                    scopeOptions.user = true;
                }
            }
        }
        return scopeOptions;
    }
}

@Getter
@Setter
@NoArgsConstructor
class ScopeOptions {
    boolean department = false;
    String departmentColumnName;

    boolean departmentList = false;
    String departmentListTableName;
    String departmentListJoinColumnName;
    String departmentListInverseJoinColumnName;

    boolean user = false;
    String userColumnName;

    boolean userList = false;
    String userListTableName;
    String userListJoinColumnName;
    String userListInverseJoinColumnName;

    boolean hasTrue() {
        return department || departmentList || user || userList;
    }
}
//        // todo 创建 exists 表达式 做数据范围限制
//        UserEntity loginUser = userApi.findById(userId).getData();
//
//        // todo 获取中间表名
//        String tableName = "user_dept";
//        // todo 获取中间表左字段
//        String leftField = "user_id";
//        // todo 获取中间表右字段
//        String rightField = "dept_id";
//        // todo 获取中间表左字段值
//        String leftFieldValue = table.getName() + ".dept_id";
//        // todo 获取中间表右字段值
//        List<String> rightFieldValues = List.of("10", "20", "30"); // 示例值，实际需要从服务获取
//        String rightFieldValuesStr = String.join(",", rightFieldValues);
//        Expression existsExpression;
//        try {
//            existsExpression = CCJSqlParserUtil.parseExpression("""
//                EXISTS (
//                    SELECT 1
//                    FROM %s tn
//                    WHERE tn.%s = u.id
//                    AND tn.%s IN (%s)
//                )
//            """.trim().formatted(tableName, leftField, rightField, rightFieldValuesStr));
//        } catch (net.sf.jsqlparser.parser.ParseException e) {
//            throw new RuntimeException(e);
//        }
//        if (where == null) {
//            return existsExpression;
//        } else {
//            return new AndExpression(where, existsExpression);
//        }