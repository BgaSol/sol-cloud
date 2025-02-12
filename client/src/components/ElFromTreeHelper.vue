<script lang="ts" setup>
import {computed, onMounted, ref} from "vue";
import {ElTree as ElTreeRefType} from "element-plus/es/components/tree";
import {TreeOptionProps} from "element-plus/es/components/tree/src/tree.type";
import {CheckboxValueType, ElCheckbox, ElFormItem, ElTree} from "element-plus";

const props = withDefaults(defineProps<{
  label: string
  error: string
  treeData: any[]
  props: TreeOptionProps;
  nodeKey: string;
  modelValue: string[];
  defaultExpandAll?: boolean;
}>(), {
  defaultExpandAll: true
});
const emit = defineEmits<{
  'update:modelValue': [string[]]
}>();

const modelValue = computed({
  get: () => {
    return props.modelValue;
  },
  set: (val: string[]) => {
    treeRef.value?.setCheckedKeys(val);
    emit('update:modelValue', val);
  }
});

onMounted(() => {
  treeRef.value?.setCheckedKeys(modelValue.value);
});

const treeRef = ref<InstanceType<typeof ElTreeRefType>>();

// 选中事件
const treeSelectChange = () => {
  modelValue.value = treeRef.value?.getCheckedKeys() as string[];
};
// 父子联动
const checkStrictly = ref(false);

// 全选/全不选
const selectAll = (checked: CheckboxValueType) => {
  modelValue.value = checked ? getTreeAllKeys(props.treeData, props.nodeKey) : [];
};

const getTreeAllKeys = (treeData: any[], nodeKey: string) => {
  const keys: string[] = [];
  treeData.forEach((item) => {
    keys.push(item[nodeKey]);
    if (item.children) {
      keys.push(...getTreeAllKeys(item.children, nodeKey));
    }
  });
  return keys;
};

const expandTree = (checked: CheckboxValueType) => {
  if (checked) {

  }
};
</script>

<template>
  <el-form-item :label='props.label' class="label-form">
    <el-checkbox v-model="checkStrictly">父子联动</el-checkbox>
    <el-checkbox @change="selectAll">全选/全不选</el-checkbox>
    <!--todo <el-checkbox @change="expandTree">展开/收回</el-checkbox>-->
  </el-form-item>
  <el-form-item :error='props.error'>
    <el-tree ref='treeRef'
             :check-strictly="!checkStrictly"
             :data='props.treeData'
             :default-expand-all="props.defaultExpandAll" :node-key='props.nodeKey'
             :props='props.props' class='form-item-tree'
             show-checkbox
             @check-change='treeSelectChange'>
    </el-tree>
  </el-form-item>
</template>