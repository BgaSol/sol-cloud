/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { FileEntity } from './FileEntity';
/**
 * 视频实体类
 */
export type VideoEntity = {
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
     * 视频名称
     */
    name?: string;
    /**
     * 视频宽度
     */
    width?: number;
    /**
     * 视频高度
     */
    height?: number;
    /**
     * 视频时长（秒）
     */
    duration?: number;
    /**
     * 视频格式
     */
    format?: string;
    /**
     * 视频码率
     */
    bitrate?: number;
    /**
     * 视频帧率
     */
    fps?: number;
    /**
     * 视频编码
     */
    codec?: string;
    /**
     * 视频文件id
     */
    fileId?: string;
    file?: FileEntity;
};

