/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PageVoPoiExportHistoryEntity } from './PageVoPoiExportHistoryEntity';
/**
 * 基础响应数据
 */
export type BaseVoPageVoPoiExportHistoryEntity = {
    /**
     * 响应码
     */
    code?: number;
    /**
     * 响应消息
     */
    message?: string;
    data?: PageVoPoiExportHistoryEntity;
    /**
     * 响应时间
     */
    time?: string;
    /**
     * 响应类型
     */
    type?: BaseVoPageVoPoiExportHistoryEntity.type;
};
export namespace BaseVoPageVoPoiExportHistoryEntity {
    /**
     * 响应类型
     */
    export enum type {
        SUCCESS = 'SUCCESS',
        WARNING = 'WARNING',
        INFO = 'INFO',
        ERROR = 'ERROR',
    }
}

