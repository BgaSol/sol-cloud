/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 部门实体
 */
export type DepartmentEntity = {
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
     * 父id
     */
    parentId?: string;
    parent?: DepartmentEntity;
    /**
     * 子实体
     */
    children?: Array<DepartmentEntity>;
    /**
     * 是否删除
     */
    deleted?: boolean;
    /**
     * 部门名
     */
    name?: string;
    /**
     * 部门编码
     */
    code?: string;
    /**
     * 部门域名
     */
    domain?: string;
    /**
     * 部门地址
     */
    address?: string;
    /**
     * 部门电话
     */
    phone?: string;
    /**
     * 部门备注HTML
     */
    html?: string;
    /**
     * 部门图标id 关联图片id
     */
    iconId?: string;
};

