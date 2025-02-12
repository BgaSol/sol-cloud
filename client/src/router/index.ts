import 'nprogress/nprogress.css';
import {createRouter, createWebHistory} from 'vue-router';
import {useNProgress} from '@vueuse/integrations/useNProgress';
import routes from '~/router/routes';
import {useMenu} from "~/pinia/modules/menu";
import {useRoutes} from "~/pinia/modules/routes";

export const initRouter = async () => {
    const router = createRouter({
        history: createWebHistory(),
        routes,
    });
    const nProgress = useNProgress();

    router.beforeEach((to) => {
        const menu = useMenu();
        nProgress.start();
        menu.setActiveMenu(to.name as string);
    });

    router.afterEach((to, from, failure) => {
        nProgress.done();
    });

    const routesPinia = useRoutes();

    await routesPinia.getRoutes(router);

    return router;
}
