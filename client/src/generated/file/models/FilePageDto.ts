/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 文件分页查询参数
 */
export type FilePageDto = {
    /**
     * 页码
     */
    page: number;
    /**
     * 每页条数
     */
    size: number;
    /**
     * 文件名称
     */
    name?: string;
    /**
     * 文件地址
     */
    url?: string;
    /**
     * 文件大小范围-最大值
     */
    maxLen?: number;
    /**
     * 文件大小范围-最小值
     */
    minLen?: number;
    /**
     * 文件HASH
     */
    hash?: string;
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
    /**
     * 文件所在桶
     */
    bucket?: string;
};

