/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BasePageDtoRoleEntity } from '../models/BasePageDtoRoleEntity';
import type { BaseVoDepartmentEntity } from '../models/BaseVoDepartmentEntity';
import type { BaseVoImportResult } from '../models/BaseVoImportResult';
import type { BaseVoInteger } from '../models/BaseVoInteger';
import type { BaseVoListDepartmentEntity } from '../models/BaseVoListDepartmentEntity';
import type { BaseVoListMenuEntity } from '../models/BaseVoListMenuEntity';
import type { BaseVoListMessageEnvelopeEntityObject } from '../models/BaseVoListMessageEnvelopeEntityObject';
import type { BaseVoListPermissionEntity } from '../models/BaseVoListPermissionEntity';
import type { BaseVoListRoleEntity } from '../models/BaseVoListRoleEntity';
import type { BaseVoListUserEntity } from '../models/BaseVoListUserEntity';
import type { BaseVoMenuEntity } from '../models/BaseVoMenuEntity';
import type { BaseVoMessageEnvelopeEntityObject } from '../models/BaseVoMessageEnvelopeEntityObject';
import type { BaseVoPageVoMessageEnvelopeEntityObject } from '../models/BaseVoPageVoMessageEnvelopeEntityObject';
import type { BaseVoPageVoRequestLogEntity } from '../models/BaseVoPageVoRequestLogEntity';
import type { BaseVoPageVoRoleEntity } from '../models/BaseVoPageVoRoleEntity';
import type { BaseVoPageVoUserEntity } from '../models/BaseVoPageVoUserEntity';
import type { BaseVoPermissionEntity } from '../models/BaseVoPermissionEntity';
import type { BaseVoRoleEntity } from '../models/BaseVoRoleEntity';
import type { BaseVoSaTokenInfo } from '../models/BaseVoSaTokenInfo';
import type { BaseVoString } from '../models/BaseVoString';
import type { BaseVoUserEntity } from '../models/BaseVoUserEntity';
import type { BaseVoVerificationVo } from '../models/BaseVoVerificationVo';
import type { BaseVoVoid } from '../models/BaseVoVoid';
import type { DepartmentCreateDto } from '../models/DepartmentCreateDto';
import type { DepartmentUpdateDto } from '../models/DepartmentUpdateDto';
import type { MenuCreateDto } from '../models/MenuCreateDto';
import type { MenuEntity } from '../models/MenuEntity';
import type { MenuUpdateDto } from '../models/MenuUpdateDto';
import type { MessageEnvelopeCreateDto } from '../models/MessageEnvelopeCreateDto';
import type { MessageEnvelopePageDto } from '../models/MessageEnvelopePageDto';
import type { MessageEnvelopeUpdateDto } from '../models/MessageEnvelopeUpdateDto';
import type { PermissionCreateDto } from '../models/PermissionCreateDto';
import type { PermissionUpdateDto } from '../models/PermissionUpdateDto';
import type { RequestLogPageDto } from '../models/RequestLogPageDto';
import type { RoleCreateDto } from '../models/RoleCreateDto';
import type { RoleUpdateDto } from '../models/RoleUpdateDto';
import type { UserCreateDto } from '../models/UserCreateDto';
import type { UserLoginDto } from '../models/UserLoginDto';
import type { UserPageDto } from '../models/UserPageDto';
import type { UserPasswordResetDto } from '../models/UserPasswordResetDto';
import type { UserPasswordUpdateDto } from '../models/UserPasswordUpdateDto';
import type { UserUpdateDto } from '../models/UserUpdateDto';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class Service {
    /**
     * 修改用户密码
     * @param requestBody
     * @returns BaseVoVoid OK
     * @throws ApiError
     */
    public static updateUserPasswordUserController(
        requestBody: UserPasswordUpdateDto,
    ): CancelablePromise<BaseVoVoid> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/user/update-password',
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
     * 重置用户密码
     * @param requestBody
     * @returns BaseVoVoid OK
     * @throws ApiError
     */
    public static resetUserPasswordUserController(
        requestBody: UserPasswordResetDto,
    ): CancelablePromise<BaseVoVoid> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/user/reset-password',
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
     * 分页查询用户
     * @param otherData
     * @param requestBody
     * @returns BaseVoPageVoUserEntity OK
     * @throws ApiError
     */
    public static findByPageUserController(
        otherData: boolean,
        requestBody: UserPageDto,
    ): CancelablePromise<BaseVoPageVoUserEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/page/{otherData}',
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
     * 用户登出
     * @returns BaseVoString OK
     * @throws ApiError
     */
    public static logoutUserController(): CancelablePromise<BaseVoString> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/logout',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 用户登录
     * @param requestBody
     * @returns BaseVoSaTokenInfo OK
     * @throws ApiError
     */
    public static loginUserController(
        requestBody: UserLoginDto,
    ): CancelablePromise<BaseVoSaTokenInfo> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/login',
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
     * 新增用户
     * @param requestBody
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static insertUserController(
        requestBody: UserCreateDto,
    ): CancelablePromise<BaseVoUserEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/insert',
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
     * 根据ID批量查询用户
     * @param otherData
     * @param requestBody
     * @returns BaseVoListUserEntity OK
     * @throws ApiError
     */
    public static findByIdsUserController(
        otherData: boolean,
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoListUserEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/get/{otherData}',
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
     * 删除用户
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static deleteUserController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/delete',
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
     * 更新用户
     * @param requestBody
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static applyUserController(
        requestBody: UserUpdateDto,
    ): CancelablePromise<BaseVoUserEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/apply',
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
     * 分页查询角色
     * @param otherData
     * @param requestBody
     * @returns BaseVoPageVoRoleEntity OK
     * @throws ApiError
     */
    public static findByPageRoleController(
        otherData: boolean,
        requestBody: BasePageDtoRoleEntity,
    ): CancelablePromise<BaseVoPageVoRoleEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/role/page/{otherData}',
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
     * 新增角色
     * @param requestBody
     * @returns BaseVoRoleEntity OK
     * @throws ApiError
     */
    public static insertRoleController(
        requestBody: RoleCreateDto,
    ): CancelablePromise<BaseVoRoleEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/role/insert',
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
            url: '/role/import',
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
     * 根据ID批量查询角色
     * @param otherData
     * @param requestBody
     * @returns BaseVoListRoleEntity OK
     * @throws ApiError
     */
    public static findByIdsRoleController(
        otherData: boolean,
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoListRoleEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/role/get/{otherData}',
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
     * 删除角色
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static deleteRoleController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/role/delete',
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
     * 更新角色
     * @param requestBody
     * @returns BaseVoRoleEntity OK
     * @throws ApiError
     */
    public static applyRoleController(
        requestBody: RoleUpdateDto,
    ): CancelablePromise<BaseVoRoleEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/role/apply',
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
     * 分页查询请求日志
     * @param otherData
     * @param requestBody
     * @returns BaseVoPageVoRequestLogEntity OK
     * @throws ApiError
     */
    public static findPageRequestLog(
        otherData: boolean,
        requestBody: RequestLogPageDto,
    ): CancelablePromise<BaseVoPageVoRequestLogEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/request-log/page/{otherData}',
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
     * 新增权限
     * @param requestBody
     * @returns BaseVoPermissionEntity OK
     * @throws ApiError
     */
    public static insertPermissionController(
        requestBody: PermissionCreateDto,
    ): CancelablePromise<BaseVoPermissionEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/permission/insert',
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
     * 根据ID批量查询权限
     * @param otherData
     * @param requestBody
     * @returns BaseVoListPermissionEntity OK
     * @throws ApiError
     */
    public static findByIdsPermissionController(
        otherData: boolean,
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoListPermissionEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/permission/get/{otherData}',
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
     * 删除权限
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static deletePermissionController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/permission/delete',
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
     * 更新权限
     * @param requestBody
     * @returns BaseVoPermissionEntity OK
     * @throws ApiError
     */
    public static applyPermissionController(
        requestBody: PermissionUpdateDto,
    ): CancelablePromise<BaseVoPermissionEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/permission/apply',
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
     * 批量已读消息
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static readMessageEnvelopeController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/message-envelope/read',
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
     * 分页查询消息
     * @param otherData
     * @param requestBody
     * @returns BaseVoPageVoMessageEnvelopeEntityObject OK
     * @throws ApiError
     */
    public static findByPageMessageEnvelopeController(
        otherData: boolean,
        requestBody: MessageEnvelopePageDto,
    ): CancelablePromise<BaseVoPageVoMessageEnvelopeEntityObject> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/message-envelope/page/{otherData}',
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
     * 新增消息
     * @param requestBody
     * @returns BaseVoMessageEnvelopeEntityObject OK
     * @throws ApiError
     */
    public static insertMessageEnvelopeController(
        requestBody: MessageEnvelopeCreateDto,
    ): CancelablePromise<BaseVoMessageEnvelopeEntityObject> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/message-envelope/insert',
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
     * 根据ID批量查询消息
     * @param otherData
     * @param requestBody
     * @returns BaseVoListMessageEnvelopeEntityObject OK
     * @throws ApiError
     */
    public static findByIdsMessageEnvelopeController(
        otherData: boolean,
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoListMessageEnvelopeEntityObject> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/message-envelope/get/{otherData}',
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
     * 删除消息
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static deleteMessageEnvelopeController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/message-envelope/delete',
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
     * 更新消息
     * @param requestBody
     * @returns BaseVoMessageEnvelopeEntityObject OK
     * @throws ApiError
     */
    public static applyMessageEnvelopeController(
        requestBody: MessageEnvelopeUpdateDto,
    ): CancelablePromise<BaseVoMessageEnvelopeEntityObject> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/message-envelope/apply',
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
     * 新增菜单
     * @param requestBody
     * @returns BaseVoMenuEntity OK
     * @throws ApiError
     */
    public static insertMenuController(
        requestBody: MenuCreateDto,
    ): CancelablePromise<BaseVoMenuEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/menu/insert',
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
     * 批量初始化系统的菜单信息
     * @param requestBody
     * @returns BaseVoVoid OK
     * @throws ApiError
     */
    public static initMenu(
        requestBody: MenuEntity,
    ): CancelablePromise<BaseVoVoid> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/menu/init',
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
     * 根据ID批量查询菜单
     * @param otherData
     * @param requestBody
     * @returns BaseVoListMenuEntity OK
     * @throws ApiError
     */
    public static findByIdsMenuController(
        otherData: boolean,
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoListMenuEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/menu/get/{otherData}',
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
     * 删除菜单
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static deleteMenuController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/menu/delete',
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
     * 更新菜单
     * @param requestBody
     * @returns BaseVoMenuEntity OK
     * @throws ApiError
     */
    public static applyMenuController(
        requestBody: MenuUpdateDto,
    ): CancelablePromise<BaseVoMenuEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/menu/apply',
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
     * 新增部门
     * @param requestBody
     * @returns BaseVoDepartmentEntity OK
     * @throws ApiError
     */
    public static insertDepartmentController(
        requestBody: DepartmentCreateDto,
    ): CancelablePromise<BaseVoDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/department/insert',
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
     * 根据ID批量查询部门
     * @param otherData
     * @param requestBody
     * @returns BaseVoListDepartmentEntity OK
     * @throws ApiError
     */
    public static findByIdsDepartmentController(
        otherData: boolean,
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoListDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/department/get/{otherData}',
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
     * 删除部门
     * @param requestBody
     * @returns BaseVoInteger OK
     * @throws ApiError
     */
    public static deleteDepartmentController(
        requestBody: Array<string>,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/department/delete',
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
     * 更新部门
     * @param requestBody
     * @returns BaseVoDepartmentEntity OK
     * @throws ApiError
     */
    public static applyDepartmentController(
        requestBody: DepartmentUpdateDto,
    ): CancelablePromise<BaseVoDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/department/apply',
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
     * 根据ID查询用户
     * @param id
     * @param otherData
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static findByIdUserController(
        id: string,
        otherData: boolean,
    ): CancelablePromise<BaseVoUserEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/{id}/{otherData}',
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
     * 获取用户信息
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static getInfoUserController(): CancelablePromise<BaseVoUserEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/info',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 获取验证码
     * @returns BaseVoVerificationVo OK
     * @throws ApiError
     */
    public static getVerificationCodeUserController(): CancelablePromise<BaseVoVerificationVo> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/get/verification/code',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 查询所有用户(支持设置otherData)
     * @param otherData
     * @returns BaseVoListUserEntity OK
     * @throws ApiError
     */
    public static findAllWithParamUserController(
        otherData: boolean = false,
    ): CancelablePromise<BaseVoListUserEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/all',
            query: {
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
     * 查询所有用户
     * @param otherData
     * @returns BaseVoListUserEntity OK
     * @throws ApiError
     */
    public static findAllUserController(
        otherData: boolean,
    ): CancelablePromise<BaseVoListUserEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/all/{otherData}',
            path: {
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
     * 查询所有在线用户
     * @param otherData
     * @returns BaseVoListUserEntity OK
     * @throws ApiError
     */
    public static findAllOnlineUserUserController(
        otherData: boolean,
    ): CancelablePromise<BaseVoListUserEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/all/online/{otherData}',
            path: {
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
     * 根据ID查询角色
     * @param id
     * @param otherData
     * @returns BaseVoRoleEntity OK
     * @throws ApiError
     */
    public static findByIdRoleController(
        id: string,
        otherData: boolean,
    ): CancelablePromise<BaseVoRoleEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/role/{id}/{otherData}',
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
     * 下载角色导入模板
     * @returns binary OK
     * @throws ApiError
     */
    public static downloadRoleImportTemplate(): CancelablePromise<Blob> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/role/template-download',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 查询所有角色(支持设置otherData)
     * @param otherData
     * @returns BaseVoListRoleEntity OK
     * @throws ApiError
     */
    public static findAllWithParamRoleController(
        otherData: boolean = false,
    ): CancelablePromise<BaseVoListRoleEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/role/all',
            query: {
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
     * 查询所有角色
     * @param otherData
     * @returns BaseVoListRoleEntity OK
     * @throws ApiError
     */
    public static findAllRoleController(
        otherData: boolean,
    ): CancelablePromise<BaseVoListRoleEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/role/all/{otherData}',
            path: {
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
     * 根据ID查询权限
     * @param id
     * @param otherData
     * @returns BaseVoPermissionEntity OK
     * @throws ApiError
     */
    public static findByIdPermissionController(
        id: string,
        otherData: boolean,
    ): CancelablePromise<BaseVoPermissionEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/permission/{id}/{otherData}',
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
     * 查询所有权限
     * @param otherData
     * @returns BaseVoListPermissionEntity OK
     * @throws ApiError
     */
    public static findAllPermissionController(
        otherData: boolean,
    ): CancelablePromise<BaseVoListPermissionEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/permission/all/{otherData}',
            path: {
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
     * 根据ID查询消息
     * @param id
     * @param otherData
     * @returns BaseVoMessageEnvelopeEntityObject OK
     * @throws ApiError
     */
    public static findByIdMessageEnvelopeController(
        id: string,
        otherData: boolean,
    ): CancelablePromise<BaseVoMessageEnvelopeEntityObject> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/message-envelope/{id}/{otherData}',
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
     * 根据ID查询菜单
     * @param id
     * @param otherData
     * @returns BaseVoMenuEntity OK
     * @throws ApiError
     */
    public static findByIdMenuController(
        id: string,
        otherData: boolean,
    ): CancelablePromise<BaseVoMenuEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/menu/{id}/{otherData}',
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
     * 查询可访问的所有路由
     * @returns BaseVoListMenuEntity OK
     * @throws ApiError
     */
    public static findAllMenuRoutes(): CancelablePromise<BaseVoListMenuEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/menu/routes',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 查询指定菜单组下的菜单
     * @param group
     * @returns BaseVoListMenuEntity OK
     * @throws ApiError
     */
    public static findByGroupMenuController(
        group: string,
    ): CancelablePromise<BaseVoListMenuEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/menu/get/menu-group/{group}',
            path: {
                'group': group,
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
     * 查询所有菜单
     * @param otherData
     * @returns BaseVoListMenuEntity OK
     * @throws ApiError
     */
    public static findAllMenuController(
        otherData: boolean,
    ): CancelablePromise<BaseVoListMenuEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/menu/all/{otherData}',
            path: {
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
     * 根据ID查询部门
     * @param id
     * @param otherData
     * @returns BaseVoDepartmentEntity OK
     * @throws ApiError
     */
    public static findByIdDepartmentController(
        id: string,
        otherData: boolean,
    ): CancelablePromise<BaseVoDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/department/{id}/{otherData}',
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
     * 查询默认部门
     * @param otherData
     * @returns BaseVoDepartmentEntity OK
     * @throws ApiError
     */
    public static findDefaultDepartmentController(
        otherData: boolean,
    ): CancelablePromise<BaseVoDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/department/get/default/{otherData}',
            path: {
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
     * 查询所有部门
     * @param otherData
     * @returns BaseVoListDepartmentEntity OK
     * @throws ApiError
     */
    public static findAllDepartmentController(
        otherData: boolean,
    ): CancelablePromise<BaseVoListDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/department/all/{otherData}',
            path: {
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
}
