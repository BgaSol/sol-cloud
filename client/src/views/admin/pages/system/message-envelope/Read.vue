<script setup lang="ts">
import {ref} from 'vue';
import {ElButton, ElMessage} from 'element-plus';
import {MessageEnvelopeUpdateDto, Service} from '~/generated/system';

const props = defineProps<{
  id: string;
}>();
const emit = defineEmits<{ success: [] }>();

const loading = ref(false);

const read = () => {
  loading.value = true;
  Service.updateMessageEnvelope({
    id: props.id,
    status: MessageEnvelopeUpdateDto.status.READ,
  })
      .then(() => {
        emit('success');
        ElMessage.success('标记已读成功');
      })
      .finally(() => {
        loading.value = false;
      });
};
</script>

<template>
  <el-button type="primary" size="small" :loading="loading" @click="read">
    标记已读
  </el-button>
</template>
