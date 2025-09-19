/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 图片分页查询参数
 */
export type VideoPageDto = {
    /**
     * 页码
     */
    page: number;
    /**
     * 每页条数
     */
    size: number;
    /**
     * 视频名称
     */
    name?: string;
    /**
     * 大于（秒）视频时长
     */
    duration?: number;
    /**
     * 视频格式
     */
    format?: string;
    /**
     * 视频编码
     */
    codec?: string;
};

