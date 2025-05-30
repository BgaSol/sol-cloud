package com.bgasol.common.core.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "分页响应数据")
public class PageVo<ENTITY> {
    @Schema(description = "总条数")
    private Long total;

    @Schema(description = "当前页码")
    private Long page;

    @Schema(description = "每页条数")
    private Long size;

    @Schema(description = "响应数据")
    private List<ENTITY> result;
}
