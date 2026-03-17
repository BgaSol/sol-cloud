package com.bgasol.common.message.handler;

import com.bgasol.model.system.message.entity.MessageEnvelopeEntity;

public interface MessageHandler {

    void handle(MessageEnvelopeEntity<?> messageEnvelopeEntity);

    boolean support(String handlerType);

}
