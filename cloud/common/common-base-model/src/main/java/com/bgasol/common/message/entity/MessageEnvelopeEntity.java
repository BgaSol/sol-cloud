package com.bgasol.common.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.common.message.dto.MessageBody;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@TableName("system_t_message_envelope")
@Schema(description = "消息实体类")
@Entity
public class MessageEnvelopeEntity<T extends MessageBody> extends BaseEntity {
    @TableField("business_type")
    @Schema(description = "业务类型: 站内信，小程序通知，邮件")
    private String businessType;

    @TableField("message_recipient_type")
    @Schema(description = "接收者类型")
    private MessageRecipientTypeEnum messageRecipientTypeEnum;

    @TableField("recipient_id")
    @Schema(description = "接收者id")
    private String recipientId;

    @TableField("title")
    @Schema(description = "消息标题")
    private String title;

    @TableField("content")
    @Schema(description = "消息内容")
    private String content;

    @TableField("handler")
    @Schema(description = "处理器: 邮件处理器，小程序通知处理器")
    private String handler;

    @TableField("metadata")
    @Schema(description = "元数据")
    private String metadata;

    @TableField("status")
    @Schema(description = "消息状态")
    private MessageEnvelopeStatusEnum status;

    @TableField(exist = false)
    @Transient
    @Schema(description = "消息体")
    @JsonIgnore
    private T body;
}