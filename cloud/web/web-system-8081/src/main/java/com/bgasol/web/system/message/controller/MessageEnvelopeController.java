package com.bgasol.web.system.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.common.message.dto.MessageEnvelopePageDto;
import com.bgasol.common.message.entity.MessageEnvelopeEntity;
import com.bgasol.common.message.service.MessageEnvelopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "消息管理")
@RequestMapping("/message-envelope")
public class MessageEnvelopeController extends BaseController<
        MessageEnvelopeEntity<?>,
        MessageEnvelopePageDto,
        BaseCreateDto<MessageEnvelopeEntity<?>>,
        BaseUpdateDto<MessageEnvelopeEntity<?>>> {

    private final MessageEnvelopeService messageEnvelopeService;

    @Override
    public BaseService<MessageEnvelopeEntity<?>, MessageEnvelopePageDto> commonBaseService() {
        return messageEnvelopeService;
    }

    @Override
    @PostMapping("/page")
    @Operation(summary = "分页查询消息", operationId = "findPageMessageEnvelope")
    @SaCheckPermission(value = "messageEnvelope:findByPage", orRole = "admin")
    public BaseVo<PageVo<MessageEnvelopeEntity<?>>> findByPage(@RequestBody @Valid MessageEnvelopePageDto pageDto) {
        return super.findByPage(pageDto);
    }

}