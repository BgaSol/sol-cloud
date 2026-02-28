package com.bgasol.common.message.dto;

import com.bgasol.common.message.dto.MessageBody;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "系统消息")
public class SystemMessageBody implements MessageBody {
    private String message;
}
