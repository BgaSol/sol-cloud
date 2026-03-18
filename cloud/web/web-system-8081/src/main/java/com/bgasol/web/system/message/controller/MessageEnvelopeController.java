package com.bgasol.web.system.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.service.BaseService;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.common.message.service.MessageEnvelopeService;
import com.bgasol.model.system.message.dto.MessageEnvelopeCreateDto;
import com.bgasol.model.system.message.dto.MessageEnvelopePageDto;
import com.bgasol.model.system.message.dto.MessageEnvelopeUpdateDto;
import com.bgasol.model.system.message.entity.MessageEnvelopeEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "消息管理")
@RequestMapping("/message-envelope")
public class MessageEnvelopeController extends BaseController<
        MessageEnvelopeEntity<?>,
        MessageEnvelopePageDto,
        MessageEnvelopeCreateDto,
        MessageEnvelopeUpdateDto> {

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

    @Override
    @PostMapping
    @Operation(summary = "保存消息", operationId = "saveMessageEnvelope")
    @SaCheckPermission(value = "messageEnvelope:save", orRole = "admin")
    public BaseVo<MessageEnvelopeEntity<?>> save(@RequestBody @Valid MessageEnvelopeCreateDto entity) {
        return super.save(entity);
    }

    @Override
    @PutMapping
    @Operation(summary = "更新消息", operationId = "updateMessageEnvelope")
    @SaCheckPermission(value = "messageEnvelope:update", orRole = "admin")
    public BaseVo<MessageEnvelopeEntity<?>> update(@RequestBody @Valid MessageEnvelopeUpdateDto entity) {
        return super.update(entity);
    }

    @PostMapping("/read")
    @Operation(summary = "批量已读消息", operationId = "readMessageEnvelope")
    @SaCheckPermission(value = "messageEnvelope:read", orRole = "admin")
    public BaseVo<Void> read(@RequestBody @Valid @NotEmpty(message = "ids列表不能为空") List<String> ids) {
        messageEnvelopeService.toReadById(ids);
        return BaseVo.success();
    }

}
