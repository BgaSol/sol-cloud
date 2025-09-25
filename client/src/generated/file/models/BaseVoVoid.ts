/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 基础响应数据
 */
export type BaseVoVoid = {
    /**
     * 响应码
     */
    code?: number;
    /**
     * 响应消息
     */
    message?: string;
    /**
     * 响应数据
     */
    data?: Record<string, any>;
    /**
     * 响应时间
     */
    time?: string;
    /**
     * 响应类型
     */
    type?: BaseVoVoid.type;
};
export namespace BaseVoVoid {
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

