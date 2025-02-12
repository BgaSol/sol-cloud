<script lang="ts" setup>
// 外部v-model绑定的值
import {BaseUrl, getHeaders, uploadFilePath} from "~/api/HttpRequest";
import {ref, watch} from "vue";
import {ElIcon, ElUpload} from "element-plus";

const props = withDefaults(defineProps<{
  modelValue: string;
  accept: string;
  tip: string;
  limit: number;
}>(), {
  accept: '*',
  tip: '只能上传jpg/png文件',
  limit: 1
});
const emit = defineEmits<{
  'update:modelValue': [string];
}>();
const uploadSuccess = async (response: any) => {
  if (response.code === 200) {
    emit('update:modelValue', response.data?.id as string);
  }
}
const fileList = ref([]);
watch(() => props.modelValue, (value) => {
  if (!value) {
    fileList.value = [];
  }
})
</script>

<template>
  <el-upload :action="uploadFilePath" :headers="getHeaders()" :on-success="uploadSuccess"
             :accept="props.accept" class="w-full" method="POST" name="uploadFile" drag
             :file-list="fileList" :limit="props.limit">
    <el-icon class="el-icon--upload">
      <upload-filled/>
    </el-icon>
    <div class="el-upload__text">
      将文件拖到此处或 <em>单击上传</em>
      <br>
      <em>{{ props.tip }}</em>
    </div>
  </el-upload>
</template>
