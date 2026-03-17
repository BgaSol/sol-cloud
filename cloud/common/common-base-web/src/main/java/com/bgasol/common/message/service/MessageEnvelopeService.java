package com.bgasol.common.message.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.message.handler.MessageHandler;
import com.bgasol.common.message.mapper.MessageEnvelopeMapper;
import com.bgasol.model.system.message.dto.MessageEnvelopePageDto;
import com.bgasol.model.system.message.entity.MessageEnvelopeEntity;
import com.bgasol.model.system.message.entity.MessageEnvelopeStatusEnum;
import com.bgasol.model.system.user.api.UserApi;
import com.bgasol.model.system.user.entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageEnvelopeService extends BaseService<MessageEnvelopeEntity<?>, MessageEnvelopePageDto> {

    private final MessageEnvelopeMapper messageEnvelopeMapper;
    private final List<MessageHandler> messageHandlers;
    private final ThreadPoolTaskExecutor ioThreadPool;
    private final UserApi userApi;

    @Override
    public MyBaseMapper<MessageEnvelopeEntity<?>> commonBaseMapper() {
        return messageEnvelopeMapper;
    }

    @Override
    public void findOtherTable(List<MessageEnvelopeEntity<?>> list) {
        Set<String> userIds = list.stream()
                .map(MessageEnvelopeEntity::getUserId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());
        Map<String, UserEntity> userEntityMap = userApi.findByIds(String.join(",", userIds)).getData().stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));
        list.forEach(e -> {
            if (ObjectUtils.isNotEmpty(e.getUserId())) {
                e.setUser(userEntityMap.get(e.getUserId()));
            }
        });
        super.findOtherTable(list);
    }

    @Override
    @Transactional
    public void insert(MessageEnvelopeEntity<?> entity) {
        super.insert(entity);
        ioThreadPool.submit(() -> {
            try {
                this.processMessages(entity);
            } catch (Exception e) {
                log.error("消息处理失败", e);
            }
        });
    }

    private void processMessages(MessageEnvelopeEntity<?> entity) {
        try {
            for (MessageHandler messageHandler : messageHandlers) {
                if (messageHandler.support(entity.getHandler())) {
                    messageHandler.handle(entity);
                }
            }
        } catch (RuntimeException e) {
            log.error("消息处理异常", e);
        }
    }

    public void read(@Valid List<String> ids) {
        Set<String> idSet = new HashSet<>(ids);
        MessageEnvelopeEntity<?> messageEnvelopeEntity = new MessageEnvelopeEntity<>();
        messageEnvelopeEntity.setStatus(MessageEnvelopeStatusEnum.READ);

        LambdaUpdateWrapper<MessageEnvelopeEntity<?>> luw = new LambdaUpdateWrapper<>();
        luw.in(MessageEnvelopeEntity::getId, idSet);
        messageEnvelopeMapper.update(messageEnvelopeEntity, luw);
    }
}
