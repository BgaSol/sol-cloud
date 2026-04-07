<script setup lang="ts">
import {ref} from 'vue';
import {ElButton, ElMessage} from 'element-plus';
import type {ElTable as ElTableRefType} from 'element-plus/es/components/table';
import {Service} from '~/generated/system';

const props = defineProps<{
  table: InstanceType<typeof ElTableRefType>;
}>();
const emit = defineEmits<{ success: [] }>();

const loading = ref(false);

const readAll = () => {
  const ids = props.table.getSelectionRows().map((entity: any) => entity.id);
  if (ids.length) {
    loading.value = true;
    Service.readMessageEnvelopeController([...ids]).then((res) => {
      ElMessage.success("标记已读" + res.data + "条数据");
      emit('success');
    }).catch((error) => {
      console.error('标记已读失败', error);
    }).finally(() => {
      loading.value = false;
    });
  }
};
</script>

<template>
  <el-button type="primary" :loading="loading" @click="readAll">批量标记已读</el-button>
</template>
