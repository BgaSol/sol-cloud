/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 查询参数
 */
export type RequestLogPageDto = {
    /**
     * 页码
     */
    page: number;
    /**
     * 每页条数
     */
    size: number;
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
};

