import {defineStore} from 'pinia';
import {ref} from 'vue';

export const useIcons = defineStore('icons', () => {

    const icons = ref<string[]>([]);
    const setIcons = (iconsData: string[]) => {
        icons.value = iconsData;
    };
    const addIcon = (...icon: string[]) => {
        icons.value.push(...icon);
    }
    return {icons, setIcons, addIcon};
});