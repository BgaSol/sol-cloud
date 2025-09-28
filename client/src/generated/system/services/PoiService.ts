/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseVoImportResult } from '../models/BaseVoImportResult';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class PoiService {
    /**
     * 导入角色
     * @param formData
     * @returns BaseVoImportResult OK
     * @throws ApiError
     */
    public static importRole(
        formData?: {
            file: Blob;
        },
    ): CancelablePromise<BaseVoImportResult> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/role-poi/import',
            formData: formData,
            mediaType: 'multipart/form-data',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 下载角色导入模板
     * @returns binary OK
     * @throws ApiError
     */
    public static downloadRoleImportTemplate(): CancelablePromise<Blob> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/role-poi/template-download',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
}
