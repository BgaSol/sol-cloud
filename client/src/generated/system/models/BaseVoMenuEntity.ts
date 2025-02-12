/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { MenuEntity } from './MenuEntity';
/**
 * 基础响应数据
 */
export type BaseVoMenuEntity = {
    /**
     * 响应码
     */
    code?: number;
    /**
     * 响应消息
     */
    message?: string;
    data?: MenuEntity;
    /**
     * 响应时间
     */
    time?: string;
    /**
     * 响应类型
     */
    type?: BaseVoMenuEntity.type;
};
export namespace BaseVoMenuEntity {
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

