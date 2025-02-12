import 'virtual:uno.css'
import 'animate.css';
import '~/styles/index.scss';

import {createApp} from 'vue';
import "./api/HttpRequest";
import App from './views/App.vue';
import pinia from '~/pinia';
import ElementPlusIcons from '~/composables/ElementPlusIcons';
import {initVueUseMotion} from "~/composables/VueUseMotion";
import IconParkIconsPlugin from "~/composables/IconParkIcons";
import {initRouter} from "~/router";

const app = createApp(App);
app.use(pinia);
app.use(await initRouter());

app.use(ElementPlusIcons);
app.use(IconParkIconsPlugin)

initVueUseMotion(app)

app.mount('#app');
