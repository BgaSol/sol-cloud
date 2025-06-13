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
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.bgasol.common.constant.value.RedisConfigValues.DEFAULT_TIME_UNIT;
import static com.bgasol.common.constant.value.RedisConfigValues.randomizeTtl;

@Transactional
@Slf4j
@Service
public abstract class BaseService<ENTITY extends BaseEntity, PAGE_DTO extends BasePageDto<ENTITY>> {

    abstract public MyBaseMapper<ENTITY> commonBaseMapper();

    @Value("${spring.application.name}")
    private String serviceName;

    public RedissonClient commonBaseRedissonClient() {
        return null;
    }

    /**
     * 获取ENTITY实体类的Class对象
     *
     * @return ENTITY实体类的Class对象
     */
    @SuppressWarnings("unchecked")
    public Class<ENTITY> commonBaseEntityClass() {
        return (Class<ENTITY>) ResolvableType.forClass(getClass()).as(BaseService.class).getGeneric(0).resolve();
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
        // 删除缓存
        this.cacheDelete(entity.getId());
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
        ENTITY queryEntity = cacheSearch(entity.getId());
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
        // 删除缓存
        this.cacheDelete(entity.getId());
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
        ENTITY entity = this.cacheSearch(id);
        if (entity == null) {
            throw new BaseException("删除失败，删除数据不存在");
        }
        int i = commonBaseMapper().deleteById(id);
        this.cacheDelete(id);
        return i;
    }

    /**
     * 根据id查询实体
     *
     * @param id 实体id
     * @return 实体
     */
    @Transactional(readOnly = true)
    public ENTITY findById(String id) {
        ENTITY entity = this.cacheSearch(id);
        if (ObjectUtils.isNotEmpty(entity)) {
            this.findOtherTable(entity);
        }
        return entity;
    }

    @Transactional(readOnly = true)
    public List<ENTITY> findIds(String... ids) {
        // 优先从缓存查询结果
        if (ObjectUtils.isEmpty(commonBaseRedissonClient())) {
            if (ObjectUtils.isEmpty(ids)) {
                return new ArrayList<>();
            }
            return commonBaseMapper().selectByIds(Arrays.asList(ids));
        }

        // 先获取缓存中的结果
        RMapCache<String, ENTITY> mapCache = getRMapCache();
        Map<String, ENTITY> cacheList = mapCache.getAll(Set.of(ids));

        // 缓存中没有的查询数据库
        List<String> noneCacheIds = Arrays.stream(ids).filter(id -> !cacheList.containsKey(id)).toList();
        List<ENTITY> entities;
        if (ObjectUtils.isEmpty(noneCacheIds)) {
            entities = new ArrayList<>();
        } else {
            entities = commonBaseMapper().selectByIds(noneCacheIds);
        }
        // 准备缓存的新数据 数据库中也没有查到的数据制作为NULL_PLACEHOLDER实体
        Map<String, ENTITY> toCacheDate = noneCacheIds.stream().collect(Collectors.toMap(id -> id,
                id -> entities.stream().filter(entity -> entity.getId().equals(id)).findFirst().orElse(
                        (ENTITY) BaseEntity.builder().id(NULL_PLACEHOLDER).build()))
        );
        // 将数据库查询的结果，缓存到redis中
        mapCache.putAll(toCacheDate, randomizeTtl(), DEFAULT_TIME_UNIT);

        // 合并缓存和数据库的结果 （将防止缓存穿透的空对象扔掉）
        List<ENTITY> result = new ArrayList<>(cacheList.values().stream().filter(entity -> !entity.getId().equals(NULL_PLACEHOLDER)).toList());
        result.addAll(entities);

        this.findOtherTable(result);
        return result;
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

        // 缓存查询结果
        if (ObjectUtils.isNotEmpty(commonBaseRedissonClient())) {
            RMapCache<String, ENTITY> mapCache = getRMapCache();
            mapCache.putAll(
                    entityPage.getRecords().stream().collect(Collectors.toMap(BaseEntity::getId, entity -> entity)),
                    randomizeTtl(), DEFAULT_TIME_UNIT);
        }
        // 查询关联的数据
        this.findOtherTable(entityPage.getRecords());

        return PageVo.<ENTITY>builder()
                .total(entityPage.getTotal())
                .page(entityPage.getCurrent())
                .size(entityPage.getSize())
                .result(entityPage.getRecords())
                .build();
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

        // 缓存查询结果
        if (ObjectUtils.isNotEmpty(commonBaseRedissonClient())) {
            RMapCache<String, ENTITY> mapCache = getRMapCache();
            mapCache.putAll(
                    entities.stream().collect(Collectors.toMap(BaseEntity::getId, entity -> entity)),
                    randomizeTtl(), DEFAULT_TIME_UNIT);
        }
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

        // 缓存查询结果
        if (ObjectUtils.isNotEmpty(commonBaseRedissonClient())) {
            RMapCache<String, ENTITY> mapCache = getRMapCache();
            mapCache.putAll(
                    entities.stream().collect(Collectors.toMap(BaseEntity::getId, entity -> entity)),
                    randomizeTtl(), DEFAULT_TIME_UNIT);
        }

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
        if (ObjectUtils.isEmpty(list)) {
            return;
        }

        list.parallelStream().forEach(this::findOtherTable);
    }

    /**
     * Service需要做关联查询的，重写它就可以。
     * 子类如果有关联查询需求，可以继承重写该方法
     * 重写这个方法要注意性能问题
     *
     * @param entity 实体
     */
    public void findOtherTable(ENTITY entity) {
        // 暂时什么也不用做
    }


    private RMapCache<String, ENTITY> getRMapCache() {
        String className = commonBaseEntityClass().getName();
        String key = serviceName + ":" + className;
        return commonBaseRedissonClient().getMapCache(key);
    }

    private static final String NULL_PLACEHOLDER = "null";

    /**
     * 缓存查询
     */
    public ENTITY cacheSearch(String id) {
        if (ObjectUtils.isEmpty(commonBaseRedissonClient())) {
            return commonBaseMapper().selectById(id);
        }
        RMapCache<String, ENTITY> mapCache = getRMapCache();
        if (mapCache.containsKey(id)) {
            ENTITY entity = mapCache.get(id);
            if (entity.getId().equals(NULL_PLACEHOLDER)) {
                return null;
            } else {
                return entity;
            }
        } else {
            ENTITY entity = commonBaseMapper().selectById(id);
            // 如果是null就存储一个id为"null"的字符串做标记 防止穿透
            if (entity == null) {
                mapCache.put(id, (ENTITY) BaseEntity.builder().id(NULL_PLACEHOLDER).build(), randomizeTtl(), DEFAULT_TIME_UNIT);
            } else {
                mapCache.put(id, entity, randomizeTtl(), DEFAULT_TIME_UNIT);
            }
            return entity;
        }
    }

    /**
     * 删除缓存
     */
    public void cacheDelete(String id) {
        if (ObjectUtils.isNotEmpty(commonBaseRedissonClient())) {
            RMapCache<String, ENTITY> mapCache = getRMapCache();
            mapCache.remove(id);
        }
    }
}
