package com.bgasol.common.core.base.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class RelationRow {
    /**
     * 关系左端（源端 / 主体）
     * 通常对应：主表ID / owning side
     */
    private String sourceId;

    /**
     * 关系右端（目标端 / 被关联方）
     * 通常对应：从表ID / target side
     */
    private String targetId;

    public static RelationRow of(String sourceId, String targetId) {
        return RelationRow.builder()
                .sourceId(sourceId)
                .targetId(targetId)
                .build();
    }
}
