/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 创建菜单
 */
export type MenuCreateDto = {
    /**
     * 排序
     */
    sort?: number;
    /**
     * 描述
     */
    description?: string;
    /**
     * 父id
     */
    parentId?: string;
    /**
     * 菜单名
     */
    name?: string;
    /**
     * 菜单状态
     */
    status?: number;
    /**
     * 菜单类型：MENU-菜单，PAGE-页面，BUTTON-按钮
     */
    menuType?: MenuCreateDto.menuType;
    /**
     * 菜单路由地址
     */
    path?: string;
    /**
     * 菜单图标
     */
    icon?: string;
    /**
     * 菜单路由名
     */
    routeName?: string;
    /**
     * 按钮代码
     */
    buttonCode?: string;
    /**
     * 是否是外链
     */
    isExternal?: boolean;
    /**
     * 外链地址
     */
    externalUrl?: string;
    /**
     * 是否外链新窗口打开
     */
    isExternalOpen?: boolean;
    /**
     * 是否停用（置灰）
     */
    isDisabled?: boolean;
    /**
     * 是否隐藏（不显示）
     */
    isHidden?: boolean;
    /**
     * 菜单组
     */
    menuGroup?: string;
};
export namespace MenuCreateDto {
    /**
     * 菜单类型：MENU-菜单，PAGE-页面，BUTTON-按钮
     */
    export enum menuType {
        MENU = 'MENU',
        PAGE = 'PAGE',
        BUTTON = 'BUTTON',
    }
}

