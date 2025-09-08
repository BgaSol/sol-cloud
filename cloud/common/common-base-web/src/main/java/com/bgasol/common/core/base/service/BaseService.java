package com.bgasol.common.core.base.service;

import cn.idev.excel.EasyExcel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.listener.BaseExcelImportListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.core.base.vo.ImportResult;
import com.bgasol.common.core.base.vo.PageVo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

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

    // DTO类缓存，避免重复反射和类加载
    private volatile Class<? extends BaseCreateDto<ENTITY>> cachedCreateDtoClass;

    // 常量定义
    private static final String ENTITY_PACKAGE_SUFFIX = ".entity.";
    private static final String DTO_PACKAGE_SUFFIX = ".dto.";
    private static final String ENTITY_CLASS_SUFFIX = "Entity";
    private static final String CREATE_DTO_CLASS_SUFFIX = "CreateDto";

    /**
     * 创建模板对应的 DTO 类型（自动推断）。
     * 规则：将 ENTITY 的包路径由 ".entity." 替换为 ".dto."，类名后缀由 "Entity" 改为 "CreateDto"。
     * 例如：com.bgasol.model.system.role.entity.RoleEntity -> com.bgasol.model.system.role.dto.RoleCreateDto。
     * 若推断失败或类不存在，则抛出异常。
     *
     * @return 对应的CreateDto类
     * @throws BaseException 当无法获取实体类类型或推断DTO类失败时
     */
    @SuppressWarnings("unchecked")
    protected Class<? extends BaseCreateDto<ENTITY>> commonCreateDtoClass() {
        if (cachedCreateDtoClass == null) {
            synchronized (this) {
                if (cachedCreateDtoClass == null) {
                    @SuppressWarnings("unchecked")
                    Class<? extends BaseCreateDto<ENTITY>> resolved = (Class<? extends BaseCreateDto<ENTITY>>) (Class<?>) resolveDtoClass(BaseCreateDto.class, CREATE_DTO_CLASS_SUFFIX);
                    cachedCreateDtoClass = resolved;
                }
            }
        }
        return cachedCreateDtoClass;
    }

    /**
     * 通用DTO解析：基于实体类全名，将 .entity. 替换为 .dto.，并用指定后缀替换/追加类名。
     * 例如：RoleEntity -> RoleCreateDto / RoleUpdateDto 等。
     *
     * @param dtoSuperType 目标DTO的父类型（用于类型校验）
     * @param targetSuffix 目标DTO类名后缀，例如 "CreateDto"
     * @return 解析并加载后的 DTO Class
     */
    @SuppressWarnings("unchecked")
    protected <T> Class<? extends T> resolveDtoClass(Class<T> dtoSuperType, String targetSuffix) {
        Class<ENTITY> entityClass = commonBaseEntityClass();
        if (entityClass == null) {
            throw new BaseException("无法获取实体类类型，请检查泛型参数配置");
        }

        String entityClassName = entityClass.getName();
        String dtoClassName = inferDtoClassName(entityClassName, targetSuffix);

        try {
            ClassLoader classLoader = entityClass.getClassLoader();
            Class<?> dtoClass = Class.forName(dtoClassName, false, classLoader);
            if (!dtoSuperType.isAssignableFrom(dtoClass)) {
                throw new BaseException(String.format("推断的类 %s 不是 %s 的子类", dtoClassName, dtoSuperType.getSimpleName()));
            }
            return (Class<? extends T>) dtoClass;
        } catch (ClassNotFoundException e) {
            throw new BaseException(String.format("无法找到DTO类: %s（由实体类 %s 推断）", dtoClassName, entityClassName));
        } catch (Exception e) {
            throw new BaseException(String.format("解析DTO类失败: %s（实体类: %s），原因: %s", dtoClassName, entityClassName, e.getMessage()));
        }
    }

    private String inferDtoClassName(String entityClassName, String targetSuffix) {
        String dtoClassName = entityClassName.replace(ENTITY_PACKAGE_SUFFIX, DTO_PACKAGE_SUFFIX);
        if (dtoClassName.endsWith(ENTITY_CLASS_SUFFIX)) {
            int suffixIndex = dtoClassName.length() - ENTITY_CLASS_SUFFIX.length();
            dtoClassName = dtoClassName.substring(0, suffixIndex) + targetSuffix;
        } else {
            dtoClassName += targetSuffix;
        }
        return dtoClassName;
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
            List<ENTITY> entities = commonBaseMapper().selectByIds(Arrays.asList(ids));
            this.findOtherTable(entities);
            return entities;
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
            // 如果没有开启缓存 则直接查询数据库
            return commonBaseMapper().selectById(id);
        }

        RMapCache<String, ENTITY> mapCache = getRMapCache();
        ENTITY entity = mapCache.get(id);

        if (entity != null) {
            if (NULL_PLACEHOLDER.equals(entity.getId())) {
                return null;
            }
            // 缓存命中
            return entity;
        }

        entity = commonBaseMapper().selectById(id);
        if (entity == null) {
            // 查询结果为空 将空值插入缓存
            mapCache.put(id, (ENTITY) BaseEntity.builder().id(NULL_PLACEHOLDER).build(), randomizeTtl(), DEFAULT_TIME_UNIT);
        } else {
            // 将查询结果插入缓存
            mapCache.put(id, entity, randomizeTtl(), DEFAULT_TIME_UNIT);
        }
        return entity;
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

    /**
     * 直接以下载响应的形式返回导入模板
     */
    public ResponseEntity<byte[]> generateImportTemplateResponse() {
        try {
            Schema classSchema = commonBaseEntityClass().getAnnotation(Schema.class);
            String templateName = (classSchema != null && StringUtils.isNotBlank(classSchema.description()))
                    ? classSchema.description().replace("实体", "")
                    : commonBaseEntityClass().getSimpleName().replace("Entity", "");

            List<List<String>> head = buildExcelHeadFromEntity();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            EasyExcel.write(outputStream)
                    .head(head)
                    .sheet("导入模板")
                    .doWrite(new ArrayList<>());

            byte[] bytes = outputStream.toByteArray();
            String fileName = templateName + "_导入模板.xlsx";
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);
            headers.setContentLength(bytes.length);

            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (Exception e) {
            log.error("生成导入模板失败", e);
            throw new BaseException("生成导入模板失败: " + e.getMessage());
        }
    }

    /**
     * 基于实体类 @Schema(description) 注解构建 Excel 单层表头
     * 规则：
     * - 从字段的 @Schema(description) 读取列名
     * - 保持字段声明顺序；忽略无效或空标题字段（不允许重复列名）
     */
    protected List<List<String>> buildExcelHeadFromEntity() {
        Class<?> createDtoClass = commonCreateDtoClass();
        List<Field> fields = FieldUtils.getAllFieldsList(createDtoClass);

        List<String> titles = fields.stream()
                .map(field -> field.getAnnotation(Schema.class))
                .filter(Objects::nonNull)
                .map(Schema::description)
                .filter(StringUtils::isNotBlank)
                .toList();

        if (titles.isEmpty()) {
            throw new BaseException("未能从实体类生成任何列，请检查实体字段与 @Schema(description) 注解配置");
        }

        return titles.stream()
                .map(Collections::singletonList)
                .toList();
    }

    /**
     * 通用导入：根据表头与字段或 @Schema(description) 名称匹配，逐行转换并保存实体
     */
    public ImportResult importFromExcel(MultipartFile file) throws IOException {
        Class<? extends BaseCreateDto<ENTITY>> dtoClass = commonCreateDtoClass();
        BaseExcelImportListener<BaseCreateDto<ENTITY>, ENTITY> listener = BaseExcelImportListener.ofDto(
                importBatchSize(),
                // 目前还是单次插入
                batch -> batch.forEach(this::save),
                this::validateImportedEntity,
                (e, rowIndex) -> log.warn("导入行异常 row={}, ex={}", rowIndex, e.toString())
        );

        EasyExcel.read(file.getInputStream(), dtoClass, listener)
                .headRowNumber(1)
                .sheet()
                .doRead();

        return ImportResult.builder()
                .totalRows(listener.getCurrentRowIndex())
                .successRows(listener.getSuccessRows())
                .errorRows(listener.getErrors().size())
                .errors(listener.getErrors())
                .build();
    }


    /**
     * 实体级导入校验，返回是否通过。默认通过。
     * 可将错误记录到 errors 列表中（如果需要外部收集，可在子类维护）。
     */
    protected boolean validateImportedEntity(ENTITY entity, int rowIndex, List<String> errors) {
        return true;
    }

    /**
     * 批大小，默认 200
     */
    protected int importBatchSize() {
        return 200;
    }


}
