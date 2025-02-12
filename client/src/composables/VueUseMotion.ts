import {MotionPlugin} from "@vueuse/motion";
import {App} from "vue";

export const initVueUseMotion = (app: App) => {
    app.use(MotionPlugin)
}