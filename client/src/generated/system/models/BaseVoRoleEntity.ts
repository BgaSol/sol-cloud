/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { RoleEntity } from './RoleEntity';
/**
 * 基础响应数据
 */
export type BaseVoRoleEntity = {
    /**
     * 响应码
     */
    code?: number;
    /**
     * 响应消息
     */
    message?: string;
    data?: RoleEntity;
    /**
     * 响应时间
     */
    time?: string;
    /**
     * 响应类型
     */
    type?: BaseVoRoleEntity.type;
};
export namespace BaseVoRoleEntity {
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

