package com.bgasol.model.system.message.mapstruct;

import com.bgasol.common.core.base.mapstruct.BaseMapstructConfig;
import com.bgasol.model.system.message.dto.MessageBody;
import com.bgasol.model.system.message.dto.MessageEnvelopeCreateDto;
import com.bgasol.model.system.message.dto.MessageEnvelopeUpdateDto;
import com.bgasol.model.system.message.entity.MessageEnvelopeEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = BaseMapstructConfig.class, builder = @Builder(disableBuilder = true))
public interface MessageEnvelopeMapstruct {

    MessageEnvelopeMapstruct INSTANCE = Mappers.getMapper(MessageEnvelopeMapstruct.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "body", ignore = true)
    MessageEnvelopeEntity<MessageBody> toEntity(MessageEnvelopeCreateDto dto);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "body", ignore = true)
    MessageEnvelopeEntity<MessageBody> toEntity(MessageEnvelopeUpdateDto dto);
}
