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
     * 更新视频
     * @param requestBody
     * @returns BaseVoVideoEntity OK
     * @throws ApiError
     */
    public static updateVideo(
        requestBody: VideoUpdateDto,
    ): CancelablePromise<BaseVoVideoEntity> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/video',
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
     * 新增视频
     * @param requestBody
     * @returns BaseVoVideoEntity OK
     * @throws ApiError
     */
    public static saveVideo(
        requestBody: VideoCreateDto,
    ): CancelablePromise<BaseVoVideoEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/video',
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
    public static updateImage(
        requestBody: ImageUpdateDto,
    ): CancelablePromise<BaseVoImageEntity> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/image',
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
     * 新增图片
     * @param requestBody
     * @returns BaseVoImageEntity OK
     * @throws ApiError
     */
    public static saveImage(
        requestBody: ImageCreateDto,
    ): CancelablePromise<BaseVoImageEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/image',
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
     * 更新文件状态
     * @param requestBody
     * @returns BaseVoFileEntity OK
     * @throws ApiError
     */
    public static updateFile(
        requestBody: FileUpdateDto,
    ): CancelablePromise<BaseVoFileEntity> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/file',
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
    public static saveFile(
        formData?: FileCreateDto,
    ): CancelablePromise<BaseVoFileEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/file',
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
     * 分页查询视频
     * @param requestBody
     * @returns BaseVoPageVoVideoEntity OK
     * @throws ApiError
     */
    public static findPageVideo(
        requestBody: VideoPageDto,
    ): CancelablePromise<BaseVoPageVoVideoEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/video/page',
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
     * @param requestBody
     * @returns BaseVoPageVoImageEntity OK
     * @throws ApiError
     */
    public static findPageImage(
        requestBody: ImagePageDto,
    ): CancelablePromise<BaseVoPageVoImageEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/image/page',
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
     * @param requestBody
     * @returns BaseVoPageVoFileEntity OK
     * @throws ApiError
     */
    public static findPageFile(
        requestBody: FilePageDto,
    ): CancelablePromise<BaseVoPageVoFileEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/file/page',
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
     * 查询视频
     * @param id
     * @returns BaseVoVideoEntity OK
     * @throws ApiError
     */
    public static findVideoById(
        id: string,
    ): CancelablePromise<BaseVoVideoEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/video/{id}',
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
     * 在线播放文件
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
     * 根据id批量查询视频
     * @param ids
     * @returns BaseVoListVideoEntity OK
     * @throws ApiError
     */
    public static findVideoByIds(
        ids: string,
    ): CancelablePromise<BaseVoListVideoEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/video/ids/{ids}',
            path: {
                'ids': ids,
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
     * 查询图片
     * @param id
     * @returns BaseVoImageEntity OK
     * @throws ApiError
     */
    public static findImageById(
        id: string,
    ): CancelablePromise<BaseVoImageEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/image/{id}',
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
     * 根据id批量查询图片
     * @param ids
     * @returns BaseVoListImageEntity OK
     * @throws ApiError
     */
    public static findImageByIds(
        ids: string,
    ): CancelablePromise<BaseVoListImageEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/image/ids/{ids}',
            path: {
                'ids': ids,
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
    public static downloadImage(
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
     * 根据id查询文件
     * @param id
     * @returns BaseVoFileEntity OK
     * @throws ApiError
     */
    public static findFileById(
        id: string,
    ): CancelablePromise<BaseVoFileEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/file/{id}',
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
     * 根据id批量查询图片
     * @param ids
     * @returns BaseVoListFileEntity OK
     * @throws ApiError
     */
    public static findFileByIds(
        ids: string,
    ): CancelablePromise<BaseVoListFileEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/file/ids/{ids}',
            path: {
                'ids': ids,
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
    /**
     * 删除视频
     * @param ids
     * @returns BaseVoInteger<any> OK
     * @throws ApiError
     */
    public static deleteVideo(
        ids: string,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/video/{ids}',
            path: {
                'ids': ids,
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
     * 删除图片
     * @param ids
     * @returns BaseVoInteger<any> OK
     * @throws ApiError
     */
    public static deleteImage(
        ids: string,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/image/{ids}',
            path: {
                'ids': ids,
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
     * 删除文件
     * @param ids
     * @returns BaseVoInteger<any> OK
     * @throws ApiError
     */
    public static deleteFile(
        ids: string,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/file/{ids}',
            path: {
                'ids': ids,
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
