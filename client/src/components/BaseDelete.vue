<script lang='ts' setup>
import {ref} from 'vue';
import {ElButton, ElMessage, ElPopconfirm} from 'element-plus';

const props = defineProps<{ id: string; api: (id: string) => Promise<any> }>();

const emit = defineEmits<{ success: [] }>();

const loading = ref(false);

const deleteById = () => {
  loading.value = true;
  props.api(props.id).then(() => {
    ElMessage.success('删除成功');
    emit('success');
  }).finally(() => {
    loading.value = false;
  });
};
</script>

<template>
  <el-popconfirm cancel-button-text='取消' confirm-button-text='删除'
                 confirm-button-type='danger'
                 title='确定删除吗' @confirm='deleteById'>
    <template #reference>
      <el-button :loading='loading' size='small' type='danger'>删除</el-button>
    </template>
  </el-popconfirm>
</template>
