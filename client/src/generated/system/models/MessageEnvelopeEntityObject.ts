/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { UserEntity } from './UserEntity';
/**
 * 消息实体类
 */
export type MessageEnvelopeEntityObject = {
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
     * 业务类型: 站内信，小程序通知，邮件
     */
    businessType?: string;
    /**
     * 用户ID
     */
    userId?: string;
    user?: UserEntity;
    /**
     * 消息标题
     */
    title?: string;
    /**
     * 消息内容
     */
    content?: string;
    /**
     * 处理器: 邮件处理器，小程序通知处理器
     */
    handler?: string;
    /**
     * 元数据
     */
    metadata?: string;
    /**
     * 消息状态枚举
     */
    status?: MessageEnvelopeEntityObject.status;
};
export namespace MessageEnvelopeEntityObject {
    /**
     * 消息状态枚举
     */
    export enum status {
        UNREAD = 'UNREAD',
        READ = 'READ',
        IGNORE = 'IGNORE',
    }
}

