/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 更新权限Dto
 */
export type PermissionUpdateDto = {
    /**
     * 主键
     */
    id: string;
    /**
     * 排序
     */
    sort?: number;
    /**
     * 描述
     */
    description?: string;
    /**
     * 类型
     */
    type?: string;
    /**
     * 父权限id
     */
    parentId?: string;
    /**
     * 权限名
     */
    name?: string;
    /**
     * 权限编码
     */
    code?: string;
    /**
     * 权限路径
     */
    path?: string;
    /**
     * 微服务名
     */
    microService?: string;
};

