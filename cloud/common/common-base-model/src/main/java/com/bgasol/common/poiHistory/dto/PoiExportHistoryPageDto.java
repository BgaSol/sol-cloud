package com.bgasol.common.poiHistory.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.common.poiHistory.entity.PoiExportHistoryEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "POI导出记录分页查询参数")
public class PoiExportHistoryPageDto extends BasePageDto<PoiExportHistoryEntity> {

    @Schema(description = "导出业务标识")
    private String exportServer;

    @Schema(description = "导出名称")
    private String exportName;

    @Schema(description = "导出状态：0 进行中 / 1 成功 / 2 失败")
    @TableField("status")
    private Integer status;

}
