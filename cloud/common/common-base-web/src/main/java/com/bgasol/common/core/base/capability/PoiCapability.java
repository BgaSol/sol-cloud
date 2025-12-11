package com.bgasol.common.core.base.capability;

import cn.idev.excel.FastExcel;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.listener.BaseExcelImportListener;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.core.base.vo.ImportResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

@Slf4j
@Service
public class PoiCapability {

    /**
     * 生成Excel导入模板数据
     *
     * @return Excel模板的字节数组
     */
    public <ENTITY extends BaseEntity, CREATE_DTO extends BaseCreateDto<ENTITY>> byte[] generateImportTemplateBytes(Class<CREATE_DTO> dtoClass) {
        try {
            List<List<String>> headers = buildExcelHeaders(dtoClass);
            return generateExcelBytes(headers);
        } catch (Exception e) {
            log.error("生成导入模板失败", e);
            throw new BaseException("生成导入模板失败: " + e.getMessage());
        }
    }

    /**
     * 构建Excel表头
     * 基于CreateDTO类的@Schema(description)注解生成表头
     *
     * @return Excel表头列表
     */
    private <ENTITY extends BaseEntity, CREATE_DTO extends BaseCreateDto<ENTITY>> List<List<String>> buildExcelHeaders(Class<CREATE_DTO> createDtoClass) {
        List<Field> fields = FieldUtils.getAllFieldsList(createDtoClass);

        List<String> columnTitles = fields.stream()
                .map(field -> field.getAnnotation(Schema.class))
                .filter(Objects::nonNull).map(Schema::description)
                .filter(StringUtils::isNotBlank).toList();

        if (columnTitles.isEmpty()) {
            throw new BaseException("未能从DTO类生成任何列，请检查DTO字段的@Schema(description)注解配置");
        }

        return columnTitles.stream().map(Collections::singletonList).toList();

    }


    /**
     * 生成Excel字节数组
     *
     * @param headers Excel表头
     * @return Excel文件字节数组
     */
    private byte[] generateExcelBytes(List<List<String>> headers) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FastExcel.write(outputStream).head(headers).sheet("导入模板").doWrite(new ArrayList<>());
        return outputStream.toByteArray();
    }

    /**
     * 获取导入模板文件名
     *
     * @return 模板文件名
     */
    public <ENTITY extends BaseEntity> String getImportTemplateFileName(Class<ENTITY> baseEntityClass) {
        Schema classSchema = baseEntityClass.getAnnotation(Schema.class);
        if (classSchema != null && StringUtils.isNotBlank(classSchema.description())) {
            String description = classSchema.description();
            return description.replaceAll("(实体类|实体)$", "");
        }
        return baseEntityClass.getSimpleName().replaceAll("(Entity|实体类|实体)$", "");
    }

    /**
     * 从Excel文件导入数据
     *
     * @param file Excel文件
     * @return 导入结果
     */
    public <ENTITY extends BaseEntity, PAGE_DTO extends BasePageDto<ENTITY>, CREATE_DTO extends BaseCreateDto<ENTITY>> ImportResult importFromExcel(
            MultipartFile file, Class<CREATE_DTO> dtoClass,
            BaseService<ENTITY, PAGE_DTO> baseService,
            BiPredicate<ENTITY, List<String>> validator
    ) {
        if (file == null || file.isEmpty()) {
            throw new BaseException("导入文件不能为空");
        }
        BaseExcelImportListener<CREATE_DTO, ENTITY> listener =
                BaseExcelImportListener.ofDto(
                        200,
                        entities -> saveBatch(entities, baseService),
                        (entity, rowIndex, errors) -> validator == null || validator.test(entity, errors),
                        (e, rowIndex) -> log.warn("导入第{}行异常: {}", rowIndex, e.getMessage())
                );
        try {
            FastExcel.read(file.getInputStream(), dtoClass, listener).headRowNumber(1).sheet().doRead();

            return ImportResult.builder().totalRows(listener.getCurrentRowIndex()).successRows(listener.getSuccessRows())
                    .errorRows(listener.getErrors().size()).errors(listener.getErrors()).build();
        } catch (Exception e) {
            log.error("Excel导入失败", e);
            throw new BaseException("Excel导入失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存实体
     * 子类可以重写此方法实现批量保存优化
     *
     * @param entities 实体列表
     */
    protected <ENTITY extends BaseEntity, PAGE_DTO extends BasePageDto<ENTITY>> void saveBatch(List<ENTITY> entities, BaseService<ENTITY, PAGE_DTO> baseService) {
        entities.forEach(baseService::insert);
    }

//    // 导出
//    public <EXPORT_DTO> WriteSheet getExcelWriter(Class<EXPORT_DTO> exportDtoClass, String sheetName) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ExcelWriter excelWriter = FastExcel.write(outputStream, exportDtoClass).build();
//    }
}
