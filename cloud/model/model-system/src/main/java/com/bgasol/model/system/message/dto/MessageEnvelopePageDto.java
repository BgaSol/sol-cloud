package com.bgasol.model.system.message.dto;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.model.system.message.entity.MessageEnvelopeEntity;
import com.bgasol.model.system.message.entity.MessageEnvelopeStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "消息分页DTO")
public class MessageEnvelopePageDto extends BasePageDto<MessageEnvelopeEntity<?>> {
    @Schema(description = "业务类型: 站内信，小程序通知，邮件")
    private String businessType;

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
    public Wrapper<MessageEnvelopeEntity<?>> getQueryWrapper() {
        LambdaQueryWrapper<MessageEnvelopeEntity<?>> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(businessType), MessageEnvelopeEntity::getBusinessType, businessType);
        queryWrapper.like(ObjectUtils.isNotEmpty(title), MessageEnvelopeEntity::getTitle, title);
        queryWrapper.like(ObjectUtils.isNotEmpty(content), MessageEnvelopeEntity::getContent, content);

        queryWrapper.like(ObjectUtils.isNotEmpty(metadata), MessageEnvelopeEntity::getMetadata, metadata);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), MessageEnvelopeEntity::getStatus, status);
        queryWrapper.like(ObjectUtils.isNotEmpty(description), MessageEnvelopeEntity::getDescription, description);
        return queryWrapper;
    }
}
