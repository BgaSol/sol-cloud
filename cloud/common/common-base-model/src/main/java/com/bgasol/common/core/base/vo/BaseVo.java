package com.bgasol.common.core.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "基础响应数据")
public class BaseVo<T> {
    @Schema(description = "响应码")
    private Integer code;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "响应时间")
    private Date time;

    @Schema(description = "响应类型")
    private ResponseType type;

    /**
     * 成功响应
     *
     * @param data 响应数据
     */
    static public <T> BaseVo<T> success(T data) {
        BaseVo<T> baseVo = new BaseVo<>();
        baseVo.setCode(200);
        baseVo.setData(data);
        baseVo.setTime(new Date());
        return baseVo;
    }

    /**
     * 成功响应
     *
     * @param data    响应数据
     * @param message 响应消息
     */
    static public <T> BaseVo<T> success(T data, String message) {
        BaseVo<T> baseVo = new BaseVo<>();
        baseVo.setCode(200);
        baseVo.setMessage(message);
        baseVo.setData(data);
        baseVo.setTime(new Date());
        baseVo.setType(ResponseType.SUCCESS);
        return baseVo;
    }

    /**
     * 错误响应
     *
     * @param message 响应消息
     */
    static public <T> BaseVo<T> error(String message) {
        BaseVo<T> baseVo = new BaseVo<>();
        baseVo.setCode(500);
        baseVo.setMessage(message);
        baseVo.setTime(new Date());
        return baseVo;
    }

    /**
     * 错误响应
     *
     * @param data    响应数据
     * @param message 响应消息
     */
    static public <T> BaseVo<T> error(T data, String message) {
        BaseVo<T> baseVo = new BaseVo<>();
        baseVo.setData(data);
        baseVo.setCode(500);
        baseVo.setMessage(message);
        baseVo.setTime(new Date());
        return baseVo;
    }

    /**
     * 错误响应
     *
     * @param message 响应消息
     * @param type    响应类型
     */
    static public <T> BaseVo<T> error(String message, ResponseType type) {
        BaseVo<T> baseVo = new BaseVo<>();
        baseVo.setCode(500);
        baseVo.setMessage(message);
        baseVo.setTime(new Date());
        baseVo.setType(type);
        return baseVo;
    }

    /**
     * 未登录
     */
    public static BaseVo<Void> code401() {
        BaseVo<Void> baseVo = new BaseVo<>();
        baseVo.setCode(401);
        baseVo.setMessage("未登录");
        baseVo.setTime(new Date());
        return baseVo;
    }

    /**
     * 无权限
     */
    public static BaseVo<Void> code403() {
        BaseVo<Void> baseVo = new BaseVo<>();
        baseVo.setCode(403);
        baseVo.setMessage("无权限");
        baseVo.setTime(new Date());
        return baseVo;
    }

    /**
     * 参数错误
     *
     * @param verificationResults 参数校验结果
     */
    public static BaseVo<List<VerificationResult>> code400(List<VerificationResult> verificationResults) {
        BaseVo<List<VerificationResult>> baseVo = new BaseVo<>();
        baseVo.setCode(400);
        baseVo.setTime(new Date());
        baseVo.setData(verificationResults);
        return baseVo;
    }
}