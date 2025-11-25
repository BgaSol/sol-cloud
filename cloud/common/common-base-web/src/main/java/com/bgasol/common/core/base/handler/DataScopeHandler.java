package com.bgasol.common.core.base.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.user.api.UserApi;
import com.bgasol.model.system.user.bo.ScopeField;
import com.bgasol.model.system.user.bo.ScopeOptionsBo;
import com.bgasol.model.system.user.entity.UserEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
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

    private final EntityFieldCache entityFieldCache;
    private final UserApi userApi;

    private static final List<String> MyBaseMapperMethodNameList = Arrays.stream(MyBaseMapper.class.getDeclaredMethods()).map(Method::getName).toList();

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // 获取 MyBaseMapper 的所有方法名
        boolean isMyBaseMapperMethod = MyBaseMapperMethodNameList.stream().anyMatch(mappedStatementId::endsWith);
        if (isMyBaseMapperMethod) {
            // 忽略 MyBaseMapper 的自定义方法
            return null;
        }
        ScopeOptionsBo scopeOption;
        try {
            Class<? extends BaseEntity> entityClass = (Class<? extends BaseEntity>) entityFieldCache.tableClassCache.get(table.getName());
            if (entityClass == null) {
                return null;
            }
            scopeOption = getScopeOption(entityClass);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e);
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
        Expression finalExpression = null;

        if (scopeOption.isUser()) {
            EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(new Column(table, scopeOption.getUserColumnName()));
            equalsTo.setRightExpression(new StringValue(userId));
            finalExpression = equalsTo;
        }

        if (scopeOption.isUserList()) {
            PlainSelect plainSelect = new PlainSelect();
            plainSelect.addSelectItems(new SelectItem<>(new StringValue("1")));

            Table subTable = new Table(scopeOption.getUserListTableName());
            plainSelect.setFromItem(subTable);

            EqualsTo joinCondition = new EqualsTo();
            joinCondition.setLeftExpression(new Column(subTable, scopeOption.getUserListJoinColumnName()));
            joinCondition.setRightExpression(new Column(table, "id"));

            EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(new Column(subTable, scopeOption.getUserListInverseJoinColumnName()));
            equalsTo.setRightExpression(new StringValue(userId));

            plainSelect.setWhere(new AndExpression(joinCondition, equalsTo));

            ParenthesedSelect parenthesedSelect = new ParenthesedSelect();
            parenthesedSelect.setSelect(plainSelect);
            ExistsExpression existsExpression = new ExistsExpression();
            existsExpression.setRightExpression(parenthesedSelect);

            finalExpression = finalExpression == null ? existsExpression : new AndExpression(finalExpression, existsExpression);
        }

        UserEntity loginUser = userApi.findById(userId).getData();
        List<String> departmentIds = loginUser.getDepartment()
                .getAllChildren()
                .stream()
                .map(DepartmentEntity::getId)
                .toList();
        if (scopeOption.isDepartment()) {
            InExpression inExpression = new InExpression();
            inExpression.setLeftExpression(new Column(table, scopeOption.getDepartmentColumnName()));
            inExpression.setRightExpression(
                    new ParenthesedExpressionList<>(departmentIds
                            .stream()
                            .map(StringValue::new)
                            .toList()));

            finalExpression = finalExpression == null ? inExpression : new AndExpression(finalExpression, inExpression);
        }

        if (scopeOption.isDepartmentList()) {
            PlainSelect plainSelect = new PlainSelect();
            plainSelect.addSelectItems(new SelectItem<>(new StringValue("1")));

            Table subTable = new Table(scopeOption.getDepartmentListTableName());
            plainSelect.setFromItem(subTable);

            EqualsTo joinCondition = new EqualsTo();
            joinCondition.setLeftExpression(new Column(subTable, scopeOption.getDepartmentListJoinColumnName()));
            joinCondition.setRightExpression(new Column(table, "id"));

            InExpression inCondition = new InExpression();
            inCondition.setLeftExpression(new Column(subTable, scopeOption.getDepartmentListInverseJoinColumnName()));
            inCondition.setRightExpression(new ParenthesedExpressionList<>(
                    new ParenthesedExpressionList<>(departmentIds
                            .stream()
                            .map(StringValue::new)
                            .toList()))
            );

            plainSelect.setWhere(new AndExpression(joinCondition, inCondition));

            ParenthesedSelect parenthesedSelect = new ParenthesedSelect();
            parenthesedSelect.setSelect(plainSelect);
            ExistsExpression existsExpression = new ExistsExpression();
            existsExpression.setRightExpression(parenthesedSelect);
            finalExpression = finalExpression == null ? existsExpression : new AndExpression(finalExpression, existsExpression);
        }
        return finalExpression;
    }

    private ScopeOptionsBo getScopeOption(Class<? extends BaseEntity> entityClass) {
        ScopeOptionsBo scopeOptions = new ScopeOptionsBo();
        if (entityClass == null) {
            return scopeOptions;
        }
        Field[] fields = FieldUtils.getAllFields(entityClass);
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            // 忽略没有 ScopeField 注解的字段
            if (!field.isAnnotationPresent(ScopeField.class)) {
                continue;
            }
            if (ClassUtils.isAssignable(fieldType, List.class)) {
                if (!(field.getGenericType() instanceof ParameterizedType parameterizedType)) {
                    continue;
                }
                // 获取泛型参数
                Type[] typeArgs = parameterizedType.getActualTypeArguments();
                if (ArrayUtils.isNotEmpty(typeArgs) && typeArgs[0] instanceof Class<?> listType) {
                    if (ClassUtils.isAssignable(listType, DepartmentEntity.class)) {
                        JoinTable joinTable = field.getAnnotation(JoinTable.class);
                        scopeOptions.setDepartmentListTableName(joinTable.name());
                        scopeOptions.setDepartmentListJoinColumnName(joinTable.joinColumns()[0].name());
                        scopeOptions.setDepartmentListInverseJoinColumnName(joinTable.inverseJoinColumns()[0].name());
                        scopeOptions.setDepartmentList(true);
                    } else if (ClassUtils.isAssignable(listType, UserEntity.class)) {
                        JoinTable joinTable = field.getAnnotation(JoinTable.class);
                        scopeOptions.setUserListTableName(joinTable.name());
                        scopeOptions.setUserListJoinColumnName(joinTable.joinColumns()[0].name());
                        scopeOptions.setUserListInverseJoinColumnName(joinTable.inverseJoinColumns()[0].name());
                        scopeOptions.setUserList(true);
                    }
                }
            } else {
                if (ClassUtils.isAssignable(fieldType, DepartmentEntity.class)) {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    scopeOptions.setDepartmentColumnName(joinColumn.name());
                    scopeOptions.setDepartment(true);
                } else if (ClassUtils.isAssignable(fieldType, UserEntity.class)) {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    scopeOptions.setUserColumnName(joinColumn.name());
                    scopeOptions.setUser(true);
                }
            }
        }
        return scopeOptions;
    }
}