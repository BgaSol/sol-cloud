package com.bgasol.plugin.websocket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "WebSocket发送消息分片DTO")
public class SendMessageChunkDto {
    private String uuid;
    private Integer size;
    private Integer index;
    private String data;
}
