<script setup lang="ts">
import {ref} from "vue";
import axios from "axios";
import {BaseUrl} from "~/api/HttpRequest";
import {useCssVar} from "@vueuse/core";
import {ElButton, ElMessage} from "element-plus";

const percent = ref(0);

const state = ref<"loading" | "success" | "error">("success");

const notificationVisible = ref(false);
const downloadFileById = async (id: string) => {
  state.value = "loading"
  notificationVisible.value = true
  percent.value = 0;
  axios.get(`${BaseUrl}/file/download/${id}`, {
    responseType: 'blob',
    // 获取文件下载进度
    onDownloadProgress: (progressEvent) => {
      let progressNum = (progressEvent.loaded / (progressEvent.total as number)) * 100
      if (progressNum >= 99) {
        progressNum = 99
      }
      percent.value = progressNum
    }
  }).then(res => {
    ElMessage.success("下载成功")
    percent.value = 100
    state.value = "success"
    const link = document.createElement('a')
    link.style.display = 'none'
    link.href = URL.createObjectURL(res.data)
    link.setAttribute('download', decodeURI(res.headers['content-disposition'].split('filename=')[1]))
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }).catch(() => {
    state.value = "error"
    ElMessage.error("下载失败")
  }).finally(() => {
    setTimeout(() => notificationVisible.value = false, 3000);
  })
}
const props = withDefaults(defineProps<{
  name: string
  id: string
}>(), {
  name: "下载"
});

const successColor = useCssVar("--el-color-success")
const errorColor = useCssVar("--el-color-danger")
</script>

<template>
  <el-button @click="downloadFileById(props.id)" :loading="state==='loading'">{{ props.name }}</el-button>
  <!--  <el-dialog class="downLoad-dialog important-mt-20px" v-model="notificationVisible" append-to-body :close-on-click-modal="false" width="200px"-->
  <!--             :modal="false" :show-close="false">-->
  <!--    <template #header>-->
  <!--      <div class="flex items-center">-->
  <!--        <template v-if="state==='loading'">-->
  <!--          <el-icon class="is-loading">-->
  <!--            <Loading/>-->
  <!--          </el-icon>-->
  <!--          <span class="ml-2">文件下载中</span>-->
  <!--        </template>-->
  <!--        <template v-else-if="state==='success'">-->
  <!--          <el-icon :color="success">-->
  <!--            <CircleCheck/>-->
  <!--          </el-icon>-->
  <!--          <span class="ml-2">文件下载完成</span>-->
  <!--        </template>-->
  <!--        <template v-else-if="state==='error'">-->
  <!--          <el-icon :color="error">-->
  <!--            <WarningFilled/>-->
  <!--          </el-icon>-->
  <!--          <span class="ml-2">文件下载失败</span>-->
  <!--        </template>-->
  <!--      </div>-->
  <!--    </template>-->
  <!--    <template>-->
  <!--      <el-progress :text-inside="true" :stroke-width="26" :percentage="percent"/>-->
  <!--    </template>-->
  <!--  </el-dialog>-->
</template>
