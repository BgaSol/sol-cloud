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
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Excel导入导出基础服务类
 * 提供通用的Excel模板生成和数据导入功能
 *
 * @param <ENTITY>     实体类型
 * @param <PAGE_DTO>   分页查询DTO类型
 * @param <CREATE_DTO> 创建DTO类型
 */
@Transactional
@Slf4j
@Service
public abstract class BasePoiService<
        ENTITY extends BaseEntity,
        PAGE_DTO extends BasePageDto<ENTITY>,
        CREATE_DTO extends BaseCreateDto<ENTITY>,
        UPDATE_DTO extends BaseUpdateDto<ENTITY>> extends BaseService<ENTITY, PAGE_DTO> {
    /**
     * 获取创建DTO的Class对象
     *
     * @return CreateDto类的Class对象
     */
    @SuppressWarnings("unchecked")
    public Class<CREATE_DTO> getCreateDtoClass() {
        return (Class<CREATE_DTO>) ResolvableType.forClass(getClass()).as(BasePoiService.class).getGeneric(2).resolve();
    }

    /**
     * 生成Excel导入模板数据
     *
     * @return Excel模板的字节数组
     */
    public byte[] generateImportTemplateBytes() {
        try {
            List<List<String>> headers = buildExcelHeaders();
            return generateExcelBytes(headers);
        } catch (Exception e) {
            log.error("生成导入模板失败", e);
            throw new BaseException("生成导入模板失败: " + e.getMessage());
        }
    }

    /**
     * 获取导入模板文件名
     *
     * @return 模板文件名
     */
    public String getImportTemplateFileName() {
        return getTemplateName() + "_导入模板.xlsx";
    }

    /**
     * 获取模板名称
     * 子类可以重写此方法自定义模板名称
     *
     * @return 模板名称
     */
    protected String getTemplateName() {
        Schema classSchema = commonBaseEntityClass().getAnnotation(Schema.class);
        if (classSchema != null && StringUtils.isNotBlank(classSchema.description())) {
            return classSchema.description().replace("实体", "");
        }
        return commonBaseEntityClass().getSimpleName().replace("Entity", "");
    }

    /**
     * 生成Excel字节数组
     *
     * @param headers Excel表头
     * @return Excel文件字节数组
     * @throws IOException IO异常
     */
    private byte[] generateExcelBytes(List<List<String>> headers) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream)
                    .head(headers)
                    .sheet("导入模板")
                    .doWrite(new ArrayList<>());
            return outputStream.toByteArray();
        }
    }


    /**
     * 构建Excel表头
     * 基于CreateDTO类的@Schema(description)注解生成表头
     *
     * @return Excel表头列表
     */
    protected List<List<String>> buildExcelHeaders() {
        Class<CREATE_DTO> createDtoClass = getCreateDtoClass();
        List<Field> fields = FieldUtils.getAllFieldsList(createDtoClass);

        List<String> columnTitles = fields.stream()
                .map(field -> field.getAnnotation(Schema.class))
                .filter(Objects::nonNull)
                .map(Schema::description)
                .filter(StringUtils::isNotBlank)
                .toList();

        if (columnTitles.isEmpty()) {
            throw new BaseException("未能从DTO类生成任何列，请检查DTO字段的@Schema(description)注解配置");
        }

        // 转换为EasyExcel需要的格式（每个标题包装在List中）
        return columnTitles.stream()
                .map(Collections::singletonList)
                .toList();
    }

    /**
     * 从Excel文件导入数据
     *
     * @param file Excel文件
     * @return 导入结果
     */
    public ImportResult importFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseException("导入文件不能为空");
        }

        Class<CREATE_DTO> dtoClass = getCreateDtoClass();
        BaseExcelImportListener<CREATE_DTO, ENTITY> listener = createImportListener();

        try {
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
        } catch (Exception e) {
            log.error("Excel导入失败", e);
            throw new BaseException("Excel导入失败: " + e.getMessage());
        }
    }

    /**
     * 创建导入监听器
     *
     * @return 导入监听器
     */
    private BaseExcelImportListener<CREATE_DTO, ENTITY> createImportListener() {
        return BaseExcelImportListener.ofDto(
                getImportBatchSize(),
                this::saveBatch,
                this::validateImportedEntity,
                (e, rowIndex) -> log.warn("导入第{}行异常: {}", rowIndex, e.getMessage())
        );
    }

    /**
     * 批量保存实体
     * 子类可以重写此方法实现批量保存优化
     *
     * @param entities 实体列表
     */
    protected void saveBatch(List<ENTITY> entities) {
        entities.forEach(this::save);
    }

    /**
     * 校验导入的实体
     * 子类可以重写此方法实现自定义校验逻辑
     *
     * @param entity   实体
     * @param rowIndex 行号
     * @param errors   错误列表
     * @return 是否通过校验
     */
    protected boolean validateImportedEntity(ENTITY entity, int rowIndex, List<String> errors) {
        return true;
    }

    /**
     * 获取导入批次大小
     * 子类可以重写此方法自定义批次大小
     *
     * @return 批次大小
     */
    protected int getImportBatchSize() {
        return 200;
    }


}
