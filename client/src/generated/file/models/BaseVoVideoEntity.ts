/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { VideoEntity } from './VideoEntity';
/**
 * 基础响应数据
 */
export type BaseVoVideoEntity = {
    /**
     * 响应码
     */
    code?: number;
    /**
     * 响应消息
     */
    message?: string;
    data?: VideoEntity;
    /**
     * 响应时间
     */
    time?: string;
    /**
     * 响应类型
     */
    type?: BaseVoVideoEntity.type;
};
export namespace BaseVoVideoEntity {
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

