<script setup lang="ts">
import {useRouter} from "vue-router";
import {ElButton} from "element-plus";

const router = useRouter();
</script>
<template>
  <div class='page-403'>
    <div class='page-403__content'>
      <div class='page-403__content__title'>403</div>
      <div class='page-403__content__desc'>抱歉，你无权限访问此页面</div>
      <div class='page-403__content__action'>
        <el-button type='primary' @click='router.back()'>返回上一页</el-button>
        <el-button type='success' @click='router.replace({name:"home"})'>返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<style lang='scss' scoped>
.page-403 {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  &__content {
    text-align: center;

    &__title {
      font-size: 72px;
      font-weight: 600;
      color: var(--el-color-primary);
    }

    &__desc {
      font-size: 24px;
      color: var(--el-color-primary);
    }

    &__action {
      margin-top: 24px;
    }
  }
}
</style>