package com.bgasol.common.core.base.service;

import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.core.base.mapper.PoiExportHistoryMapper;
import com.bgasol.common.poiHistory.dto.PoiExportHistoryPageDto;
import com.bgasol.common.poiHistory.entity.PoiExportHistoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoiExportHistoryService extends BaseService<PoiExportHistoryEntity, PoiExportHistoryPageDto>{

    private final PoiExportHistoryMapper poiExportHistoryMapper;

    @Override
    public MyBaseMapper<PoiExportHistoryEntity> commonBaseMapper() {
        return poiExportHistoryMapper;
    }
}
