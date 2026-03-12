package com.bgasol.model.system.requestLog.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.model.system.requestLog.entity.RequestLogEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "查询参数")
public class RequestLogPageDto extends BasePageDto<RequestLogEntity> {
    @Schema(description = "全局链路ID")
    @TableField("trace_id")
    private String traceId;

    @Schema(description = "服务名")
    private String serviceName;

    @Schema(description = "节点名")
    private String nodeName;

    @Schema(description = "节点IP")
    private String nodeIp;

    @Schema(description = "HTTP方法")
    @TableField("method")
    private String method;

    @Schema(description = "请求URI")
    @TableField("uri")
    private String uri;

    @Schema(description = "请求参数")
    @TableField("query_string")
    private String queryString;

    @Schema(description = "是否是重要异常")
    @TableField("is_primary_err")
    private Boolean isPrimaryErr;

    @Schema(description = "业务方法")
    @TableField("business_method")
    private String businessMethod;

    @Schema(description = "业务模块/Controller")
    @TableField("business_controller")
    private String businessController;

    @Schema(description = "用户ID")
    @TableField("user_id")
    @Transient
    private String userId;

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
        return queryWrapper;
    }
}