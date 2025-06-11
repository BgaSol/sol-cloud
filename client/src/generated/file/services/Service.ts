/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseVoFileEntity } from '../models/BaseVoFileEntity';
import type { BaseVoImageEntity } from '../models/BaseVoImageEntity';
import type { BaseVoInteger } from '../models/BaseVoInteger';
import type { BaseVoPageVoFileEntity } from '../models/BaseVoPageVoFileEntity';
import type { BaseVoPageVoImageEntity } from '../models/BaseVoPageVoImageEntity';
import type { FileCreateDto } from '../models/FileCreateDto';
import type { FilePageDto } from '../models/FilePageDto';
import type { FileUpdateDto } from '../models/FileUpdateDto';
import type { ImageCreateDto } from '../models/ImageCreateDto';
import type { ImagePageDto } from '../models/ImagePageDto';
import type { ImageUpdateDto } from '../models/ImageUpdateDto';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class Service {
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
                500: `业务异常`,
            },
        });
    }
    /**
     * 更新|上传文件
     * @param fileUpdateDto
     * @returns BaseVoFileEntity OK
     * @throws ApiError
     */
    public static updateFile(
        fileUpdateDto: FileUpdateDto,
    ): CancelablePromise<BaseVoFileEntity> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/file',
            query: {
                'fileUpdateDto': fileUpdateDto,
            },
            errors: {
                400: `参数校验异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 保存|上传文件
     * @param fileCreateDto
     * @returns BaseVoFileEntity OK
     * @throws ApiError
     */
    public static saveFile(
        fileCreateDto: FileCreateDto,
    ): CancelablePromise<BaseVoFileEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/file',
            query: {
                'fileCreateDto': fileCreateDto,
            },
            errors: {
                400: `参数校验异常`,
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
    public static downloadFile(
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
    public static downloadFile1(
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
                500: `业务异常`,
            },
        });
    }
}
