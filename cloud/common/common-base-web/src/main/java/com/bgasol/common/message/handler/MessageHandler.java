package com.bgasol.common.message.handler;

import com.bgasol.common.message.entity.MessageEnvelopeEntity;

public interface MessageHandler {

    void handle(MessageEnvelopeEntity<?> messageEnvelopeEntity);

    boolean support(String type);


}
