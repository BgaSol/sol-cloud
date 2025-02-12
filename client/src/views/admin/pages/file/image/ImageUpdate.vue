<script lang='ts' setup>
import {ref} from 'vue';
import {ElMessage} from 'element-plus';
import {ImageUpdateDto, Service, VerificationResult} from "~/generated/file";
import {useFormValidation} from "~/composables/FormValidationHook";
import {buildDto, getFileUrl, getHeaders, uploadFilePath} from "~/api/HttpRequest";

const props = defineProps<{ id: string }>();

const queryUpdateData = async () => {
  return Service.findImageById(props.id).then((res) => {
    if (res.code === 200 && res.data) {
      data.value = {
        name: res.data?.name || '',
        fileId: res.data?.file?.id || '',
        id: res.data?.id as string
      };
    }
  });
}

let defaultData: () => ImageUpdateDto = () => ({
  fileId: '',
  name: '',
  id: ''
})
const data = ref<ImageUpdateDto>(defaultData());
const {errData, validate, resetValidate} = useFormValidation();

const visible = ref(false);

const openLoading = ref(false);
const openDialog = () => {
  resetValidate();
  openLoading.value = true;
  Promise.all([queryUpdateData()]).then(() => {
    visible.value = true;
  }).finally(() => {
    openLoading.value = false;
  });
}

const emit = defineEmits<{ success: [] }>();
const submitLoading = ref(false);

const submitForm = () => {
  resetValidate();
  submitLoading.value = true;
  Service.updateImage(buildDto(defaultData(), data.value)).then((res) => {
    if (res.code === 400) {
      validate(res.data as unknown as VerificationResult[]);
    } else if (res.code === 200) {
      ElMessage.success(res.message)
      emit('success');
      visible.value = false;
    }
  }).finally(() => {
    submitLoading.value = false;
  });
}
const uploadSuccess = (response: any) => {
  if (response.code === 200) {
    data.value.fileId = response.data.id
  }
}
</script>

<template>
  <el-button :loading='openLoading' size="small" type='primary' @click='openDialog'>修改</el-button>
  <el-dialog v-model='visible' append-to-body draggable title='修改图片' width='700'>
    <el-form :model='data' label-width='100px'>
      <el-form-item :error="errData.name" label="图片名">
        <el-input v-model='data.name' placeholder='请输入图片名'></el-input>
      </el-form-item>
      <el-form-item :error="errData.fileId" :label='`图片`'>
        <el-upload :action="uploadFilePath"
                   :headers="getHeaders()"
                   :on-success="uploadSuccess" :show-file-list="false"
                   accept="image/*" class="dialog-form-img-upload w-full"
                   method="POST"
                   name="uploadFile">
          <template v-if="data.fileId">
            <img :src="getFileUrl(data.fileId)" alt="加载失败" class="up-img"/>
          </template>
          <el-icon v-else class="dialog-form-img-uploader-icon">
            <component is="Plus"></component>
          </el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button :loading='submitLoading' type='primary' @click='submitForm'>提交</el-button>
        <el-button @click='visible = false'>取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>