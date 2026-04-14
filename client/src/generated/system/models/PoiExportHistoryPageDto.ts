/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * POI导出记录分页查询参数
 */
export type PoiExportHistoryPageDto = {
    /**
     * 页码
     */
    page: number;
    /**
     * 每页条数
     */
    size: number;
    /**
     * 导出业务标识
     */
    exportServer?: string;
    /**
     * 导出名称
     */
    exportName?: string;
    /**
     * 导出状态：0 进行中 / 1 成功 / 2 失败
     */
    status?: number;
};

