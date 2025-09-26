<script setup lang="ts">
import { ref } from 'vue';
import { ElButton, ElMessage } from 'element-plus';
import { getFileUrl } from '~/api/HttpRequest';

const props = defineProps<{ fileId: string; fileName: string }>();

const downloadLoading = ref(false);

const download = () => {
  downloadLoading.value = true;
  try {
    const fileUrl = getFileUrl(props.fileId);
    const a = document.createElement('a');
    a.href = fileUrl;
    a.download = props.fileName;
    document.body.appendChild(a);
    a.click();

    window.URL.revokeObjectURL(fileUrl);
    document.body.removeChild(a);
  } catch (error) {
    ElMessage.warning('下载失败!');
  } finally {
    downloadLoading.value = false;
  }
};
</script>

<template>
  <el-button
    size="small"
    type="primary"
    @click="download"
    :loading="downloadLoading"
    >下载</el-button
  >
</template>
