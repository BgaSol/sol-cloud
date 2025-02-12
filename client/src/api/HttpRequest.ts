import axios, {AxiosRequestHeaders} from "axios";
import {ElMessage} from "element-plus";

export const BaseUrl = '/api'
/**
 * 请求头的key
 */
export const authorization = "Authorization"
/**
 * 获取请求头对象
 */
export const getHeaders = () => {
    return {
        [authorization]: localStorage.getItem(authorization)
    }
}
/**
 * 上传文件的路径
 */
export const uploadFilePath = `${BaseUrl}/file/file`
/**
 * 获取请求头字符串
 */
export const getHeadersString = () => {
    return `${authorization}=${localStorage.getItem(authorization)}`
}
/**
 * 获取文件的url
 * @param id 文件id
 */
export const getFileUrl = (id: string) => {
    return `${BaseUrl}/file/file/download/${id}?${getHeadersString()}`
}
/**
 * 获取图片的url
 * @param id 图片id
 */
export const getImageUrl = (id: string) => {
    return `${BaseUrl}/file/image/download/${id}?${getHeadersString()}`
}

/**
 * 将 dto 中的 undefined 和 null 值去掉 替换为默认值 默认值一般为空字符串空数组等
 * 后端默认不更新 undefined 和 null 的值
 * @param defaultDto 默认的dto对象
 * @param dto 提交的dto对象
 */
export const buildDto = <DTO>(defaultDto: DTO, dto: DTO) => {
    const data = {...defaultDto}
    for (const key in dto) {
        if ((dto[key] !== undefined) && (dto[key] !== null)) {
            // 判断其是否是代理对象
            data[key] = dto[key]
        }
    }
    return data
}
/**
 * 响应拦截器
 */
axios.interceptors.response.use(
    response => {
        const data = response.data
        if (data.code === 500) {
            if (data.message) {
                ElMessage({
                    message: data.message,
                    type: data.type || "error"
                })
            }
            throw new Error(data.message);
        } else if (data.code === 401) {
            localStorage.removeItem(authorization);
            // 获取当前页面的url
            const url = window.location.href;
            // 跳转到登录页面
            window.location.href = `/login?redirect=${encodeURIComponent(url)}&error=401`;
        } else if (data.code === 403) {
            ElMessage.error('没有权限')
        } else if (data.code === 400) {
            ElMessage.warning('请求参数错误')
        }
        return response;
    },
    error => {
        ElMessage.error("服务器发生错误")
        return Promise.reject(error);
    }
);
/**
 * 请求拦截器
 */
axios.interceptors.request.use(
    config => {
        config.url = BaseUrl + urlFilter(config.url as string)
        // 处理请求头 添加token
        config.headers = {
            ...config.headers,
            ...getHeaders()
        } as unknown as AxiosRequestHeaders;
        return config;
    }
);

const urlFilter = (url: string) => {
    // 判断url中是否包含/api （因为API调用根据OPENAPI生成）
    const agreement = url.indexOf("://")
    if (agreement > 0) {
        url = url.substring(agreement + 3)
    }
    const host = url.indexOf("/")
    if (host > 0) {
        url = url.substring(host)
    }
    return url;
}