package com.bgasol.model.system.requestLog.dto;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.model.system.requestLog.entity.RequestLogEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "查询参数")
public class RequestLogPageDto extends BasePageDto<RequestLogEntity> {
    @Schema(description = "全局链路ID")
    private String traceId;

    @Schema(description = "服务名")
    private String serviceName;

    @Schema(description = "节点名")
    private String nodeName;

    @Schema(description = "节点IP")
    private String nodeIp;

    @Schema(description = "HTTP方法")
    private String method;

    @Schema(description = "请求URI")
    private String uri;

    @Schema(description = "请求参数")
    private String queryString;

    @Schema(description = "是否是重要异常")
    private Boolean isPrimaryErr;

    @Schema(description = "业务方法")
    private String businessMethod;

    @Schema(description = "业务模块/Controller")
    private String businessController;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "创建时间")
    @NotNull
    private Date createTime;

    @Override
    public Wrapper<RequestLogEntity> getQueryWrapper() {
        LambdaQueryWrapper<RequestLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(traceId), RequestLogEntity::getTraceId, traceId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(serviceName), RequestLogEntity::getServiceName, serviceName);
        queryWrapper.eq(ObjectUtils.isNotEmpty(nodeName), RequestLogEntity::getNodeName, nodeName);
        queryWrapper.eq(ObjectUtils.isNotEmpty(nodeIp), RequestLogEntity::getNodeIp, nodeIp);
        queryWrapper.eq(ObjectUtils.isNotEmpty(method), RequestLogEntity::getMethod, method);
        queryWrapper.eq(ObjectUtils.isNotEmpty(uri), RequestLogEntity::getUri, uri);
        queryWrapper.eq(ObjectUtils.isNotEmpty(queryString), RequestLogEntity::getQueryString, queryString);
        queryWrapper.eq(ObjectUtils.isNotEmpty(isPrimaryErr), RequestLogEntity::getIsPrimaryErr, isPrimaryErr);
        queryWrapper.eq(ObjectUtils.isNotEmpty(businessMethod), RequestLogEntity::getBusinessMethod, businessMethod);
        queryWrapper.eq(ObjectUtils.isNotEmpty(businessController), RequestLogEntity::getBusinessController, businessController);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), RequestLogEntity::getUserId, userId);
        queryWrapper.isNull(RequestLogEntity::getParentId);
        queryWrapper.orderByDesc(RequestLogEntity::getCreateTime);
        return queryWrapper;
    }
}