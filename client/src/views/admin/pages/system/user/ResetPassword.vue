<script lang='ts' setup>
import {Service, UserPasswordResetDto, VerificationResult} from "~/generated/system";
import {ref} from "vue";
import {useFormValidation} from "~/composables/FormValidationHook";
import {ElButton, ElDialog, ElForm, ElFormItem, ElInput, ElMessage} from "element-plus";
import {buildDto} from "~/api/HttpRequest";

const props = defineProps<{ id: string }>();
const queryUpdateData = async () => {
  return Service.findUserById(props.id).then((res) => {
    if (res.code === 200 && res.data) {
      data.value = {
        password: '',
        id: res.data?.id as string
      }
    }
  });
}
let defaultData: () => UserPasswordResetDto = () => ({
  password: '',
  id: ''
})
const data = ref<UserPasswordResetDto>(defaultData());
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
  Service.resetUserPassword(buildDto(defaultData(), data.value)).then((res) => {
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
</script>

<template>
  <el-button :loading='openLoading' size='small' type='warning' @click='openDialog'>
    密码
  </el-button>
  <el-dialog v-model='visible' append-to-body draggable title='重置密码' width='700'>
    <el-form :model='data' label-width='100px'>
      <el-form-item :error="errData.password" label='密码' required>
        <el-input v-model='data.password' placeholder='请输入新密码'></el-input>
      </el-form-item>
      <el-form-item>
        <el-button :loading='submitLoading' type='primary' @click='submitForm'>提交</el-button>
        <el-button @click='visible = false'>取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>