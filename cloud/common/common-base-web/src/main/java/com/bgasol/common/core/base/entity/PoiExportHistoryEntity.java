package com.bgasol.common.core.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bgasol.common.core.base.entity.BaseEntity;
import com.bgasol.model.system.department.entity.DepartmentEntity;
import com.bgasol.model.system.user.bo.ScopeField;
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
@Schema(description = "POI导出记录实体")
@TableName("poi_t_export_history")
@Entity
public class PoiExportHistoryEntity extends BaseEntity {

    @Schema(description = "导出业务标识")
    @TableField("export_server")
    private String exportServer;

    @Schema(description = "导出名称")
    @TableField("export_name")
    private String exportName;

    @Schema(description = "导出参数快照")
    @TableField("params")
    private String params;

    @Schema(description = "导出状态：0 未开始 / 1 进行中 / 2 成功 / 3 失败 / 4 文件删除")
    @TableField("status")
    private Integer status;

    @Schema(description = "文件ID，关联文件存储表")
    @TableField("file_id")
    private String fileId;

    @Schema(description = "错误信息")
    @TableField("error_message")
    private String errorMessage;

    @Schema(description = "部门id，关联部门表")
    @TableField("department_id")
    private String departmentId;

    @Schema(description = "用户名")
    @TableField("user_name")
    private String userName;

    @Schema(description = "文件大小")
    @TableField("file_size")
    private Long fileSize;

    @Schema(description = "文件数量")
    @TableField("file_num")
    private Long fileNum;

    @Schema(description = "部门")
    @TableField(exist = false)
    @ManyToOne
    @JoinColumn(name = "department_id")
    @ScopeField
    private DepartmentEntity department;

}
