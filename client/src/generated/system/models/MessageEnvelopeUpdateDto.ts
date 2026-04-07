/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 创建消息
 */
export type MessageEnvelopeUpdateDto = {
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
    /**
     * 类型
     */
    type?: string;
    /**
     * 业务类型: 站内信，小程序通知，邮件
     */
    businessType?: string;
    /**
     * 用户ID
     */
    userId?: string;
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
    status?: MessageEnvelopeUpdateDto.status;
};
export namespace MessageEnvelopeUpdateDto {
    /**
     * 消息状态枚举
     */
    export enum status {
        UNREAD = 'UNREAD',
        READ = 'READ',
        IGNORE = 'IGNORE',
    }
}

