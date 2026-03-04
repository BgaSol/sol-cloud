package com.bgasol.model.system.message.dto;

import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.message.dto.MessageBody;
import com.bgasol.common.message.entity.MessageEnvelopeEntity;
import com.bgasol.common.message.entity.MessageEnvelopeStatusEnum;
import com.bgasol.common.message.entity.MessageRecipientTypeEnum;
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
public class MessageEnvelopeUpdateDto extends BaseUpdateDto<MessageEnvelopeEntity<?>> {
    @Schema(description = "业务类型: 站内信，小程序通知，邮件")
    private String businessType;

    @Schema(description = "接收者类型")
    private MessageRecipientTypeEnum messageRecipientTypeEnum;

    @Schema(description = "接收者id")
    private String recipientId;

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
    public MessageEnvelopeEntity<MessageBody> toEntity() {
        MessageEnvelopeEntity<MessageBody> entity = new MessageEnvelopeEntity<>();
        entity.setBusinessType(this.businessType);
        entity.setMessageRecipientTypeEnum(this.messageRecipientTypeEnum);
        entity.setRecipientId(this.recipientId);
        entity.setTitle(this.title);
        entity.setContent(this.content);
        entity.setHandler(this.handler);
        entity.setMetadata(this.metadata);
        entity.setStatus(this.status);

        // 手动调用, 放弃调用super
        entity.setSort(this.getSort());
        entity.setDescription(this.getDescription());
        entity.setId(this.getId());
        return entity;
    }
}
