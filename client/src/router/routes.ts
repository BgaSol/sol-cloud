import {RouteRecordRaw} from 'vue-router';

const routes: Readonly<RouteRecordRaw[]> = [
    {
        path: '/',
        name: 'home',
        redirect: {
            name: "admin_home" // 这里可以随意更改
        }
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('~/views/pages/Login.vue'),
    },
    {
        path: '/admin',
        name: 'admin',
        component: () => import('~/views/admin/pages/index.vue'),
        children: [
            {
                path: '',
                name: 'admin_home',
                components: {
                    default: () => import('~/views/admin/layout/AdminHome.vue'),
                    menu: () => import('~/views/admin/layout/AdminMenu.vue'),
                    header: () => import('~/views/admin/layout/AdminHeader.vue'),
                }
            },
        ]
    },
    {
        path: '/403',
        name: '403',
        component: () => import('~/views/pages/NoPermission.vue'),
    },
    {
        path: '/500',
        name: '500',
        component: () => import('~/views/pages/Error.vue'),
    },
    {
        path: '/404',
        name: '404',
        component: () => import('~/views/pages/NotFound.vue'),
    },
    {
        path: '/:catchAll(.*)*',
        redirect: {
            name: '404',
        },
    },
];
export default routes;