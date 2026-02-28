package com.bgasol.common.message.service;

import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.message.dto.MessageEnvelopePageDto;
import com.bgasol.common.message.entity.MessageEnvelopeEntity;
import com.bgasol.common.message.handler.MessageHandler;
import com.bgasol.common.message.mapper.MessageEnvelopeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageEnvelopeService extends BaseService<MessageEnvelopeEntity<?>, MessageEnvelopePageDto> {

    private final MessageEnvelopeMapper messageEnvelopeMapper;
    private final List<MessageHandler> messageHandlers;

    @Override
    public MyBaseMapper<MessageEnvelopeEntity<?>> commonBaseMapper() {
        return messageEnvelopeMapper;
    }

    @Override
    public void insert(MessageEnvelopeEntity<?> entity) {
        for (MessageHandler messageHandler : messageHandlers) {
            if (messageHandler.support(entity.getHandler())) {
                messageHandler.handle(entity);
            }
        }
        super.insert(entity);
    }
}
