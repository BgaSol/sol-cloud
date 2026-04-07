<script lang="ts" setup>
import {ref} from "vue";
import {MenuCreateDto, MenuEntity, Service, VerificationResult} from "~/generated/system";
import {useFormValidation} from "~/composables/FormValidationHook";
import {
  ElButton,
  ElCascader,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElOption,
  ElSelect,
  ElSwitch
} from "element-plus";
import {menuFormTreeProps} from "~/views/admin/pages/system/menu/menu.form.tree.props";
import {buildDto} from "~/api/HttpRequest";
import IconSelector from "~/components/IconSelector.vue";

const defaultData: () => MenuCreateDto = () => ({
  sort: 0,
  description: '',
  parentId: '',
  name: '',
  menuType: MenuEntity.menuType.MENU,
  path: '',
  icon: '',
  routeName: '',
  buttonCode: '',
  isExternal: false,
  externalUrl: '',
  isExternalOpen: false,
  isDisabled: false,
  isHidden: false,
  menuGroup: '',
})

const data = ref<MenuCreateDto>(defaultData());

const {errData, validate, resetValidate} = useFormValidation();

const visible = ref(false);

const openLoading = ref(false);
const openDialog = () => {
  resetValidate();
  openLoading.value = true;
  Promise.all([getMenuTree()]).then(() => {
    data.value = defaultData();
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
  Service.insertMenuController(buildDto(defaultData(), data.value) as MenuCreateDto).then((res) => {
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

const menuTree = ref<MenuEntity[]>([]);
const getMenuTree = async () => {
  return Service.findAllMenuController(false).then((res) => {
    menuTree.value = res.data as MenuEntity[];
  });
}
</script>

<template>
  <el-button :loading='openLoading' type='primary' @click='openDialog'>新增菜单</el-button>
  <el-dialog v-model='visible' append-to-body draggable title='新增菜单' width='700'>
    <el-form :model='data' label-width='120px'>
      <el-form-item :error="errData.parentId" label="上级菜单">
        <el-cascader v-model="data.parentId" :options="menuTree" :props="menuFormTreeProps"
                     class="w-full" clearable placeholder="请选择上级菜单">
        </el-cascader>
      </el-form-item>
      <el-form-item :error="errData.name" label="菜单名称">
        <el-input v-model='data.name' placeholder='请输入菜单名称'></el-input>
      </el-form-item>
      <el-form-item :error="errData.menuType" label="菜单类型">
        <el-select v-model="data.menuType" class="w-full" placeholder="请选择菜单类型">
          <el-option :value="MenuEntity.menuType.MENU" label="菜单"></el-option>
          <el-option :value="MenuEntity.menuType.PAGE" label="页面"></el-option>
          <el-option :value="MenuEntity.menuType.BUTTON" label="按钮"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :error="errData.path" label="路由地址">
        <el-input v-model='data.path' placeholder='请输入路由地址'></el-input>
      </el-form-item>
      <el-form-item :error="errData.routeName" label="路由名">
        <el-input v-model='data.routeName' placeholder='请输入路由名'></el-input>
      </el-form-item>
      <el-form-item :error="errData.buttonCode" label="按钮代码">
        <el-input v-model='data.buttonCode' placeholder='请输入按钮代码'></el-input>
      </el-form-item>
      <el-form-item :error="errData.icon" label="图标">
        <icon-selector v-model="data.icon as string" placeholder="请选择图标"></icon-selector>
      </el-form-item>
      <el-form-item :error="errData.menuGroup" label="菜单组">
        <el-input v-model='data.menuGroup' placeholder='请输入菜单组'></el-input>
      </el-form-item>
      <el-form-item :error="errData.sort" label="排序">
        <el-input-number v-model='data.sort' :min="0" class="w-full"></el-input-number>
      </el-form-item>
      <el-form-item :error="errData.isExternal" label="是否外链">
        <el-switch v-model='data.isExternal'></el-switch>
      </el-form-item>
      <el-form-item :error="errData.externalUrl" label="外链地址">
        <el-input v-model='data.externalUrl' placeholder='请输入外链地址'></el-input>
      </el-form-item>
      <el-form-item :error="errData.isExternalOpen" label="外链新窗口打开">
        <el-switch v-model='data.isExternalOpen'></el-switch>
      </el-form-item>
      <el-form-item :error="errData.isDisabled" label="是否停用">
        <el-switch v-model='data.isDisabled'></el-switch>
      </el-form-item>
      <el-form-item :error="errData.isHidden" label="是否隐藏">
        <el-switch v-model='data.isHidden'></el-switch>
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
