package com.bgasol.web.file.poi.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.service.PoiExportHistoryService;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.common.core.base.dto.PoiExportHistoryPageDto;
import com.bgasol.common.core.base.entity.PoiExportHistoryEntity;
import com.bgasol.web.file.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@Tag(name = "POI导出记录管理")
@RequestMapping("/poi-export-history")
@Slf4j
public class PoiExportHistoryController extends BaseController<
        PoiExportHistoryEntity,
        PoiExportHistoryPageDto,
        BaseCreateDto<PoiExportHistoryEntity>,
        BaseUpdateDto<PoiExportHistoryEntity>> {
    private final PoiExportHistoryService poiExportHistoryService;
    private final FileService fileService;

    @Override
    public PoiExportHistoryService commonBaseService() {
        return poiExportHistoryService;
    }

    @Override
    @PostMapping("/page")
    @Operation(summary = "分页查询POI导出记录", operationId = "findPagePoiExportHistory")
    @SaCheckPermission(value = "poiExportHistory:findByPage", orRole = "admin")
    public BaseVo<PageVo<PoiExportHistoryEntity>> findByPage(@RequestBody @Valid PoiExportHistoryPageDto pageDto) {
        return super.findByPage(pageDto);
    }

//    @Override
//    @DeleteMapping("/{ids}")
//    @SaCheckPermission(value = "poiExportHistory:delete", orRole = "admin")
//    @Operation(summary = "删除POI导出记录", operationId = "deletePoiExportHistory")
//    public BaseVo<Integer[]> delete(@PathVariable("ids") String ids) {
//        List<PoiExportHistoryEntity> poiExportHistoryEntityList = poiExportHistoryService.findDirectByIds(ids.split(","));
//        poiExportHistoryEntityList.stream()
//                .filter(e -> ObjectUtils.isNotEmpty(e.getFileId()))
//                .forEach(e -> fileService.delete(e.getFileId()));
//        return super.delete(ids);
//    }
}
