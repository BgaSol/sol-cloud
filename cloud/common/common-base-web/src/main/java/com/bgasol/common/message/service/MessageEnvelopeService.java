package com.bgasol.common.message.service;

import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.message.entity.MessageEnvelopeEntity;
import com.bgasol.common.message.handler.MessageHandler;
import com.bgasol.common.message.mapper.MessageEnvelopeMapper;
import com.bgasol.model.system.message.dto.MessageEnvelopePageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
