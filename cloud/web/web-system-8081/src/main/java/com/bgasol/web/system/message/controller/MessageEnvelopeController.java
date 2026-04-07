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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "消息管理")
@RequestMapping("/message-envelope")
@Validated
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
    @PostMapping("/insert")
    @Operation(summary = "新增消息", operationId = "insertMessageEnvelopeController")
    @SaCheckPermission(value = "MessageEnvelopeController:insert", orRole = "admin")
    public BaseVo<MessageEnvelopeEntity<?>> insert(@RequestBody MessageEnvelopeCreateDto createDto) {
        return super.insert(createDto);
    }

    @Override
    @PostMapping("/apply")
    @Operation(summary = "更新消息", operationId = "applyMessageEnvelopeController")
    @SaCheckPermission(value = "MessageEnvelopeController:apply", orRole = "admin")
    public BaseVo<MessageEnvelopeEntity<?>> apply(@RequestBody MessageEnvelopeUpdateDto updateDto) {
        return super.apply(updateDto);
    }

    @Override
    @PostMapping("/delete")
    @Operation(summary = "删除消息", operationId = "deleteMessageEnvelopeController")
    @SaCheckPermission(value = "MessageEnvelopeController:delete", orRole = "admin")
    public BaseVo<Integer> delete(@RequestBody Set<String> ids) {
        return super.delete(ids);
    }

    @Override
    @GetMapping("/{id}/{otherData}")
    @Operation(summary = "根据ID查询消息", operationId = "findByIdMessageEnvelopeController")
    @SaCheckPermission(value = "MessageEnvelopeController:findById", orRole = "admin")
    public BaseVo<MessageEnvelopeEntity<?>> findById(@PathVariable String id, @PathVariable Boolean otherData) {
        return super.findById(id, otherData);
    }

    @Override
    @PostMapping("/get/{otherData}")
    @Operation(summary = "根据ID批量查询消息", operationId = "findByIdsMessageEnvelopeController")
    @SaCheckPermission(value = "MessageEnvelopeController:findByIds", orRole = "admin")
    public BaseVo<List<MessageEnvelopeEntity<?>>> findByIds(@RequestBody Set<String> ids, @PathVariable Boolean otherData) {
        return super.findByIds(ids, otherData);
    }

    @Override
    @PostMapping("/page/{otherData}")
    @Operation(summary = "分页查询消息", operationId = "findByPageMessageEnvelopeController")
    @SaCheckPermission(value = "MessageEnvelopeController:findByPage", orRole = "admin")
    public BaseVo<PageVo<MessageEnvelopeEntity<?>>> findByPage(@RequestBody MessageEnvelopePageDto pageDto,
                                                               @PathVariable Boolean otherData) {
        return super.findByPage(pageDto, otherData);
    }

    @PostMapping("/read")
    @Operation(summary = "批量已读消息", operationId = "readMessageEnvelopeController")
    @SaCheckPermission(value = "messageEnvelope:read", orRole = "admin")
    public BaseVo<Integer> read(@RequestBody @Valid @NotEmpty(message = "ids列表不能为空") Set<String> ids) {
        return BaseVo.success(messageEnvelopeService.read(ids));
    }

}
