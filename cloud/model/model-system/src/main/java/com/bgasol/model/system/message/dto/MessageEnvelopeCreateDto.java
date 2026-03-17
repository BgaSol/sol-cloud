package com.bgasol.model.system.message.dto;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.system.message.entity.MessageEnvelopeEntity;
import com.bgasol.model.system.message.entity.MessageEnvelopeStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "创建消息")
public class MessageEnvelopeCreateDto extends BaseCreateDto<MessageEnvelopeEntity<?>> {
    @Schema(description = "业务类型: 站内信，小程序通知，邮件")
    private String businessType;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "消息标题")
    private String title;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "处理器: 邮件处理器，小程序通知处理器")
    private String handler;

    @Schema(description = "元数据")
    private String metadata;

    @Schema(description = "消息状态")
    private MessageEnvelopeStatusEnum status;

    @Schema(description = "描述")
    private String description;

    @Override
    public MessageEnvelopeEntity<?> toEntity() {
        MessageEnvelopeEntity<?> entity = new MessageEnvelopeEntity<>();
        entity.setBusinessType(this.businessType);
        entity.setUserId(this.userId);
        entity.setTitle(this.title);
        entity.setContent(this.content);
        entity.setHandler(this.handler);
        entity.setMetadata(this.metadata);
        entity.setStatus(this.status);
        return entity;
    }
}
