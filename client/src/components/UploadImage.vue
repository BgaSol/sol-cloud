<script lang="ts" setup>
// 外部v-model绑定的值
import {Service} from "~/generated/file";
import {BaseUrl, getHeaders, getImageUrl, uploadFilePath} from "~/api/HttpRequest";
import {ElIcon, ElUpload} from "element-plus";

const props = defineProps<{
  modelValue: string;
}>();
const emit = defineEmits<{
  'update:modelValue': [string];
}>();
const uploadSuccess = async (response: any) => {
  if (response.code === 200) {
    return Service.saveImage({
      fileId: response.data.id,
      name: response.data.name,
    }).then((res) => {
      if (res.code === 200) {
        emit('update:modelValue', res.data?.id as string);
      }
    })
  }
}
</script>

<template>
  <el-upload :action="uploadFilePath"
             :headers="getHeaders()"
             :on-success="uploadSuccess"
             :show-file-list="false"
             accept="image/*"
             class="dialog-form-img-upload w-full"
             method="POST"
             name="uploadFile">
    <template v-if="props.modelValue">
      <img :src="getImageUrl(props.modelValue)" alt="加载失败" class="up-img"/>
    </template>
    <el-icon v-else class="dialog-form-img-uploader-icon">
      <component is="Plus"></component>
    </el-icon>
  </el-upload>
</template>
