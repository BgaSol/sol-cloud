/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { MenuEntity } from './MenuEntity';
import type { PermissionEntity } from './PermissionEntity';
/**
 * 角色实体
 */
export type RoleEntity = {
    id?: string;
    /**
     * 类型
     */
    type?: string;
    /**
     * 排序
     */
    sort?: number;
    /**
     * 创建时间
     */
    createTime?: string;
    /**
     * 更新时间
     */
    updateTime?: string;
    /**
     * 描述
     */
    description?: string;
    /**
     * 角色名
     */
    name?: string;
    /**
     * 角色编码
     */
    code?: string;
    /**
     * 角色状态
     */
    status?: number;
    /**
     * 角色权限
     */
    permissions?: Array<PermissionEntity>;
    /**
     * 角色菜单
     */
    menus?: Array<MenuEntity>;
};

