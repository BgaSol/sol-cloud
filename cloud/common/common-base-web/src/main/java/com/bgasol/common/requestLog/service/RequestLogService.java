package com.bgasol.common.requestLog.service;

import com.bgasol.common.core.base.service.BaseTreeService;
import com.bgasol.common.requestLog.mapper.RequestLogMapper;
import com.bgasol.model.system.requestLog.dto.RequestLogPageDto;
import com.bgasol.model.system.requestLog.entity.RequestLogEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestLogService extends BaseTreeService<RequestLogEntity, RequestLogPageDto> {
    private final RequestLogMapper requestLogMapper;

    @Override
    public RequestLogMapper commonBaseMapper() {
        return requestLogMapper;
    }
}
