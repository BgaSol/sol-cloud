package com.bgasol.common.core.base.service;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.core.base.vo.PageVo;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

@Transactional
@Slf4j
public abstract class BaseService<ENTITY extends BaseEntity, PAGE_DTO extends BasePageDto<ENTITY>> {

    abstract public MyBaseMapper<ENTITY> commonBaseMapper();

    /**
     * 获取ENTITY实体类的Class对象
     *
     * @return ENTITY实体类的Class对象
     */
    public Class<ENTITY> commonBaseEntityClass() {
        Class<?> cls = getClass();
        while (!cls.getSuperclass().equals(BaseService.class)) {
            cls = cls.getSuperclass();
        }
        ParameterizedType type = (ParameterizedType) cls.getGenericSuperclass();
        @SuppressWarnings("unchecked") Class<ENTITY> entityClass = (Class<ENTITY>) type.getActualTypeArguments()[0];
        return entityClass;
    }

    /**
     * 保存实体
     * 如果实体有中间表，也会保存中间表
     *
     * @param entity 实体
     * @return 实体
     */
    public ENTITY save(ENTITY entity) {
        // 反射获取entity的所有字段
        Class<? extends BaseEntity> entityClass = entity.getClass();
        // Field[] fields = entityClass.getDeclaredFields();
        List<Field> fields = FieldUtils.getAllFieldsList(entityClass);
        // 检索所有的joinColumn字段为空字符串的字段，将其设置为null
        for (Field field : fields) {
            // 判断字段是否有注解Transient并且字段尾部是id结尾并且有TableField注解
            if (field.isAnnotationPresent(Transient.class) && field.getName().toLowerCase().endsWith("_id") && field.isAnnotationPresent(TableField.class)) {
                // 将字段设为可访问
                field.setAccessible(true);
                Object value;
                try {
                    value = field.get(entity);
                } catch (IllegalAccessException e) {
                    log.error("获取字段值失败", e);
                    throw new BaseException("获取字段值失败");
                }
                // 如果值是空字符串，将其设置为null
                if ("".equals(value)) {
                    try {
                        field.set(entity, null);
                    } catch (IllegalAccessException e) {
                        log.error("设置字段值失败", e);
                        throw new BaseException("设置字段值失败");
                    }
                }
            }
        }
        // 插入实体
        commonBaseMapper().insert(entity);
        for (Field field : fields) {
            // 判断字段是否有注解JoinTable
            if (field.isAnnotationPresent(JoinTable.class)) {
                // 获取字段的JoinTable注解
                JoinTable joinTable = field.getAnnotation(JoinTable.class);
                // 获取中间表的表名
                String tableName = joinTable.name();
                // 获取中间表字段
                String masterName = joinTable.joinColumns()[0].name();
                String slaveName = joinTable.inverseJoinColumns()[0].name();
                // 获取中间表字段的值
                field.setAccessible(true);
                Object value;
                try {
                    value = field.get(entity);
                } catch (IllegalAccessException e) {
                    log.error("获取字段值失败", e);
                    throw new BaseException("获取字段值失败");
                }
                if (value != null) {
                    @SuppressWarnings("unchecked") List<BaseEntity> entities = (List<BaseEntity>) value;
                    insertIntoTable(entity, tableName, masterName, slaveName, entities);
                }
            }
        }
        return this.findById(entity.getId());
    }

    /**
     * 更新实体
     * 如果实体有中间表，也会更新中间表
     * <p>
     * 后端默认不更新 undefined 和 null 的值
     * 使用默认更新时使用应在前端调用 buildDto
     * 将 dto 中的 undefined 和 null 值去掉 替换为默认值 默认值一般为空字符串空数组等
     *
     * @param entity 实体
     * @return 实体
     */
    public ENTITY update(ENTITY entity) {
        ENTITY queryEntity = commonBaseMapper().selectById(entity.getId());
        if (queryEntity == null) {
            throw new BaseException("更新失败，更新数据不存在");
        }
        // 反射获取entity的所有字段
        Class<? extends BaseEntity> entityClass = entity.getClass();
        List<Field> fields = FieldUtils.getAllFieldsList(entityClass);
        UpdateWrapper<ENTITY> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", entity.getId());
        // 检索所有的joinColumn字段为空字符串的字段，将其设置为null
        // 处理关联表字段 JoinColumn 字段为空字符串的情况
        for (Field field : fields) {
            // 判断字段是否有注解Transient并且字段尾部是id结尾并且有TableField注解
            if (field.isAnnotationPresent(Transient.class) && field.getName().toLowerCase().endsWith("id") && field.isAnnotationPresent(TableField.class)) {
                // 获取TableField注解
                TableField tableField = field.getAnnotation(TableField.class);
                // 获取字段名
                String tableFieldName = tableField.value();
                // 获取字段的值
                field.setAccessible(true);
                Object value;
                try {
                    value = field.get(entity);
                } catch (IllegalAccessException e) {
                    log.error("获取字段值失败", e);
                    throw new BaseException("获取字段值失败");
                }
                if ("".equals(value)) {
                    try {
                        field.set(entity, null);
                    } catch (IllegalAccessException e) {
                        log.error("设置字段值失败", e);
                        throw new BaseException("设置字段值失败");
                    }
                    updateWrapper.set(tableFieldName, null);
                }
            }
        }
        // 更新实体
        commonBaseMapper().update(entity, updateWrapper);
        // 反射获取entity的所有字段
        for (Field field : fields) {
            // 判断字段是否有注解
            if (field.isAnnotationPresent(JoinTable.class)) {
                // 获取字段的JoinTable注解
                JoinTable joinTable = field.getAnnotation(JoinTable.class);
                // 获取中间表的表名
                String tableName = joinTable.name();
                // 获取中间表字段
                String masterName = joinTable.joinColumns()[0].name();
                String slaveName = joinTable.inverseJoinColumns()[0].name();
                // 获取中间表字段的值
                field.setAccessible(true);
                Object value;
                try {
                    value = field.get(entity);
                } catch (IllegalAccessException e) {
                    log.error("获取字段值失败", e);
                    throw new BaseException("获取字段值失败");
                }
                if (value != null) {
                    // 删除中间表的数据
                    commonBaseMapper().deleteFromTable(tableName, masterName, entity.getId());
                    @SuppressWarnings("unchecked") List<BaseEntity> entities = (List<BaseEntity>) value;
                    insertIntoTable(entity, tableName, masterName, slaveName, entities);
                }
            }
        }
        return this.findById(entity.getId());
    }

