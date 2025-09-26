/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 文件实体类
 */
export type FileEntity = {
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
     * 文件名称(包含文件后缀)
     */
    name?: string;
    /**
     * 文件地址
     */
    url?: string;
    /**
     * 文件大小
     */
    size?: number;
    /**
     * 文件HASH
     */
    hash?: string;
    /**
     * 文件状态：LOADING, SUCCESS
     */
    status?: FileEntity.status;
    /**
     * 文件后缀
     */
    suffix?: string;
    /**
     * 文件来源
     */
    source?: string;
    /**
     * 文件所在桶
     */
    bucket?: string;
};
export namespace FileEntity {
    /**
     * 文件状态：LOADING, SUCCESS
     */
    export enum status {
        LOADING = 'LOADING',
        SUCCESS = 'SUCCESS',
    }
}

