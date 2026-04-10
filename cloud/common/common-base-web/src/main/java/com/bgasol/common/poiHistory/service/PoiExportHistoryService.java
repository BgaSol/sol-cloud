package com.bgasol.common.poiHistory.service;

import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.poiHistory.mapper.PoiExportHistoryMapper;
import com.bgasol.model.system.department.api.DepartmentApi;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.poiHistory.dto.PoiExportHistoryPageDto;
import com.bgasol.model.system.poiHistory.entity.PoiExportHistoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoiExportHistoryService extends BaseService<PoiExportHistoryEntity, PoiExportHistoryPageDto> {

    private final PoiExportHistoryMapper poiExportHistoryMapper;
    private final DepartmentApi departmentApi;

    @Override
    public MyBaseMapper<PoiExportHistoryEntity> commonBaseMapper() {
        return poiExportHistoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void findOtherTable(List<PoiExportHistoryEntity> list) {
        Set<String> departmentIds = list.stream()
                .map(PoiExportHistoryEntity::getDepartmentId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());

        if (ObjectUtils.isNotEmpty(departmentIds)) {
            List<DepartmentEntity> departmentEntityList = departmentApi.findByIds(departmentIds, true).getData();
            if (ObjectUtils.isNotEmpty(departmentEntityList)) {
                Map<String, DepartmentEntity> departmentEntityMap = departmentEntityList.stream()
                        .collect(Collectors.toMap(DepartmentEntity::getId, Function.identity()));

                list.forEach(entity -> {
                    if (ObjectUtils.isNotEmpty(entity.getDepartmentId())) {
                        entity.setDepartment(departmentEntityMap.get(entity.getDepartmentId()));
                    }
                });
            }
        }

        super.findOtherTable(list);
    }

    public void markFailed(String recordId, String errorMessage) {
        PoiExportHistoryEntity poiExportHistoryEntity = PoiExportHistoryEntity.builder()
                .id(recordId)
                .status(3)
                .errorMessage(errorMessage)
                .build();
        poiExportHistoryMapper.updateById(poiExportHistoryEntity);
    }

    public void markSuccess(String recordId, String fileId) {
        PoiExportHistoryEntity poiExportHistoryEntity = PoiExportHistoryEntity.builder()
                .id(recordId)
                .status(2)
                .fileId(fileId)
                .build();
        poiExportHistoryMapper.updateById(poiExportHistoryEntity);
    }
}
