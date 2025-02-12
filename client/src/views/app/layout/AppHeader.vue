<script lang="ts" setup>
import {useUser} from "~/pinia/modules/user";
import {computed, nextTick, onMounted, ref, watch} from "vue";
import {useMenu} from "~/pinia/modules/menu";
import {useDark, useMediaQuery} from "@vueuse/core";
import UpdatePassword from "~/views/admin/pages/system/user/UpdatePassword.vue";
import {useRoute, useRouter} from "vue-router";
import {ElDrawer, ElIcon, ElImage, ElMenu, ElMenuItem, ElSubMenu} from "element-plus";

const user = useUser();

onMounted(() => {
  void user.getDepartment();
});

const menu = useMenu();

const login = () => {
  // 获取当前页面的url
  const url = window.location.href;
  window.location.href = `/login?redirect=${encodeURIComponent(url)}`;
}
const isBackend = computed(() => {
  if (user.user?.id === 'admin') {
    return true;
  }
  return user.user?.roles?.find(role => role.menus?.find(menu => menu.menuGroup === 'admin-master'))
})
const updatePasswordRef = ref<InstanceType<typeof UpdatePassword>>();

const route = useRoute();
const router = useRouter();

const activeMenu = computed(() => route.query.page as string || menu.activeMenu);

const isPc = useMediaQuery('(min-width: 768px)')
const drawer = ref(false)
</script>

<template>
  <el-menu :default-active="activeMenu" :ellipsis="false" mode="horizontal">
    <el-menu-item @click="router.push({name:'home'})">
      <el-image v-if="user.departmentLogoAndName.isShowLogo" :src="user.departmentLogoAndName.logoUrl"
                class="h-9 mt-auto mb-auto el-image-block el-image-w-auto" fit="contain"></el-image>
      <div v-if="user.departmentLogoAndName.isShowLogoName" class="title important-ml-4 font-bold font-size-6">
        {{ user.departmentLogoAndName.logoName }}
      </div>
    </el-menu-item>
    <template v-if="isPc">
      <div class="flex-grow"/>
      <el-menu-item v-if="isBackend" @click="router.replace({name:'admin_home'})">后台管理</el-menu-item>
      <el-sub-menu v-if="user.userToken" index="users">
        <template #title>{{ user.user?.username }}</template>
        <el-menu-item @click="updatePasswordRef?.openDialog">修改密码</el-menu-item>
        <update-password ref="updatePasswordRef"/>
        <el-menu-item @click="user.logout({name:'home'})">
          退出登录
        </el-menu-item>
      </el-sub-menu>
      <el-menu-item v-if="(route.name!=='login')&&!user.userToken" @click="login">
        登录
      </el-menu-item>
    </template>
    <template v-if="!isPc">
      <div class="flex-grow"/>
      <el-menu-item v-if="(route.name!=='login')" @click="drawer = true">
        <el-icon>
          <Grid/>
        </el-icon>
      </el-menu-item>
      <el-drawer v-model="drawer" size="50%" direction="ttb" :with-header="false">
        <el-menu :default-active="activeMenu" mode="vertical">
          <el-menu-item v-if="isBackend" @click="router.replace({name:'admin_home'})">后台管理</el-menu-item>
          <el-sub-menu v-if="user.userToken" index="users">
            <template #title>{{ user.user?.username }}</template>
            <el-menu-item @click="updatePasswordRef?.openDialog">修改密码</el-menu-item>
            <update-password ref="updatePasswordRef"/>
            <el-menu-item @click="user.logout({name:'home'})">
              退出登录
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-if="(route.name!=='login')&&!user.userToken" @click="login">
            登录
          </el-menu-item>
        </el-menu>
      </el-drawer>
    </template>
  </el-menu>
</template>