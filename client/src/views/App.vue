<script setup>
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import {useDark} from "@vueuse/core";
import {ElConfigProvider} from "element-plus";

const dark = useDark()

</script>
<template>
  <el-config-provider :locale="zhCn">
    <router-view v-slot="{ Component }">
      <transition enter-active-class="animate__animated animate__fadeIn">
        <!--                  leave-active-class="animate__animated animate__fadeOut">-->
        <component :is="Component" :class="{dark:dark}"></component>
      </transition>
    </router-view>
  </el-config-provider>
</template>