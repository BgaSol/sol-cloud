package com.bgasol.model.system.requestLog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseTreeEntity;
import com.bgasol.common.message.dto.MessageBody;
import com.bgasol.model.system.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "系统请求日志实体类")
@TableName(value = RequestLogTable.TableName, autoResultMap = true)
@Entity
public class RequestLogEntity extends BaseTreeEntity<RequestLogEntity> implements MessageBody {
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

    @Schema(description = "HTTP状态码")
    @TableField("status")
    private Integer status;

    @Schema(description = "线程ID")
    @TableField("thread_id")
    private Long threadId;

    @Schema(description = "异常堆栈")
    @TableField("error_log")
    private String errorLog;

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

    @Schema(description = "用户")
    @TableField(exist = false)
    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserEntity user;
}