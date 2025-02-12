/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 创建用户数据传输对象
 */
export type UserCreateDto = {
    /**
     * 排序
     */
    sort?: number;
    /**
     * 描述
     */
    description?: string;
    /**
     * 用户名
     */
    username: string;
    /**
     * 密码
     */
    password: string;
    /**
     * 昵称
     */
    nickname?: string;
    /**
     * 邮箱
     */
    email?: string;
    /**
     * 手机号
     */
    phone?: string;
    /**
     * 账户锁定
     */
    locked: boolean;
    /**
     * 角色id列表
     */
    roleIds?: Array<string>;
    /**
     * 部门id
     */
    departmentId?: string;
};