    /**
     * 插入中间表
     */
    private void insertIntoTable(ENTITY entity, String tableName, String masterName, String slaveName, List<BaseEntity> value) {
        for (BaseEntity childrenEntity : value) {
            // 获取中间表字段的值
            String masterValue = entity.getId();
            String slaveValue = childrenEntity.getId();
            // 插入中间表
            commonBaseMapper().insertIntoTable(tableName, masterName, masterValue, slaveName, slaveValue);
        }
    }

    /**
     * 删除实体
     *
     * @param ids 实体id
     * @return 删除数量
     */
    public Integer[] delete(String... ids) {
        return Arrays.stream(ids).map(this::delete).toArray(Integer[]::new);
    }

    public Integer delete(String id) {
        ENTITY entity = commonBaseMapper().selectById(id);
        if (entity == null) {
            throw new BaseException("删除失败，删除数据不存在");
        }
        return commonBaseMapper().deleteById(id);
    }

    /**
     * 根据id查询实体
     *
     * @param id 实体id
     * @return 实体
     */
    @Transactional(readOnly = true)
    public ENTITY findById(String id) {
        ENTITY entity = commonBaseMapper().selectById(id);
        this.findOtherTable(entity);
        return entity;
    }

    /**
     * 分页查询
     *
     * @param pageDto 分页查询条件
     * @return 分页数据
     */
    @Transactional(readOnly = true)
    public PageVo<ENTITY> findByPage(PAGE_DTO pageDto) {
        // 获取分页条件
        Page<ENTITY> page = new Page<>(pageDto.getPage(), pageDto.getSize());

        // 执行分页查询
        Page<ENTITY> entityPage = commonBaseMapper().selectPage(page, pageDto.getQueryWrapper());

        // 查询关联的数据
        this.findOtherTable(entityPage.getRecords());

        return new PageVo<>(entityPage.getTotal(), entityPage.getRecords(), entityPage.getCurrent(), entityPage.getSize());
    }

    /**
     * 查询所有实体
     *
     * @return 实体列表
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findAll() {
        return this.findAll(null);
    }

    /**
     * 查询所有实体
     * 如果实体是树形结构，查询所有根节点
     *
     * @return 实体列表
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findAll(QueryWrapper<ENTITY> queryWrapper) {
        // 判断ENTITY是否是树形结构
        if (BaseTreeEntity.class.isAssignableFrom(commonBaseEntityClass())) {
            return this.findTreeAll(null, queryWrapper);
        }
        List<ENTITY> entities = commonBaseMapper().selectList(queryWrapper);
        this.findOtherTable(entities);
        return entities;
    }

    /**
     * 查询所有树形结构
     *
     * @param parentId 父id
     * @return 树形结构列表
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findTreeAll(String parentId, QueryWrapper<ENTITY> queryWrapper) {
        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper<>();
        }
        if (parentId == null) {
            queryWrapper.isNull("parent_id");
        } else {
            queryWrapper.eq("parent_id", parentId);
        }
        List<ENTITY> entities = commonBaseMapper().selectList(queryWrapper);
        this.findOtherTable(entities);
        for (ENTITY entity : entities) {
            BaseTreeEntity treeEntity = (BaseTreeEntity) entity;
            treeEntity.setChildren(this.findTreeAll(treeEntity.getId(), null));
        }
        return entities;
    }

    /**
     * 关联数据查询
     * 子类如果有性能需求，可以重写该方法
     * 不然直接重写 单条记录查询的方法
     */
    @Transactional(readOnly = true)
    public void findOtherTable(List<ENTITY> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        list.parallelStream().forEach(this::findOtherTable);
    }

    /**
     * 每个Service需要做关联查询的，重写它就可以。
     * 子类如果有关联查询需求，可以继承重写该方法
     * 重写这个方法要注意性能问题
     *
     * @param entity 实体
     */
    public void findOtherTable(ENTITY entity) {
        // 暂时什么也不用做
    }

    /**
     * 清空表内所有数据
     */
    public void truncateTable() {
        Class<ENTITY> entityClass = this.commonBaseEntityClass();
        String tableName = entityClass.getAnnotation(Table.class).name();
        this.commonBaseMapper().truncateTable(tableName);
    }
}
