/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { DepartmentEntity } from './DepartmentEntity';
/**
 * 基础响应数据
 */
export type BaseVoListDepartmentEntity = {
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
    data?: Array<DepartmentEntity>;
    /**
     * 响应时间
     */
    time?: string;
    /**
     * 响应类型
     */
    type?: BaseVoListDepartmentEntity.type;
};
export namespace BaseVoListDepartmentEntity {
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

