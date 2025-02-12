import {defineStore} from "pinia";
import {Router, useRouter} from "vue-router";
import {MenuEntity, Service} from "~/generated/system";

export const useRoutes = defineStore('routes', () => {
    const getRoutes = async (router?: Router) => {
        if (!router) {
            router = useRouter()
        }
        if (router) {
            const {data: menus} = await Service.findAllMenuRoutes()
            menus?.forEach(menu => {
                if (menu.menuGroup === "admin-master" &&
                    menu.menuType === MenuEntity.menuType.PAGE &&
                    !router.hasRoute(menu.routeName as string)) {
                    const path = menu.path?.replace("/admin/", "") as string
                    const pathLength = path.split('/');
                    let component = () => import(`../../views/admin/pages/${path}/index.vue`)
                    if (pathLength.length === 1) {
                        component = () => import(`../../views/admin/pages/${path}/index.vue`)
                    } else if (pathLength.length === 2) {
                        component = () => import(`../../views/admin/pages/${pathLength[0]}/${pathLength[1]}/index.vue`)
                    } else if (pathLength.length === 3) {
                        component = () => import(`../../views/admin/pages/${pathLength[0]}/${pathLength[1]}/${pathLength[2]}/index.vue`)
                    } else if (pathLength.length === 4) {
                        component = () => import(`../../views/admin/pages/${pathLength[0]}/${pathLength[1]}/${pathLength[2]}/${pathLength[3]}/index.vue`)
                    } else if (pathLength.length === 5) {
                        component = () => import(`../../views/admin/pages/${pathLength[0]}/${pathLength[1]}/${pathLength[2]}/${pathLength[3]}/${pathLength[4]}/index.vue`)
                    }
                    router.addRoute("admin", {
                        path: menu.path as string,
                        name: menu.routeName,
                        components: {
                            default: component,
                            menu: () => import('~/views/admin/layout/AdminMenu.vue'),
                            header: () => import('~/views/admin/layout/AdminHeader.vue'),
                        },
                    })
                }
            })
        }
    }
    return {getRoutes}
})
