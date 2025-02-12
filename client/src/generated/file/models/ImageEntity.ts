/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { FileEntity } from './FileEntity';
/**
 * 图片实体类
 */
export type ImageEntity = {
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
     * 是否删除
     */
    deleted?: number;
    /**
     * 图片名称
     */
    name?: string;
    /**
     * 图片宽度
     */
    width?: number;
    /**
     * 图片高度
     */
    height?: number;
    /**
     * 图片文件id
     */
    fileId?: string;
    file?: FileEntity;
};

