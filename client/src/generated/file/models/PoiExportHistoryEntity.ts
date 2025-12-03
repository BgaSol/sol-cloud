/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { DepartmentEntity } from './DepartmentEntity';
/**
 * POI导出记录实体
 */
export type PoiExportHistoryEntity = {
    id?: string;
    /**
     * 类型
     */
    type?: string;
    /**
     * 排序
     */
    sort?: number;
    /**
     * 创建时间
     */
    createTime?: string;
    /**
     * 更新时间
     */
    updateTime?: string;
    /**
     * 描述
     */
    description?: string;
    /**
     * 导出业务标识
     */
    exportServer?: string;
    /**
     * 导出名称
     */
    exportName?: string;
    /**
     * 导出参数快照
     */
    params?: string;
    /**
     * 导出状态：0 未开始 / 1 进行中 / 2 成功 / 3 失败 / 4 文件删除
     */
    status?: number;
    /**
     * 文件ID，关联文件存储表
     */
    fileId?: string;
    /**
     * 错误信息
     */
    errorMessage?: string;
    /**
     * 部门id，关联部门表
     */
    departmentId?: string;
    /**
     * 用户名
     */
    userName?: string;
    /**
     * 文件大小
     */
    fileSize?: number;
    /**
     * 文件数量
     */
    fileNum?: number;
    department?: DepartmentEntity;
};

