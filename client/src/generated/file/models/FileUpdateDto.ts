/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 更新文件
 */
export type FileUpdateDto = {
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
    uploadFile?: Blob;
    name?: string;
    /**
     * 文件状态
     */
    status?: string;
    /**
     * 文件后缀
     */
    suffix?: string;
    /**
     * 文件来源
     */
    source?: string;
};

