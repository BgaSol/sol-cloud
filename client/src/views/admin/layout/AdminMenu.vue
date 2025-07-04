<script lang='ts' setup>

import {useMenu} from "~/pinia/modules/menu";
import AdminMenuNode from "~/views/admin/layout/AdminMenuNode.vue";
import {ElMenu} from "element-plus";

const menu = useMenu();
menu.getMenuList();
</script>

<template>
  <el-menu :collapse='menu.collapse' :default-active='String(menu.activeMenu)' class="min-h-full" mode='vertical'>
    <template v-for='menuItem in menu.menus' :key='menu.id'>
      <admin-menu-node v-if='!menuItem.isHidden' :menu='menuItem'></admin-menu-node>
    </template>
  </el-menu>
</template>