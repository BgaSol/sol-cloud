/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseVoFileEntity } from '../models/BaseVoFileEntity';
import type { BaseVoImageEntity } from '../models/BaseVoImageEntity';
import type { BaseVoInteger } from '../models/BaseVoInteger';
import type { BaseVoListFileEntity } from '../models/BaseVoListFileEntity';
import type { BaseVoListImageEntity } from '../models/BaseVoListImageEntity';
import type { BaseVoListVideoEntity } from '../models/BaseVoListVideoEntity';
import type { BaseVoPageVoFileEntity } from '../models/BaseVoPageVoFileEntity';
import type { BaseVoPageVoImageEntity } from '../models/BaseVoPageVoImageEntity';
import type { BaseVoPageVoVideoEntity } from '../models/BaseVoPageVoVideoEntity';
import type { BaseVoVideoEntity } from '../models/BaseVoVideoEntity';
import type { FileCreateDto } from '../models/FileCreateDto';
import type { FilePageDto } from '../models/FilePageDto';
import type { FileUpdateDto } from '../models/FileUpdateDto';
import type { ImageCreateDto } from '../models/ImageCreateDto';
import type { ImagePageDto } from '../models/ImagePageDto';
import type { ImageUpdateDto } from '../models/ImageUpdateDto';
import type { VideoCreateDto } from '../models/VideoCreateDto';
import type { VideoPageDto } from '../models/VideoPageDto';
import type { VideoUpdateDto } from '../models/VideoUpdateDto';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class Service {
    /**
     * 分页查询视频
     * @param otherData
     * @param requestBody
     * @returns BaseVoPageVoVideoEntity OK
     * @throws ApiError
     */
    public static findByPageVideoController(
        otherData: boolean,
        requestBody: VideoPageDto,
    ): CancelablePromise<BaseVoPageVoVideoEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/video/page/{otherData}',
            path: {
                'otherData': otherData,
            },
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
    /**
     * 创建视频
     * @param requestBody
     * @returns BaseVoVideoEntity OK
     * @throws ApiError
     */
    public static insertVideoController(
        requestBody: VideoCreateDto,
    ): CancelablePromise<BaseVoVideoEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/video/insert',
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
    /**
     * 根据ID批量查询视频
     * @param otherData
     * @param requestBody
     * @returns BaseVoListVideoEntity OK
     * @throws ApiError
     */
    public static findByIdsVideoController(
        otherData: boolean,
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoListVideoEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/video/get/{otherData}',
            path: {
                'otherData': otherData,
            },
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
    /**
     * 删除视频
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static deleteVideoController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/video/delete',
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
    /**
     * 更新视频
     * @param requestBody
     * @returns BaseVoVideoEntity OK
     * @throws ApiError
     */
    public static applyVideoController(
        requestBody: VideoUpdateDto,
    ): CancelablePromise<BaseVoVideoEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/video/apply',
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
    /**
     * 分页查询图片
     * @param otherData
     * @param requestBody
     * @returns BaseVoPageVoImageEntity OK
     * @throws ApiError
     */
    public static findByPageImageController(
        otherData: boolean,
        requestBody: ImagePageDto,
    ): CancelablePromise<BaseVoPageVoImageEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/image/page/{otherData}',
            path: {
                'otherData': otherData,
            },
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
    /**
     * 创建图片
     * @param requestBody
     * @returns BaseVoImageEntity OK
     * @throws ApiError
     */
    public static insertImageController(
        requestBody: ImageCreateDto,
    ): CancelablePromise<BaseVoImageEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/image/insert',
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
    /**
     * 根据ID批量查询图片
     * @param otherData
     * @param requestBody
     * @returns BaseVoListImageEntity OK
     * @throws ApiError
     */
    public static findByIdsImageController(
        otherData: boolean,
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoListImageEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/image/get/{otherData}',
            path: {
                'otherData': otherData,
            },
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
    /**
     * 删除图片
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static deleteImageController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/image/delete',
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
    /**
     * 更新图片
     * @param requestBody
     * @returns BaseVoImageEntity OK
     * @throws ApiError
     */
    public static applyImageController(
        requestBody: ImageUpdateDto,
    ): CancelablePromise<BaseVoImageEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/image/apply',
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
    /**
     * 分页查询文件
     * @param otherData
     * @param requestBody
     * @returns BaseVoPageVoFileEntity OK
     * @throws ApiError
     */
    public static findByPageFileController(
        otherData: boolean,
        requestBody: FilePageDto,
    ): CancelablePromise<BaseVoPageVoFileEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/file/page/{otherData}',
            path: {
                'otherData': otherData,
            },
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
    /**
     * 保存|上传文件
     * @param formData
     * @returns BaseVoFileEntity OK
     * @throws ApiError
     */
    public static insertFileController(
        formData?: FileCreateDto,
    ): CancelablePromise<BaseVoFileEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/file/insert',
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
     * 根据ID批量查询文件
     * @param otherData
     * @param requestBody
     * @returns BaseVoListFileEntity OK
     * @throws ApiError
     */
    public static findByIdsFileController(
        otherData: boolean,
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoListFileEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/file/get/{otherData}',
            path: {
                'otherData': otherData,
            },
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
    /**
     * 删除文件
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static deleteFileController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/file/delete',
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
    /**
     * 更新文件
     * @param requestBody
     * @returns BaseVoFileEntity OK
     * @throws ApiError
     */
    public static applyFileController(
        requestBody: FileUpdateDto,
    ): CancelablePromise<BaseVoFileEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/file/apply',
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
    /**
     * 根据ID查询视频
     * @param id
     * @param otherData
     * @returns BaseVoVideoEntity OK
     * @throws ApiError
     */
    public static findByIdVideoController(
        id: string,
        otherData: boolean,
    ): CancelablePromise<BaseVoVideoEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/video/{id}/{otherData}',
            path: {
                'id': id,
                'otherData': otherData,
            },
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 在线播放视频
     * @param id
     * @param range
     * @returns binary OK
     * @throws ApiError
     */
    public static playVideo(
        id: string,
        range?: string,
    ): CancelablePromise<Blob> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/video/play/{id}',
            path: {
                'id': id,
            },
            headers: {
                'Range': range,
            },
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 根据ID查询图片
     * @param id
     * @param otherData
     * @returns BaseVoImageEntity OK
     * @throws ApiError
     */
    public static findByIdImageController(
        id: string,
        otherData: boolean,
    ): CancelablePromise<BaseVoImageEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/image/{id}/{otherData}',
            path: {
                'id': id,
                'otherData': otherData,
            },
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 下载图片
     * @param id
     * @returns binary OK
     * @throws ApiError
     */
    public static downloadImageController(
        id: string,
    ): CancelablePromise<Blob> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/image/download/{id}',
            path: {
                'id': id,
            },
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 根据ID查询文件
     * @param id
     * @param otherData
     * @returns BaseVoFileEntity OK
     * @throws ApiError
     */
    public static findByIdFileController(
        id: string,
        otherData: boolean,
    ): CancelablePromise<BaseVoFileEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/file/{id}/{otherData}',
            path: {
                'id': id,
                'otherData': otherData,
            },
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 下载文件
     * @param id
     * @returns binary OK
     * @throws ApiError
     */
    public static downloadFile(
        id: string,
    ): CancelablePromise<Blob> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/file/download/{id}',
            path: {
                'id': id,
            },
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
}
