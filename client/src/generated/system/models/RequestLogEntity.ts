/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { UserEntity } from './UserEntity';
/**
 * 系统请求日志实体类
 */
export type RequestLogEntity = {
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
     * 父id
     */
    parentId?: string;
    parent?: RequestLogEntity;
    /**
     * 子实体
     */
    children?: Array<RequestLogEntity>;
    /**
     * 全局链路ID
     */
    traceId?: string;
    /**
     * 服务名
     */
    serviceName?: string;
    /**
     * 节点名
     */
    nodeName?: string;
    /**
     * 节点IP
     */
    nodeIp?: string;
    /**
     * HTTP方法
     */
    method?: string;
    /**
     * 请求URI
     */
    uri?: string;
    /**
     * 请求参数
     */
    queryString?: string;
    /**
     * HTTP状态码
     */
    status?: number;
    /**
     * 线程ID
     */
    threadId?: number;
    /**
     * 异常堆栈
     */
    errorLog?: string;
    /**
     * 是否是重要异常
     */
    isPrimaryErr?: boolean;
    /**
     * 业务方法
     */
    businessMethod?: string;
    /**
     * 业务模块/Controller
     */
    businessController?: string;
    /**
     * 用户ID
     */
    userId?: string;
    userEntity?: UserEntity;
};

