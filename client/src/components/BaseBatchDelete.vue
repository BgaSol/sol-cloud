<script lang="ts" setup>
import {ref} from 'vue';
import {ElTable as ElTableRefType} from 'element-plus/es/components/table';
import {ElButton, ElMessage, ElPopconfirm} from 'element-plus';

const props = defineProps<{ table: InstanceType<typeof ElTableRefType>, api: (ids: string) => Promise<any> }>();

const emit = defineEmits<{ success: [] }>();

const loading = ref(false);

const deleteByIds = () => {
  const ids = (props.table.getSelectionRows()).map((entity: any) => entity.id);
  if (ids.length) {
    loading.value = true;
    props.api(ids.join(",")).then(() => {
      ElMessage.success('删除成功');
      emit('success');
    }).finally(() => {
      loading.value = false;
    });
  } else {
    ElMessage.warning('请选择删除项');
  }
};

</script>

<template>
  <el-popconfirm :title='`确定删除所选项目吗`' cancel-button-text='取消'
                 confirm-button-text='删除'
                 confirm-button-type='danger' @confirm='deleteByIds'>
    <template #reference>
      <el-button :loading='loading' type='danger'>批量删除</el-button>
    </template>
  </el-popconfirm>
</template>