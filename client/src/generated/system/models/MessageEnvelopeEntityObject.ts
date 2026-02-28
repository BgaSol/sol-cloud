/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
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
     * 接收者类型枚举
     */
    messageRecipientTypeEnum?: MessageEnvelopeEntityObject.messageRecipientTypeEnum;
    /**
     * 接收者id
     */
    recipientId?: string;
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
    /**
     * 消息体
     */
    body?: Record<string, any>;
};
export namespace MessageEnvelopeEntityObject {
    /**
     * 接收者类型枚举
     */
    export enum messageRecipientTypeEnum {
        USER = 'USER',
        ROLE = 'ROLE',
        DEPARTMENT = 'DEPARTMENT',
    }
    /**
     * 消息状态枚举
     */
    export enum status {
        UNREAD = 'UNREAD',
        READ = 'READ',
        IGNORE = 'IGNORE',
    }
}

