package com.bgasol.common.message.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.message.handler.MessageHandler;
import com.bgasol.common.message.mapper.MessageEnvelopeMapper;
import com.bgasol.model.system.message.dto.MessageEnvelopePageDto;
import com.bgasol.model.system.message.entity.MessageEnvelopeEntity;
import com.bgasol.model.system.message.entity.MessageEnvelopeStatusEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageEnvelopeService extends BaseService<MessageEnvelopeEntity<?>, MessageEnvelopePageDto> {

    private final MessageEnvelopeMapper messageEnvelopeMapper;
    private final List<MessageHandler> messageHandlers;
    private final ThreadPoolTaskExecutor ioThreadPool;

    @Override
    public MyBaseMapper<MessageEnvelopeEntity<?>> commonBaseMapper() {
        return messageEnvelopeMapper;
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
