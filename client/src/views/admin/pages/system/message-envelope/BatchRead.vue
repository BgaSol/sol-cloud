<script setup lang="ts">
import {ref} from 'vue';
import {ElButton, ElMessage} from 'element-plus';
import type {ElTable as ElTableRefType} from 'element-plus/es/components/table';
import {MessageEnvelopeUpdateDto, Service} from '~/generated/system';

const props = defineProps<{
  table: InstanceType<typeof ElTableRefType>;
}>();
const emit = defineEmits<{ success: [] }>();

const loading = ref(false);

const readAll = () => {
  loading.value = true;
  const ids = props.table.getSelectionRows().map((entity: any) => entity.id);
  Promise.all(ids.map((id: string) => requestReadAll(id)))
      .then(() => {
        ElMessage.success('标记已读成功');
        emit('success');
      })
      .catch((error) => {
        console.error('标记已读失败', error);
      })
      .finally(() => {
        loading.value = false;
      });
};
const requestReadAll = async (id: string) => {
  return Service.updateMessageEnvelope({
    id: id,
    status: MessageEnvelopeUpdateDto.status.READ,
  });
};
</script>

<template>
  <el-button type="primary" :loading="loading" @click="readAll">批量标记已读</el-button>
</template>
