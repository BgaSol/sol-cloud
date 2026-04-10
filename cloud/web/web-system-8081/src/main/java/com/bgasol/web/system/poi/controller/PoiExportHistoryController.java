package com.bgasol.web.system.poi.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.common.poiHistory.service.PoiExportHistoryService;
import com.bgasol.model.system.poiHistory.dto.PoiExportHistoryPageDto;
import com.bgasol.model.system.poiHistory.entity.PoiExportHistoryEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Override
    public PoiExportHistoryService commonBaseService() {
        return poiExportHistoryService;
    }

    @Override
    @PostMapping("/page/{otherData}")
    @Operation(summary = "分页查询POI导出记录", operationId = "findByPagePoiExportHistoryController")
    @SaCheckPermission(value = "PoiExportHistoryController:findByPage")
    public BaseVo<PageVo<PoiExportHistoryEntity>> findByPage(@RequestBody PoiExportHistoryPageDto pageDto, @PathVariable Boolean otherData) {
        return super.findByPage(pageDto, otherData);
    }
}
