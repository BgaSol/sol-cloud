import path from 'path';
import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';
import ElementPlus from 'unplugin-element-plus/vite'

import UnoCss from 'unocss/vite';

import {ViteImageOptimizer} from "vite-plugin-image-optimizer";
import dynamicImportVars from '@rollup/plugin-dynamic-import-vars';

import * as OpenAPI from "openapi-typescript-codegen";
import {HttpClient} from "openapi-typescript-codegen";

const pathSrc = path.resolve(__dirname, 'src');

export default defineConfig({
    resolve: {
        alias: {
            '~/': `${pathSrc}/`,
        },
    },
    css: {
        preprocessorOptions: {
            scss: {
                api: 'modern-compiler',
                additionalData: `@use "~/styles/element/index.scss" as *;`,
            },
        },
    },
    build: {
        rollupOptions: {
            plugins: [
                dynamicImportVars(),
            ]
        },
        target: ['esnext']
    },
    plugins: [
        vue(),
        ElementPlus({
            useSource: true,
        }),
        UnoCss(),
        ViteImageOptimizer({
            png: {
                quality: 100,
            },
            jpg: {
                quality: 100,
            },
            jpeg: {
                quality: 100,
            }
        }),
    ],
    server: {
        port: 3000,
        host: true,
        proxy: {
            '/api': {
                target: 'http://localhost:9527',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, ''),
            },
        },
    },
})
if (process.env.NODE_ENV !== 'production') {
    setTimeout(() => {
        const initAxiosRequest = async (serverName: string) => {
            await OpenAPI.generate({
                input: `http://localhost:9527/${serverName}/api-docs`,
                output: `./src/generated/${serverName}`,
                httpClient: HttpClient.AXIOS,
            }).then(() => console.log(`Generated OpenAPI ${serverName}`)).catch((e) => console.error(e))
        }
        void Promise.all([
            initAxiosRequest('file'),
            initAxiosRequest('system'),
        ])
    }, 1000 * 3);
}
