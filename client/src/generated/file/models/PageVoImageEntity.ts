/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ImageEntity } from './ImageEntity';
/**
 * 分页响应数据
 */
export type PageVoImageEntity = {
    /**
     * 总条数
     */
    total?: number;
    /**
     * 当前页码
     */
    page?: number;
    /**
     * 每页条数
     */
    size?: number;
    /**
     * 响应数据
     */
    result?: Array<ImageEntity>;
};

