<script lang="ts" setup>
import {DepartmentEntity, DepartmentUpdateDto, Service, VerificationResult} from "~/generated/system";
import {ref} from "vue";
import {useFormValidation} from "~/composables/FormValidationHook";
import {ElButton, ElCascader, ElDialog, ElForm, ElFormItem, ElInput, ElMessage} from "element-plus";
import {
  departmentFormTreeProps,
  departmentPageTreeProps
} from "~/views/admin/pages/system/department/department.form.tree.props";
import {buildDto} from "~/api/HttpRequest";
import UploadImage from "~/components/UploadImage.vue";

const props = defineProps<{ id: string }>();

const queryUpdateData = async () => {
  return Service.findDepartmentById(props.id).then((res) => {
    if (res.code === 200 && res.data) {
      data.value = {
        name: res.data?.name || '',
        id: res.data?.id as string,
        description: res.data?.description || '',
        code: res.data?.code || '',
        domain: res.data?.domain || '',
        address: res.data?.address || '',
        phone: res.data?.phone || '',
        html: res.data?.html || '',
        iconId: res.data?.iconId || '',
        parentId: res.data?.parentId || '',
      }
    }
  });
}

let defaultData: () => DepartmentUpdateDto = () => ({
  id: '',
  description: '',
  name: '',
  code: '',
  domain: '',
  address: '',
  phone: '',
  html: '',
  iconId: '',
  parentId: '',
})

const data = ref<DepartmentUpdateDto>(defaultData());

const {errData, validate, resetValidate} = useFormValidation();

const visible = ref(false);

const openLoading = ref(false);
const openDialog = () => {
  resetValidate();
  openLoading.value = true;
  Promise.all([queryUpdateData(), getDepartmentTree()]).then(() => {
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
  Service.updateDepartment(buildDto(defaultData(), data.value)).then((res) => {
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

const departmentTree = ref<DepartmentEntity[]>([]);
const getDepartmentTree = async () => {
  return Service.findAllDepartment().then((res) => {
    departmentTree.value = res.data as DepartmentEntity[];
  });
}
</script>

<template>
  <el-button :loading='openLoading' size="small" type='primary' @click='openDialog'>修改</el-button>
  <el-dialog v-model='visible' append-to-body draggable title='修改部门' width='700'>
    <el-form :model='data' label-width='100px'>
      <el-form-item :error="errData.parentId" label="上级部门">
        <el-cascader v-model="data.parentId" :options="departmentTree" :props="departmentFormTreeProps"
                     class="w-full" clearable placeholder="请选择上级部门">
        </el-cascader>
      </el-form-item>
      <el-form-item :error="errData.name" label="名称">
        <el-input v-model='data.name' placeholder='请输入名称'></el-input>
      </el-form-item>
      <el-form-item :error="errData.description" label="描述">
        <el-input v-model='data.description' placeholder='请输入描述'></el-input>
      </el-form-item>
      <el-form-item :error="errData.code" label="编码">
        <el-input v-model='data.code' placeholder='请输入编码'></el-input>
      </el-form-item>
      <el-form-item :error="errData.domain" label="域名">
        <el-input v-model='data.domain' placeholder='例 localhost:8080'></el-input>
      </el-form-item>
      <el-form-item :error="errData.address" label="地址">
        <el-input v-model='data.address' placeholder='请输入地址'></el-input>
      </el-form-item>
      <el-form-item :error="errData.phone" label="电话">
        <el-input v-model='data.phone' placeholder='请输入电话'></el-input>
      </el-form-item>
      <el-form-item :error="errData.html" label="html">
        <el-input v-model='data.html' placeholder='请输入html'></el-input>
      </el-form-item>
      <el-form-item :error="errData.iconId" label="图标">
        <upload-image v-model='data.iconId as string'></upload-image>
      </el-form-item>
      <el-form-item>
        <el-button :loading='submitLoading' type='primary' @click='submitForm'>提交</el-button>
        <el-button @click='visible = false'>取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>