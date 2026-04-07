/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 创建文件
 */
export type FileCreateDto = {
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
     * 自定义文件id
     */
    id?: string;
    uploadFile?: Blob;
    /**
     * 文件名称(包含文件后缀)
     */
    name?: string;
    /**
     * 文件hash
     */
    hash?: string;
    /**
     * 文件状态：LOADING, SUCCESS
     */
    status?: FileCreateDto.status;
    /**
     * 文件后缀
     */
    suffix?: string;
    /**
     * 文件来源
     */
    source?: string;
};
export namespace FileCreateDto {
    /**
     * 文件状态：LOADING, SUCCESS
     */
    export enum status {
        LOADING = 'LOADING',
        SUCCESS = 'SUCCESS',
    }
}

