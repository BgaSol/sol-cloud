<script lang='ts' setup>
import {ref} from 'vue';
import {ElButton, ElCascader, ElDialog, ElForm, ElFormItem, ElInput, ElMessage, ElSwitch} from 'element-plus';
import {DepartmentEntity, RoleEntity, Service, UserCreateDto, VerificationResult} from "~/generated/system";
import {useFormValidation} from "~/composables/FormValidationHook";
import {userRoleTreeProps} from "~/views/admin/pages/system/user/user.form.tree.props";
import {buildDto} from "~/api/HttpRequest";
import {departmentFormTreeProps} from "~/views/admin/pages/system/department/department.form.tree.props";
import ElFromTreeHelper from "~/components/ElFromTreeHelper.vue";

const defaultData: () => UserCreateDto = () => ({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  description: '',
  locked: false,
  roleIds: [] as string[],
  departmentId: '',
})
const data = ref<UserCreateDto>(defaultData());
const {errData, validate, resetValidate} = useFormValidation();

const visible = ref(false);

const openLoading = ref(false);
const openDialog = () => {
  resetValidate();
  openLoading.value = true;
  Promise.all([getRoleList(), getDepartmentTree()]).then(() => {
    data.value = defaultData();
    visible.value = true;
  }).finally(() => {
    openLoading.value = false;
  });
}

const roleList = ref<RoleEntity[]>([])
const getRoleList = async () => {
  return Service.findAllRole().then((res) => {
    roleList.value = res.data as RoleEntity[]
  })
}
const emit = defineEmits<{ success: [] }>();
const submitLoading = ref(false);
const submitForm = () => {
  resetValidate();
  submitLoading.value = true;
  Service.saveUser(buildDto(defaultData(), data.value)).then((res) => {
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
  <el-button :loading='openLoading' type='primary' @click='openDialog'>添加用户</el-button>
  <el-dialog v-model='visible' append-to-body draggable title='添加用户' width='700'>
    <el-form :model='data' label-width='100px'>
      <el-form-item :error='errData.username' label='用户名' required>
        <el-input v-model='data.username' placeholder='请输入用户名'></el-input>
      </el-form-item>
      <el-form-item :error='errData.password' label='密码' required>
        <el-input v-model='data.password' placeholder='请输入密码'></el-input>
      </el-form-item>
      <el-form-item :error="errData.departmentId" label="部门">
        <el-cascader v-model="data.departmentId" :options="departmentTree" :props="departmentFormTreeProps"
                     class="w-full" clearable placeholder="请选择上级部门">
        </el-cascader>
      </el-form-item>
      <el-from-tree-helper v-if="data.roleIds && visible" v-model='data.roleIds as string[]' :error='errData.roleIds'
                           :props='userRoleTreeProps' :treeData='roleList'
                           label='角色' nodeKey='id'/>
      <el-form-item :error='errData.locked' label='账户锁定' required>
        <el-switch v-model='data.locked'></el-switch>
      </el-form-item>
      <el-form-item :error="errData.nickname" label='昵称'>
        <el-input v-model='data.nickname' placeholder='请输入昵称'></el-input>
      </el-form-item>
      <el-form-item :error="errData.email" label="邮箱">
        <el-input v-model='data.email' placeholder='请输入邮箱'></el-input>
      </el-form-item>
      <el-form-item :error="errData.phone" label="手机号">
        <el-input v-model='data.phone' placeholder='请输入手机号'></el-input>
      </el-form-item>
      <el-form-item :error="errData.description" label="描述">
        <el-input v-model='data.description' placeholder='请输入描述'></el-input>
      </el-form-item>
      <el-form-item>
        <el-button :loading='submitLoading' type='primary' @click='submitForm'>提交</el-button>
        <el-button @click='visible = false'>取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>