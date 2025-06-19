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
@Schema(description = "WebSocket发送消息DTO")
public class WsSendMessageDto {
    @Schema(description = "WebSocket消息内容")
    private String json;

    @Schema(description = "WebSocket消息类型")
    private String type;

    @Schema(description = "userId")
    private String userId;

    @Schema(description = "sessionId")
    private String sessionId;
}
