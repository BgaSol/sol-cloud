package com.bgasol.common.core.base.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bgasol.common.core.base.entity.PoiExportHistoryEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

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

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public Wrapper<PoiExportHistoryEntity> getQueryWrapper() {
        return Wrappers.<PoiExportHistoryEntity>lambdaQuery()
                .like(ObjectUtils.isNotEmpty(exportServer), PoiExportHistoryEntity::getExportServer, exportServer)
                .like(ObjectUtils.isNotEmpty(exportName), PoiExportHistoryEntity::getExportName, exportName)
                .eq(ObjectUtils.isNotEmpty(status), PoiExportHistoryEntity::getStatus, status);
    }
}
