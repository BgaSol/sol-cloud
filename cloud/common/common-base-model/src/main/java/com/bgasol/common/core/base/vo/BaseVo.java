package com.bgasol.common.core.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "基础响应数据")
public class BaseVo<T> {
    /// 响应码
    @Schema(description = "响应码")
    private Integer code;

    /// 响应消息
    @Schema(description = "响应消息")
    private String message;

    /// 响应数据
    @Schema(description = "响应数据")
    private T data;

    /// 响应时间
    @Schema(description = "响应时间")
    private Date time;

    /// 响应类型
    @Schema(description = "响应类型")
    private ResponseType type;

    /**
     * 成功响应
     *
     * @param data 响应数据
     */
    static public <T> BaseVo<T> success(T data) {
        return BaseVo.<T>builder()
                .code(200)
                .data(data)
                .time(new Date())
                .build();
    }

    /**
     * 成功响应
     *
     * @param data    响应数据
     * @param message 响应消息
     */
    static public <T> BaseVo<T> success(T data, String message) {
        return BaseVo.<T>builder()
                .code(200)
                .data(data)
                .time(new Date())
                .message(message)
                .build();
    }

    /**
     * 错误响应
     *
     * @param message 响应消息
     */
    static public <T> BaseVo<T> error(String message) {
        return BaseVo.<T>builder()
                .code(500)
                .time(new Date())
                .message(message)
                .type(ResponseType.ERROR)
                .build();
    }

    /**
     * 错误响应
     *
     * @param data    响应数据
     * @param message 响应消息
     */
    static public <T> BaseVo<T> error(T data, String message) {
        return BaseVo.<T>builder()
                .code(500)
                .data(data)
                .time(new Date())
                .message(message)
                .type(ResponseType.ERROR)
                .build();
    }

    /**
     * 错误响应
     *
     * @param message 响应消息
     * @param type    响应类型
     */
    static public <T> BaseVo<T> error(String message, ResponseType type) {
        return BaseVo.<T>builder()
                .code(500)
                .time(new Date())
                .message(message)
                .type(type)
                .build();
    }

    /**
     * 未登录
     */
    public static BaseVo<Void> code401() {
        return BaseVo.<Void>builder()
                .code(401)
                .time(new Date())
                .message("未登录")
                .type(ResponseType.WARNING)
                .build();
    }

    /**
     * 无权限
     */
    public static BaseVo<Void> code403() {
        return BaseVo.<Void>builder()
                .code(403)
                .time(new Date())
                .message("无权限")
                .type(ResponseType.WARNING)
                .build();
    }

    /**
     * 参数错误
     *
     * @param verificationResults 参数校验结果
     */
    public static BaseVo<List<VerificationResult>> code400(List<VerificationResult> verificationResults) {
        return BaseVo.<List<VerificationResult>>builder()
                .code(400)
                .time(new Date())
                .data(verificationResults)
                .message("参数错误")
                .type(ResponseType.WARNING)
                .build();
    }
}