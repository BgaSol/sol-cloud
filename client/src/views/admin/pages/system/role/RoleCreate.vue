<script lang="ts" setup>
import {ref} from "vue";
import {RoleCreateDto, MenuEntity, PermissionEntity, Service, VerificationResult} from "~/generated/system";
import {useFormValidation} from "~/composables/FormValidationHook";
import {roleMenuTreeProps, rolePermissionTreeProps} from "~/views/admin/pages/system/role/role.form.tree.props";
import {ElButton, ElDialog, ElForm, ElFormItem, ElInput, ElMessage} from "element-plus";
import {buildDto} from "~/api/HttpRequest";
import ElFromTreeHelper from "~/components/ElFromTreeHelper.vue";

const defaultData: () => RoleCreateDto = () => ({
  code: "",
  name: '',
  description: '',
  permissionIds: [] as string[],
  menuIds: [] as string[]
});
const data = ref<RoleCreateDto>(defaultData());
const {errData, validate, resetValidate} = useFormValidation();

const visible = ref(false);

const openLoading = ref(false);
const openDialog = () => {
  resetValidate();
  openLoading.value = true;
  Promise.all([getPermissionList(), getMenuList()]).then(() => {
    data.value = defaultData();
    visible.value = true;
  }).finally(() => {
    openLoading.value = false;
  });
}

// 获取权限
const permissionList = ref<PermissionEntity[]>([]);
const getPermissionList = async () => {
  return Service.findAllPermission().then((res) => {
    permissionList.value = res.data as PermissionEntity[];
  });
}

// 获取菜单
const menuList = ref<MenuEntity[]>([]);
const getMenuList = async () => {
  return Service.findAllMenu().then((res) => {
    menuList.value = res.data as MenuEntity[];
  });
}

const emit = defineEmits<{ success: [] }>();
const submitLoading = ref(false);
const submitForm = () => {
  resetValidate();
  submitLoading.value = true;
  Service.saveRole(buildDto(defaultData(), data.value)).then((res) => {
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
  <el-button :loading='openLoading' type='primary' @click='openDialog'>添加角色</el-button>
  <el-dialog v-model='visible' append-to-body draggable title='添加角色' width='700'>
    <el-form :model='data' label-width='100px'>
      <el-form-item :error="errData.name" label='角色名称' required>
        <el-input v-model='data.name' placeholder='请输入角色名称'></el-input>
      </el-form-item>
      <el-form-item :error='errData.description' label='角色描述'>
        <el-input v-model='data.description' placeholder='请输入角色描述'></el-input>
      </el-form-item>
      <el-form-item :error='errData.code' label='角色编码' required>
        <el-input v-model='data.code' placeholder='请输入角色编码'></el-input>
      </el-form-item>
      <el-from-tree-helper v-if="data.permissionIds && visible"
                           v-model='data.permissionIds as string[]' :error='errData.permissionIds'
                           :props='rolePermissionTreeProps' :treeData='permissionList'
                           label='接口权限' nodeKey='id'/>
      <el-from-tree-helper v-if="data.menuIds && visible"
                           v-model='data.menuIds as string[]' :error='errData.menuIds'
                           :props='roleMenuTreeProps' :treeData='menuList'
                           label='菜单权限' nodeKey='id'/>
      <el-form-item>
        <el-button :loading='submitLoading' type='primary' @click='submitForm'>提交</el-button>
        <el-button @click='visible = false'>取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>