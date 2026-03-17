/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 消息分页DTO
 */
export type MessageEnvelopePageDto = {
    /**
     * 页码
     */
    page: number;
    /**
     * 每页条数
     */
    size: number;
    /**
     * 业务类型: 站内信，小程序通知，邮件
     */
    businessType?: string;
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
    status?: MessageEnvelopePageDto.status;
    /**
     * 描述
     */
    description?: string;
};
export namespace MessageEnvelopePageDto {
    /**
     * 消息状态枚举
     */
    export enum status {
        UNREAD = 'UNREAD',
        READ = 'READ',
        IGNORE = 'IGNORE',
    }
}

