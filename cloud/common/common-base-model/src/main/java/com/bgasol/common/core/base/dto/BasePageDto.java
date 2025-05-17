package com.bgasol.common.core.base.dto;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "分页查询通用参数")
public abstract class BasePageDto<T extends BaseEntity> {
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码不能小于1")
    @Schema(description = "页码")
    private Integer page;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页请求数量过小")
    @Max(value = 1000, message = "每页请求数量过大")
    @Schema(description = "每页条数")
    private Integer size;

    @Schema(hidden = true)
    public AbstractLambdaWrapper<T, LambdaQueryWrapper<T>> getQueryWrapper() {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
