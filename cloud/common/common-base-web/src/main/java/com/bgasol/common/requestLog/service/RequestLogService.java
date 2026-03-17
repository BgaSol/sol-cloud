package com.bgasol.common.requestLog.service;

import com.bgasol.common.core.base.service.BaseTreeService;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.common.message.service.MessageEnvelopeService;
import com.bgasol.common.requestLog.handler.RequestLogTableNameHandler;
import com.bgasol.common.requestLog.mapper.RequestLogMapper;
import com.bgasol.model.system.message.entity.MessageEnvelopeEntity;
import com.bgasol.model.system.message.entity.MessageEnvelopeStatusEnum;
import com.bgasol.model.system.requestLog.dto.RequestLogPageDto;
import com.bgasol.model.system.requestLog.entity.RequestLogEntity;
import com.bgasol.model.system.user.api.UserApi;
import com.bgasol.model.system.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bgasol.common.constant.value.SystemConfigValues.ADMIN_USER_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestLogService extends BaseTreeService<RequestLogEntity, RequestLogPageDto> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd");
    private final RequestLogMapper requestLogMapper;
    private final UserApi userApi;
    private final MessageEnvelopeService messageEnvelopeService;
    public final static String BUSINESS_TYPE = "REQUEST_LOG";

    @Override
    public RequestLogMapper commonBaseMapper() {
        return requestLogMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void findOtherTable(List<RequestLogEntity> list) {
        Set<String> userIds = list.stream()
                .map(RequestLogEntity::getUserId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());

        if (ObjectUtils.isNotEmpty(userIds)) {
            Map<String, UserEntity> userEntityMap = userApi.findByIds(String.join(",", userIds)).getData().stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

            list.forEach(requestLogEntity -> {
                if (ObjectUtils.isEmpty(requestLogEntity.getUserId())) {
                    return;
                }
                UserEntity userEntity = userEntityMap.get(requestLogEntity.getUserId());
                requestLogEntity.setUser(userEntity);
            });
        }
    }

    @Override
    public PageVo<RequestLogEntity> findByPage(RequestLogPageDto pageDto) {
        String dateStr = pageDto.getCreateTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DATE_FORMAT);
        RequestLogTableNameHandler.TABLE_SUFFIX.set(dateStr);
        return super.findByPage(pageDto);
    }

    @Override
    @Transactional
    public void insert(RequestLogEntity entity) {
        String dateStr = entity.getId().substring(0, "yyyy_MM_dd".length());
        RequestLogTableNameHandler.TABLE_SUFFIX.set(dateStr);
        super.insert(entity);
        if (!entity.getIsPrimaryErr()) {
            return;
        }
        MessageEnvelopeEntity<RequestLogEntity> messageEnvelopeEntity = new MessageEnvelopeEntity<>();
        messageEnvelopeEntity.setBusinessType(BUSINESS_TYPE);
        messageEnvelopeEntity.setUserId(ADMIN_USER_ID);
        messageEnvelopeEntity.setTitle(entity.getBusinessController() + entity.getBusinessMethod());
        messageEnvelopeEntity.setDescription(entity.getErrorLog());
        messageEnvelopeEntity.setMetadata(entity.getId());
        messageEnvelopeEntity.setStatus(MessageEnvelopeStatusEnum.UNREAD);
        messageEnvelopeEntity.setBody(entity);
        messageEnvelopeService.insert(messageEnvelopeEntity);
    }
}
