package com.bgasol.common.message.mapper;

import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.common.message.entity.MessageEnvelopeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageEnvelopeMapper extends MyBaseMapper<MessageEnvelopeEntity<?>> {
}
