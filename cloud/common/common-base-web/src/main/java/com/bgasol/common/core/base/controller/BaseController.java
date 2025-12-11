package com.bgasol.common.core.base.controller;

import com.bgasol.common.core.base.capability.PoiCapability;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.ImportResult;
import com.bgasol.common.core.base.vo.PageVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Validated
public abstract class BaseController<
        ENTITY extends BaseEntity,
        PAGE_DTO extends BasePageDto<ENTITY>,
        CREATE_DTO extends BaseCreateDto<ENTITY>,
        UPDATE_DTO extends BaseUpdateDto<ENTITY>
        > {
    abstract public BaseService<ENTITY, PAGE_DTO> commonBaseService();


    /**
     * 获取poi能力 如果需要，就实现这个方法去做poi操作
     */
    public PoiCapability getPoiCapability() {
        return null;
    }

    public BaseVo<PageVo<ENTITY>> findByPage(@Valid PAGE_DTO pageDto) {
        PageVo<ENTITY> byPage = commonBaseService().findByPage(pageDto);
        return BaseVo.success(byPage);
    }

    public BaseVo<Void> insert(@Valid CREATE_DTO createDto) {
        ENTITY entity = createDto.toEntity();
        commonBaseService().insert(entity);
        return BaseVo.success(null, "保存成功");
    }

    public BaseVo<Void> apply(@Valid UPDATE_DTO updateDto) {
        ENTITY entity = updateDto.toEntity();
        commonBaseService().apply(entity);
        return BaseVo.success(null, "更新成功");
    }

    @Deprecated
    public BaseVo<ENTITY> save(@Valid CREATE_DTO createDto) {
        ENTITY entity = createDto.toEntity();
        return BaseVo.success(commonBaseService().save(entity), "保存成功");
    }

    @Deprecated
    public BaseVo<ENTITY> update(@Valid UPDATE_DTO updateDto) {
        ENTITY entity = updateDto.toEntity();
        return BaseVo.success(commonBaseService().update(entity), "更新成功");
    }


    public BaseVo<Integer[]> delete(@Valid @NotBlank String ids) {
        String[] idsArr = ids.split(",");
        Integer[] delete = commonBaseService().delete(idsArr);
        return BaseVo.success(delete, "删除成功");
    }

    public BaseVo<ENTITY> findById(@Valid @NotBlank String id) {
        ENTITY entity = commonBaseService().findById(id);
        return BaseVo.success(entity);
    }

    public BaseVo<List<ENTITY>> findByIds(@Valid @NotBlank String ids) {
        String[] idsArr = ids.split(",");
        return BaseVo.success(commonBaseService().findByIds(idsArr));
    }

    public BaseVo<List<ENTITY>> findAll() {
        List<ENTITY> all = commonBaseService().findAll();
        return BaseVo.success(all);
    }

    /**
     * 获取ENTITY实体类的Class对象
     */
    @SuppressWarnings("unchecked")
    public Class<ENTITY> commonBaseEntityClass() {
        return (Class<ENTITY>) ResolvableType.forClass(getClass()).as(BaseController.class).getGeneric(0).resolve();
    }

    /**
     * 获取CreateDto实体类的Class对象
     */
    @SuppressWarnings("unchecked")
    public Class<CREATE_DTO> commonCreateDtoClass() {
        return (Class<CREATE_DTO>) ResolvableType.forClass(getClass()).as(BaseController.class).getGeneric(2).resolve();
    }


    /**
     * 下载导入模板
     *
     * @return Excel模板文件响应
     */
    public ResponseEntity<InputStreamResource> downloadImportTemplate() {
        PoiCapability poiCapability = getPoiCapability();
        if (ObjectUtils.isEmpty(poiCapability)) {
            throw new BaseException("该接口未实现poi能力");
        }
        byte[] templateBytes = poiCapability.generateImportTemplateBytes(commonCreateDtoClass());
        String fileName = poiCapability.getImportTemplateFileName(commonBaseEntityClass()) + "_导入模板.xlsx";

        // 构建响应
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(templateBytes.length))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(
                        fileName,
                        StandardCharsets.UTF_8
                ))
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(new ByteArrayInputStream(templateBytes)));
    }

    public BaseVo<ImportResult> importFromExcel(MultipartFile file) {
        PoiCapability poiCapability = getPoiCapability();
        if (ObjectUtils.isEmpty(poiCapability)) {
            throw new BaseException("该接口未实现poi能力");
        }
        ImportResult importResult = poiCapability.importFromExcel(file, commonCreateDtoClass(), commonBaseService(), commonBaseService().importValidator());
        return BaseVo.success(importResult, importResult.getErrorRows() > 0 ? "导入失败，有" + importResult.getErrorRows() + "行数据导入失败" : "导入成功");
    }
}
