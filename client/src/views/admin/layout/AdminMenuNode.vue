<script lang='ts' setup>
import {MenuEntity} from "~/generated/system";
import {useRouter} from "vue-router";
import {useMenu} from "~/pinia/modules/menu";
import {ElIcon, ElMenuItem, ElSubMenu} from "element-plus";

const props = defineProps<{ menu: MenuEntity }>();
const router = useRouter();
const menu = useMenu();
const selectMenu = () => {
  router.push({
    name: props.menu.routeName,
  });
  menu.setActiveMenu(<string>props.menu.routeName);
};
</script>

<template>
  <el-sub-menu v-if='props.menu.menuType===MenuEntity.menuType.MENU && props.menu.children?.length'
               :disabled='props.menu.isDisabled' :index='String(props.menu.id)'>
    <template #title>
      <el-icon>
        <component :is='String(props.menu.icon)'></component>
      </el-icon>
      <span>{{ props.menu.name }}</span>
    </template>
    <template v-for='menu in props.menu.children' :key='menu.id'>
      <admin-menu-node v-if='!menu.isHidden' :menu='menu'></admin-menu-node>
    </template>
  </el-sub-menu>
  <el-menu-item v-else-if='props.menu.menuType===MenuEntity.menuType.PAGE' :disabled='props.menu.isDisabled'
                :index='props.menu.routeName' @click='selectMenu'>
    <el-icon>
      <component :is='String(props.menu.icon)'></component>
    </el-icon>
    <span>{{ props.menu.name }}</span>
  </el-menu-item>
</template>
