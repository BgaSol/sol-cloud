package com.bgasol.common.core.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "分页响应数据")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVo<ENTITY> {
    @Schema(description = "总条数")
    private Long total;

    @Schema(description = "当前页码")
    private Long page;

    @Schema(description = "每页条数")
    private Long size;

    @Schema(description = "响应数据")
    private List<ENTITY> result;

    public PageVo(Long total, List<ENTITY> result, Long page, Long size) {
        this.total = total;
        this.result = result;

        this.page = page;
        this.size = size;
    }
}
