import {defineStore} from "pinia";
import {ref} from "vue";
import {MenuEntity, Service} from "~/generated/system";
import {useRoutes} from "~/pinia/modules/routes";

export const useMenu = defineStore('menu', () => {
    const activeMenu = ref<string>('');
    const routes = useRoutes();
    const setActiveMenu = (menu: string) => {
        activeMenu.value = menu;
    };
    const menus = ref<MenuEntity[]>([]);
    const getMenuList = async () => {
        await routes.getRoutes();
        const menuList = await Service.findAdminMenuGroup()
        menus.value = <MenuEntity[]>menuList.data;
    }

    const collapse = ref<boolean>(false);
    return {activeMenu, setActiveMenu, menus, getMenuList, collapse};
})

