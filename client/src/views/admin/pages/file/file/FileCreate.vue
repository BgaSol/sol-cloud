<script lang="ts" setup>
import { ref } from 'vue';
import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElUpload,
} from 'element-plus';
import { FileCreateDto, Service } from '~/generated/file';
import { useFormValidation } from '~/composables/FormValidationHook';
import { getHeaders, uploadFilePath } from '~/api/HttpRequest';
import type { UploadInstance } from 'element-plus';

const defaultData: () => FileCreateDto = () => ({
  uploadFile: new Blob(),
  description: '',
});
const data = ref<FileCreateDto>(defaultData());

const uploadRef = ref<UploadInstance>();

const visible = ref(false);

const openDialog = () => {
  visible.value = true;
};

const emit = defineEmits<{ success: [] }>();
const submitLoading = ref(false);
const submitForm = () => {
  submitLoading.value = true;
  uploadRef.value!.submit();
};
const uploadSuccess = () => {
  submitLoading.value = false;
  visible.value = false;
  emit('success');
};
</script>

<template>
  <el-button type="primary" @click="openDialog"
    >添加文件</el-button
  >
  <el-dialog
    v-model="visible"
    append-to-body
    draggable
    title="添加文件"
    width="700"
  >
    <el-form :model="data" label-width="100px">
      <el-form-item label="文件描述">
        <el-input
          v-model="data.description"
          placeholder="请输入文件描述"
        ></el-input>
      </el-form-item>
      <el-form-item :label="`文件`">
        <el-upload
          :action="uploadFilePath"
          :headers="getHeaders()"
          :on-success="uploadSuccess"
          :show-file-list="true"
          :auto-upload="false"
          class="dialog-form-img-upload w-full"
          method="POST"
          name="uploadFile"
          ref="uploadRef"
        >
          <el-icon class="dialog-form-img-uploader-icon">
            <component is="Plus"></component>
          </el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button :loading="submitLoading" type="primary" @click="submitForm"
          >提交</el-button
        >
        <el-button @click="visible = false">取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>
