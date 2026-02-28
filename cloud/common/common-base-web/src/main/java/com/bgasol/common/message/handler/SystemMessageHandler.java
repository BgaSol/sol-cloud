package com.bgasol.common.message.handler;

import com.bgasol.common.message.entity.MessageEnvelopeEntity;
import org.springframework.stereotype.Component;

@Component
public class SystemMessageHandler implements MessageHandler {

    public String TYPE = "SystemMessageHandler";

    @Override
    public void handle(MessageEnvelopeEntity<?> messageEnvelopeEntity) {
        // todo 系统消息处理函数
    }

    @Override
    public boolean support(String type) {
        return TYPE.equals(type);
    }
}
