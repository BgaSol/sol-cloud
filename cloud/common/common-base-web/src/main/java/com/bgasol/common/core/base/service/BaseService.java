package com.bgasol.common.core.base.service;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.core.base.vo.PageVo;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public abstract class BaseService<ENTITY extends BaseEntity, PAGE_DTO extends BasePageDto<ENTITY>> {


    abstract public MyBaseMapper<ENTITY> commonBaseMapper();

    public RedissonClient commonBaseRedissonClient() {
        return null;
    }

    /**
     * 向数据库插入实体记录。
     * <p>
     * 此方法处理以下操作：
     * <ul>
     *   <li>对于标注了 {@link Transient}、以 "_id" 结尾且具有 {@link TableField} 注解的字段，
     *       如果值为空字符串则设置为 null</li>
     *   <li>插入主实体记录</li>
     *   <li>通过 {@link JoinTable} 关系保存关联实体数据</li>
     * </ul>
     *
     * @param entity 要插入的实体对象，不能为 null
     */
    @Transactional
    public void insert(ENTITY entity) {
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
    }

    /**
     * 根据 ID 更新实体的部分或全部字段。
     * <p>
     * 此方法处理以下操作：
     * <ul>
     *   <li>验证实体 ID 是否存在</li>
     *   <li>更新非 null 和非 undefined 的实体字段</li>
     *   <li>将 {@link Transient} ID 字段的空字符串值设置为 null</li>
     *   <li>同步 {@link JoinTable} 关联数据（先删除后重新插入）</li>
     * </ul>
     * <p>
     * 注意：后端默认不更新 {@code undefined} 或 {@code null} 的值。
     * 如需使用默认更新行为，应在前端调用 {@code buildDto}，
     * 将 DTO 中的 {@code undefined} 和 {@code null} 值替换为默认值
     * （通常为空字符串、空数组等）。
     *
     * @param entity 包含更新数据的实体对象，必须具有有效的 ID
     * @throws BaseException 当实体 ID 为 null、实体不存在或字段访问失败时抛出
     */
    @Transactional
    public void apply(ENTITY entity) {
        if (ObjectUtils.isEmpty(entity.getId())) {
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
        if (commonBaseMapper().update(entity, updateWrapper) == 0) {
            throw new BaseException("更新失败，更新数据不存在");
        }
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
                    // 重新插入中间表数据
                    insertIntoTable(entity, tableName, masterName, slaveName, entities);
                }
            }
        }
    }

    /**
     * 根据 ID 集合批量删除实体。
     *
     * @param ids 要删除的实体 ID 集合
     * @return 被删除的实体数量
     */
    @Transactional
    public Integer delete(Set<String> ids) {
        return this.commonBaseMapper().deleteByIds(ids);
    }

    /**
     * 分页查询实体列表，可选择是否加载关联数据。
     *
     * @param pageDto   分页参数和查询条件封装对象
     * @param otherData 如果为 {@code true}，则为每个实体加载 {@link JoinTable} 关联数据
     * @return 包含实体列表和分页元数据的分页结果对象
     * @see PageVo
     */
    @Transactional(readOnly = true)
    public PageVo<ENTITY> findByPage(PAGE_DTO pageDto, boolean otherData) {
        // 构件分页查询条件
        Page<ENTITY> page = new Page<>(pageDto.getPage(), pageDto.getSize());
        // 查询关联的数据
        Page<ENTITY> entityPage = this.findByPage(page, pageDto.getQueryWrapper(), otherData);
        return PageVo.<ENTITY>builder().total(entityPage.getTotal()).page(entityPage.getCurrent()).size(entityPage.getSize()).result(entityPage.getRecords()).build();
    }

    /**
     * 分页查询实体列表，可选择是否加载关联数据。
     *
     * @param page         分页对象，包含当前页码和每页大小
     * @param queryWrapper 查询条件包装器（可为 null，表示无条件查询）
     * @param otherData    如果为 {@code true}，则为每个实体加载 {@link JoinTable} 关联数据
     * @return 分页结果，包含实体列表
     * @see Page
     */
    @Transactional(readOnly = true)
    public Page<ENTITY> findByPage(Page<ENTITY> page, Wrapper<ENTITY> queryWrapper, boolean otherData) {
        Page<ENTITY> entityPage = commonBaseMapper().selectPage(page, queryWrapper);
        if (otherData) {
            this.findOtherTable(entityPage.getRecords());
        }
        return entityPage;
    }

    /**
     * 根据 ID 集合查询实体列表，可选择是否加载关联数据。
     *
     * @param ids       要查询的实体 ID 集合
     * @param otherData 如果为 {@code true}，则为每个实体加载 {@link JoinTable} 关联数据
     * @return 匹配 ID 的实体列表（如果 ID 为 null 或空，则返回空列表）
     * @see BaseEntity
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findById(Set<String> ids, boolean otherData) {
        if (ObjectUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<ENTITY> entities = commonBaseMapper().selectByIds(ids);
        if (otherData) {
            this.findOtherTable(entities);
        }
        return entities;
    }

    /**
     * 根据单个 ID 查询实体，可选择是否加载关联数据。
     *
     * @param id        要查询的实体 ID
     * @param otherData 如果为 {@code true}，则为该实体加载 {@link JoinTable} 关联数据
     * @return 如果找到则返回实体对象，否则返回 null
     * @see BaseEntity
     */
    @Transactional(readOnly = true)
    public ENTITY findById(String id, Boolean otherData) {
        List<ENTITY> entities = this.findById(Set.of(id), otherData);
        return ObjectUtils.isEmpty(entities) ? null : entities.get(0);
    }


    /**
     * 查询所有实体并加载关联数据。
     *
     * @return 所有实体列表，包含已加载的关联数据
     * @see #findAll(Wrapper, Boolean)
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findAll(boolean otherData) {
        return this.findAll(null, otherData);
    }

    /**
     * 根据查询条件查询所有实体，可选择是否加载关联数据。
     *
     * @param wrapper   查询条件包装器（可为 null，表示无条件查询）
     * @param otherData 如果为 {@code true}，则为每个实体加载 {@link JoinTable} 关联数据
     * @return 匹配查询条件的实体列表
     * @see Wrapper
     */
    @Transactional(readOnly = true)
    public List<ENTITY> findAll(Wrapper<ENTITY> wrapper, Boolean otherData) {
        List<ENTITY> entities = commonBaseMapper().selectList(wrapper);
        if (otherData) {
            this.findOtherTable(entities);
        }
        return entities;
    }

    /**
     * 为批量的实体加载关联数据。
     * <p>
     * 此方法使用流式并行处理实体列表。
     * 子类如有性能优化需求可重写此方法。
     * 否则建议重写 {@link #findOtherTable(BaseEntity)} 方法进行单条记录查询。
     *
     * @param list 要加载关联数据的实体列表
     *             （如果列表为 null 或空，方法会立即返回）
     */
    @Transactional(readOnly = true)
    public void findOtherTable(List<ENTITY> list) {
        if (ObjectUtils.isEmpty(list)) {
            return;
        }
        list.parallelStream().forEach(this::findOtherTable);
    }

    /**
     * 为单个实体加载关联数据。
     * <p>
     * 当子类需要查询关联表数据时应重写此方法。
     * 在批处理过程中，此方法会被每个实体调用。
     * <p>
     * <strong>性能提示：</strong> 重写此方法时要注意性能影响，
     * 特别是在处理大量数据时。
     * 尽可能考虑使用批量操作或优化查询策略。
     *
     * @param entity 要加载关联数据的实体对象
     */
    @Transactional(readOnly = true)
    public void findOtherTable(ENTITY entity) {
        // 暂时什么也不用做
    }

    /**
     * 批量查询中间表（关联表）数据。
     *
     * @param tableName    中间表/关联表的表名
     * @param masterName   中间表中主键列的列名
     * @param masterValues 要查询的主键值列表
     * @param slaveName    中间表中从键/关联键列的列名
     * @return 返回 Map 结构，key 为主键值，value 为关联的从键值列表
     * （如果 masterValues 为 null 或空，则返回空 Map）
     * @example 返回格式示例：{主键值 1: [从键值 1, 从键值 2], 主键值 2: [从键值 3]}
     */
    @Transactional(readOnly = true)
    public Map<String, List<String>> findFromTableBatch(String tableName, String masterName, List<String> masterValues, String slaveName) {
        // 检查 masterValues 空值
        if (ObjectUtils.isEmpty(masterValues)) {
            return new HashMap<>();
        }
        List<Map<String, String>> fromTableBatch = this.commonBaseMapper().findFromTableBatch(tableName, masterName, masterValues, slaveName);
        // 根据主键值分组
        return fromTableBatch.stream().collect(Collectors.groupingBy(map -> map.get(masterName), Collectors.mapping(map -> map.get(slaveName), Collectors.toList())));
    }

    /**
     * 批量插入中间表/关联表数据。
     *
     * @param tableName  中间表/关联表的表名
     * @param masterName 中间表中主键列的列名
     * @param slaveName  中间表中从键/关联键列的列名
     * @param values     要插入的主从关系键值对列表
     *                   （如果 values 为 null 或空，方法会立即返回）
     * @example 键值对格式：Map.entry(主键 ID, 从键 ID)
     */
    @Transactional
    public void insertIntoTableBatch(String tableName, String masterName, String slaveName, List<Map.Entry<String, String>> values) {
        if (ObjectUtils.isEmpty(values)) {
            return;
        }
        List<Map<String, String>> inserList = values.stream().map(entry -> Map.of("masterValue", entry.getKey(), "slaveValue", entry.getValue())).toList();
        this.commonBaseMapper().insertIntoTableBatch(tableName, masterName, slaveName, inserList);
    }

    /**
     * 为实体插入中间表/关联表数据。
     *
     * @param entity     主实体对象，其 ID 将作为主键值使用
     * @param tableName  中间表/关联表的表名
     * @param masterName 中间表中主键列的列名
     * @param slaveName  中间表中从键/关联键列的列名
     * @param value      要关联到主实体的从实体列表
     *                   （每个从实体的 ID 将作为从键值使用）
     */
    @Transactional
    public void insertIntoTable(ENTITY entity, String tableName, String masterName, String slaveName, List<BaseEntity> value) {
        List<Map.Entry<String, String>> insertList = value.stream().map(childrenEntity -> new AbstractMap.SimpleEntry<>(entity.getId(), childrenEntity.getId())).collect(Collectors.toList());

        insertIntoTableBatch(tableName, masterName, slaveName, insertList);
    }

    /**
     * 获取 Excel 导入的行级校验器。
     * <p>
     * 此方法提供 Excel 导入操作的行级验证逻辑。
     * 校验器接收每个实体和错误消息列表，如果验证通过返回 {@code true}，
     * 否则返回 {@code false}。
     * <p>
     * 默认情况下不执行任何验证。子类可以重写此方法
     * 为其特定的实体类型提供自定义验证逻辑。
     *
     * @return 用于 Excel 导入验证的 BiPredicate 函数，
     * 如果未配置验证则返回 {@code null}（默认情况）
     */
    public BiPredicate<ENTITY, List<String>> importValidator() {
        return null;
    }

    /**
     * 根据id查询实体
     * 有关联查询
     */
    @Deprecated
    @Transactional
    public ENTITY findById(String id) {
        return this.findById(id, true);
    }

    /**
     * 根据id查询实体
     * 有关联查询
     */
    @Deprecated
    @Transactional(readOnly = true)
    public List<ENTITY> findByIds(String... idArray) {
        return this.findById(Arrays.stream(idArray).collect(Collectors.toSet()), true);
    }

    /**
     * 分页查询
     * 有关联查询
     */
    @Deprecated
    @Transactional(readOnly = true)
    public PageVo<ENTITY> findByPage(PAGE_DTO pageDto) {
        return this.findByPage(pageDto, true);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public PageVo<ENTITY> findByPage(Page<ENTITY> page, Wrapper<ENTITY> queryWrapper) {
        // 查询关联的数据
        Page<ENTITY> entityPage = this.findByPage(new Page<>(page.getPages(), page.getSize()), queryWrapper, true);
        return PageVo.<ENTITY>builder().total(entityPage.getTotal()).page(entityPage.getCurrent()).size(entityPage.getSize()).result(entityPage.getRecords()).build();
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<ENTITY> findAll() {
        return this.findAll(null, true);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<ENTITY> findAll(Wrapper<ENTITY> wrapper) {
        return this.findAll(wrapper, true);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public ENTITY findDirectById(String id) {
        return this.findById(id, false);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<ENTITY> findDirectByIds(String... idArray) {
        return this.findById(Arrays.stream(idArray).collect(Collectors.toSet()), false);
    }

    /// 改为调用 findDirectById
    @Deprecated
    @Transactional(readOnly = true)
    public ENTITY cacheSearch(String id) {
        return this.findDirectById(id);
    }

    /// 改为调用 findByIds
    @Deprecated
    @Transactional(readOnly = true)
    public List<ENTITY> findIds(String... ids) {
        return this.findByIds(ids);
    }

    /**
     * 建议使用 insert
     * 保存实体
     * 有关联查询
     * 如果实体有中间表，也会保存中间表
     */
    @Deprecated
    @Transactional
    public ENTITY save(ENTITY entity) {
        this.insert(entity);
        return this.findById(entity.getId());
    }

    /**
     * 建议使用 apply
     * 更新实体
     * 有关联查询
     * 如果实体有中间表，也会保存中间表
     */
    @Deprecated
    @Transactional
    public ENTITY update(ENTITY entity) {
        this.apply(entity);
        return this.findById(entity.getId());
    }

}
