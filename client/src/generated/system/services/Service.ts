/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseVoDepartmentEntity } from '../models/BaseVoDepartmentEntity';
import type { BaseVoInteger } from '../models/BaseVoInteger';
import type { BaseVoListDepartmentEntity } from '../models/BaseVoListDepartmentEntity';
import type { BaseVoListMenuEntity } from '../models/BaseVoListMenuEntity';
import type { BaseVoListPermissionEntity } from '../models/BaseVoListPermissionEntity';
import type { BaseVoListRoleEntity } from '../models/BaseVoListRoleEntity';
import type { BaseVoListUserEntity } from '../models/BaseVoListUserEntity';
import type { BaseVoMenuEntity } from '../models/BaseVoMenuEntity';
import type { BaseVoPageVoUserEntity } from '../models/BaseVoPageVoUserEntity';
import type { BaseVoPermissionEntity } from '../models/BaseVoPermissionEntity';
import type { BaseVoRoleEntity } from '../models/BaseVoRoleEntity';
import type { BaseVoSaTokenInfo } from '../models/BaseVoSaTokenInfo';
import type { BaseVoString } from '../models/BaseVoString';
import type { BaseVoUserEntity } from '../models/BaseVoUserEntity';
import type { BaseVoVerificationVo } from '../models/BaseVoVerificationVo';
import type { DepartmentCreateDto } from '../models/DepartmentCreateDto';
import type { DepartmentUpdateDto } from '../models/DepartmentUpdateDto';
import type { MenuCreateDto } from '../models/MenuCreateDto';
import type { MenuEntity } from '../models/MenuEntity';
import type { PermissionEntity } from '../models/PermissionEntity';
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
     * 更新用户
     * @param requestBody
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static updateUser(
        requestBody: UserUpdateDto,
    ): CancelablePromise<BaseVoUserEntity> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/user',
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
     * 保存用户
     * @param requestBody
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static saveUser(
        requestBody: UserCreateDto,
    ): CancelablePromise<BaseVoUserEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user',
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
     * 修改用户密码
     * @param requestBody
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static updateUserPassword(
        requestBody: UserPasswordUpdateDto,
    ): CancelablePromise<BaseVoUserEntity> {
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
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static resetUserPassword(
        requestBody: UserPasswordResetDto,
    ): CancelablePromise<BaseVoUserEntity> {
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
     * 查询所有角色
     * @returns BaseVoListRoleEntity OK
     * @throws ApiError
     */
    public static findAllRole(): CancelablePromise<BaseVoListRoleEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/role',
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
    public static updateRole(
        requestBody: RoleUpdateDto,
    ): CancelablePromise<BaseVoRoleEntity> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/role',
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
    public static saveRole(
        requestBody: RoleCreateDto,
    ): CancelablePromise<BaseVoRoleEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/role',
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
     * 查询所有部门
     * @returns BaseVoListDepartmentEntity OK
     * @throws ApiError
     */
    public static findAllDepartment(): CancelablePromise<BaseVoListDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/department',
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
    public static updateDepartment(
        requestBody: DepartmentUpdateDto,
    ): CancelablePromise<BaseVoDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/department',
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
    public static saveDepartment(
        requestBody: DepartmentCreateDto,
    ): CancelablePromise<BaseVoDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/department',
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
     * @param requestBody
     * @returns BaseVoPageVoUserEntity OK
     * @throws ApiError
     */
    public static findPageUser(
        requestBody: UserPageDto,
    ): CancelablePromise<BaseVoPageVoUserEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/page',
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
    public static logout(): CancelablePromise<BaseVoString> {
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
    public static login(
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
     * 批量初始化系统的权限信息
     * @param requestBody
     * @returns BaseVoPermissionEntity OK
     * @throws ApiError
     */
    public static initPermission(
        requestBody: PermissionEntity,
    ): CancelablePromise<BaseVoPermissionEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/permission/init',
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
     * 查询所有菜单
     * @returns BaseVoListMenuEntity OK
     * @throws ApiError
     */
    public static findAllMenu(): CancelablePromise<BaseVoListMenuEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/menu',
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
    public static createMenu(
        requestBody: MenuCreateDto,
    ): CancelablePromise<BaseVoMenuEntity> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/menu',
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
     * @returns BaseVoMenuEntity OK
     * @throws ApiError
     */
    public static initMenu(
        requestBody: MenuEntity,
    ): CancelablePromise<BaseVoMenuEntity> {
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
     * 根据id查询用户
     * @param id
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static findUserById(
        id: string,
    ): CancelablePromise<BaseVoUserEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/{id}',
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
     * 获取用户信息
     * @returns BaseVoUserEntity OK
     * @throws ApiError
     */
    public static getUserInfo(): CancelablePromise<BaseVoUserEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/user-info',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 根据id批量查询角色
     * @param ids
     * @returns BaseVoListUserEntity OK
     * @throws ApiError
     */
    public static findUserByIds(
        ids: string,
    ): CancelablePromise<BaseVoListUserEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/ids/{ids}',
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
     * 获取验证码
     * @returns BaseVoVerificationVo OK
     * @throws ApiError
     */
    public static getVerificationCode(): CancelablePromise<BaseVoVerificationVo> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/get-verification-code',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 获取我的部门
     * @returns BaseVoDepartmentEntity OK
     * @throws ApiError
     */
    public static getMyDepartment(): CancelablePromise<BaseVoDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/get-my',
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
     * @returns BaseVoListUserEntity OK
     * @throws ApiError
     */
    public static findAllOnlineUser(): CancelablePromise<BaseVoListUserEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/findAllOnlineUser',
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
     * @returns BaseVoRoleEntity OK
     * @throws ApiError
     */
    public static findRoleById(
        id: string,
    ): CancelablePromise<BaseVoRoleEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/role/{id}',
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
     * 根据id批量查询角色
     * @param ids
     * @returns BaseVoListRoleEntity OK
     * @throws ApiError
     */
    public static findRoleByIds(
        ids: string,
    ): CancelablePromise<BaseVoListRoleEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/role/ids/{ids}',
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
     * 查询所有权限
     * @returns BaseVoListPermissionEntity OK
     * @throws ApiError
     */
    public static findAllPermission(): CancelablePromise<BaseVoListPermissionEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/permission',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 根据id批量查询权限
     * @param ids
     * @returns BaseVoListPermissionEntity OK
     * @throws ApiError
     */
    public static findPermissionByIds(
        ids: string,
    ): CancelablePromise<BaseVoListPermissionEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/permission/ids/{ids}',
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
     * 根据id查询菜单
     * @param id
     * @returns BaseVoMenuEntity OK
     * @throws ApiError
     */
    public static findMenuById(
        id: string,
    ): CancelablePromise<BaseVoMenuEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/menu/{id}',
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
     * 根据id批量查询菜单
     * @param ids
     * @returns BaseVoListMenuEntity OK
     * @throws ApiError
     */
    public static findMenuByIds(
        ids: string,
    ): CancelablePromise<BaseVoListMenuEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/menu/ids/{ids}',
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
     * 查询指定菜单组下的菜单
     * @param group
     * @returns BaseVoListMenuEntity OK
     * @throws ApiError
     */
    public static findByMenuGroup(
        group: string,
    ): CancelablePromise<BaseVoListMenuEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/menu/find-by-menu-group/{group}',
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
     * 查询管理员菜单组
     * @returns BaseVoListMenuEntity OK
     * @throws ApiError
     */
    public static findAdminMenuGroup(): CancelablePromise<BaseVoListMenuEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/menu/find-admin-menu-group',
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
     * @returns BaseVoDepartmentEntity OK
     * @throws ApiError
     */
    public static findDepartmentById(
        id: string,
    ): CancelablePromise<BaseVoDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/department/{id}',
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
     * 根据id批量查询部门
     * @param ids
     * @returns BaseVoListDepartmentEntity OK
     * @throws ApiError
     */
    public static findDepartmentByIds(
        ids: string,
    ): CancelablePromise<BaseVoListDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/department/ids/{ids}',
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
     * 查询默认部门
     * @returns BaseVoDepartmentEntity OK
     * @throws ApiError
     */
    public static findDefaultDepartment(): CancelablePromise<BaseVoDepartmentEntity> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/department/find-default',
            errors: {
                400: `参数校验异常`,
                401: `未登录异常`,
                403: `无权限异常`,
                500: `业务异常`,
            },
        });
    }
    /**
     * 删除用户并强制退出登录
     * @param ids
     * @returns BaseVoInteger<any> OK
     * @throws ApiError
     */
    public static deleteUser(
        ids: string,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/user/{ids}',
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
     * 删除角色
     * @param ids
     * @returns BaseVoInteger<any> OK
     * @throws ApiError
     */
    public static deleteRole(
        ids: string,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/role/{ids}',
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
     * 删除权限
     * @param ids
     * @returns BaseVoInteger<any> OK
     * @throws ApiError
     */
    public static deletePermission(
        ids: string,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/permission/{ids}',
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
     * 删除菜单
     * @param ids
     * @returns BaseVoInteger<any> OK
     * @throws ApiError
     */
    public static deleteMenu(
        ids: string,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/menu/{ids}',
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
     * 删除部门
     * @param ids
     * @returns BaseVoInteger<any> OK
     * @throws ApiError
     */
    public static deleteDepartment(
        ids: string,
    ): CancelablePromise<BaseVoInteger> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/department/{ids}',
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
