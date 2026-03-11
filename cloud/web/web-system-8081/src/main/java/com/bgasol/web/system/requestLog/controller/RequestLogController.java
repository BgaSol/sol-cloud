package com.bgasol.web.system.requestLog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bgasol.common.core.base.controller.BaseController;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.core.base.dto.BaseUpdateDto;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.PageVo;
import com.bgasol.common.requestLog.service.RequestLogService;
import com.bgasol.model.system.requestLog.dto.RequestLogPageDto;
import com.bgasol.model.system.requestLog.entity.RequestLogEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "请求日志管理")
@RequestMapping("/request-log")
@Validated
public class RequestLogController extends BaseController<
        RequestLogEntity,
        RequestLogPageDto,
        BaseCreateDto<RequestLogEntity>,
        BaseUpdateDto<RequestLogEntity>> {
    private final RequestLogService requestLogService;

    @Override
    public RequestLogService commonBaseService() {
        return requestLogService;
    }

    @Override
    @PostMapping("/page")
    @Operation(summary = "分页查询请求日志", operationId = "findPageRequestLog")
    @SaCheckPermission(value = "requestLog:findByPage", orRole = "admin")
    public BaseVo<PageVo<RequestLogEntity>> findByPage(@RequestBody @Valid RequestLogPageDto pageDto) {
        return super.findByPage(pageDto);
    }

}
