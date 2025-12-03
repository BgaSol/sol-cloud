/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseVoPageVoPoiExportHistoryEntity } from '../models/BaseVoPageVoPoiExportHistoryEntity';
import type { PoiExportHistoryPageDto } from '../models/PoiExportHistoryPageDto';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class PoiService {
    /**
     * 分页查询POI导出记录
     * @param requestBody
     * @returns BaseVoPageVoPoiExportHistoryEntity OK
     * @throws ApiError
     */
    public static findPagePoiExportHistory(
        requestBody: PoiExportHistoryPageDto,
    ): CancelablePromise<BaseVoPageVoPoiExportHistoryEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/poi-export-history/page',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
}
