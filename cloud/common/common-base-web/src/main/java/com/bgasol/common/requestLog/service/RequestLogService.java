package com.bgasol.common.requestLog.service;

import com.bgasol.common.core.base.service.BaseTreeService;
import com.bgasol.common.requestLog.mapper.RequestLogMapper;
import com.bgasol.model.system.requestLog.dto.RequestLogPageDto;
import com.bgasol.model.system.requestLog.entity.RequestLogEntity;
import com.bgasol.model.system.user.api.UserApi;
import com.bgasol.model.system.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestLogService extends BaseTreeService<RequestLogEntity, RequestLogPageDto> {
    private final RequestLogMapper requestLogMapper;
    private final UserApi userApi;

    @Override
    public RequestLogMapper commonBaseMapper() {
        return requestLogMapper;
    }

    @Override
    public void findOtherTable(List<RequestLogEntity> list) {
        Set<String> userIds = list.stream()
                .map(RequestLogEntity::getUserId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());

        if (ObjectUtils.isNotEmpty(userIds)) {
            Map<String, UserEntity> userEntityMap = userApi.findByIds(String.join(",", userIds)).getData().stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

            list.forEach(requestLogEntity -> {
                if (ObjectUtils.isNotEmpty(requestLogEntity.getUserId())) {
                    UserEntity userEntity = userEntityMap.get(requestLogEntity.getUserId());
                    requestLogEntity.setUser(userEntity);
                }
            });
        }
    }
}
