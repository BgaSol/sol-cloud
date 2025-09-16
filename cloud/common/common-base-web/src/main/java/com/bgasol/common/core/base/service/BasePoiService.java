package com.bgasol.common.core.base.service;

import cn.idev.excel.EasyExcel;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.listener.BaseExcelImportListener;
import com.bgasol.common.core.base.vo.ImportResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Transactional
@Slf4j
@Service
public abstract class BasePoiService<
        ENTITY extends BaseEntity,
        PAGE_DTO extends BasePageDto<ENTITY>,
        CREATE_DTO extends BaseCreateDto<ENTITY>,
        UPDATE_DTO extends BaseUpdateDto<ENTITY>> extends BaseService<ENTITY, PAGE_DTO> {
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
