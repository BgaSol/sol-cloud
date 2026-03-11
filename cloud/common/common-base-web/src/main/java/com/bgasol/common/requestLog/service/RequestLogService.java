package com.bgasol.common.requestLog.service;

import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.service.BaseTreeService;
import com.bgasol.model.system.requestLog.entity.RequestLogEntity;
import com.bgasol.common.requestLog.mapper.RequestLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestLogService extends BaseTreeService<RequestLogEntity, BasePageDto<RequestLogEntity>> {
    private final RequestLogMapper requestLogMapper;

    @Override
    public RequestLogMapper commonBaseMapper() {
        return requestLogMapper;
    }
}
