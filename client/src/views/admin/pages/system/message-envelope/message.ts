export const BusinessTypes = {
    REQUEST_LOG: '重要请求日志'
} as const
export type BusinessTypeKey = keyof typeof BusinessTypes;
