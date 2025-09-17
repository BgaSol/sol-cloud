package com.bgasol.common.core.base.controller;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.service.BasePoiService;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.ImportResult;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public abstract class BasePoiController<
        ENTITY extends BaseEntity,
        PAGE_DTO extends BasePageDto<ENTITY>,
        CREATE_DTO extends BaseCreateDto<ENTITY>,
        UPDATE_DTO extends BaseUpdateDto<ENTITY>
        > extends BaseController<ENTITY, PAGE_DTO, CREATE_DTO, UPDATE_DTO> {
    abstract public BasePoiService<ENTITY, PAGE_DTO, CREATE_DTO, UPDATE_DTO> commonBaseService();

    /**
     * 下载导入模板
     *
     * @return Excel模板文件响应
     */
    public ResponseEntity<InputStreamResource> downloadImportTemplate() {
        BasePoiService<ENTITY, PAGE_DTO, CREATE_DTO, UPDATE_DTO> service = commonBaseService();
        
        // 生成模板数据
        byte[] templateBytes = service.generateImportTemplateBytes();
        String fileName = service.getImportTemplateFileName();
        
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

    public BaseVo<ImportResult> importFromExcel(MultipartFile file)  {
        ImportResult importResult = commonBaseService().importFromExcel(file);
        return BaseVo.success(importResult, importResult.getErrorRows()>0?"导入失败，有"+ importResult.getErrorRows() +"行数据导入失败":"导入成功");
    }

}
