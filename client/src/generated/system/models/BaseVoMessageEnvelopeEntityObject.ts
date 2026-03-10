/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { MessageEnvelopeEntityObject } from './MessageEnvelopeEntityObject';
/**
 * 基础响应数据
 */
export type BaseVoMessageEnvelopeEntityObject = {
    /**
     * 响应码
     */
    code?: number;
    /**
     * 响应消息
     */
    message?: string;
    data?: MessageEnvelopeEntityObject;
    /**
     * 响应时间
     */
    time?: string;
    /**
     * 响应类型
     */
    type?: BaseVoMessageEnvelopeEntityObject.type;
};
export namespace BaseVoMessageEnvelopeEntityObject {
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

