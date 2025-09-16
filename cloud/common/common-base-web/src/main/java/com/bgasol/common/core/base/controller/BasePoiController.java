package com.bgasol.common.core.base.controller;


import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.core.base.service.BasePoiService;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.ImportResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public abstract class BasePoiController<
        ENTITY extends BaseEntity,
        PAGE_DTO extends BasePageDto<ENTITY>,
        CREATE_DTO extends BaseCreateDto<ENTITY>,
        UPDATE_DTO extends BaseUpdateDto<ENTITY>
        > extends BaseController<ENTITY, PAGE_DTO, CREATE_DTO, UPDATE_DTO> {
    abstract public BasePoiService<ENTITY, PAGE_DTO, CREATE_DTO, UPDATE_DTO> commonBaseService();

    public ResponseEntity<byte[]> downloadImportTemplate() {
        return commonBaseService().generateImportTemplateResponse();
    }

    public BaseVo<ImportResult> importFromExcel(MultipartFile file) throws IOException {
        ImportResult importResult = commonBaseService().importFromExcel(file);
        return BaseVo.success(importResult, "导入成功");
    }

}
