<script lang='ts' setup>
import {computed} from 'vue';
import {useIcons} from '~/pinia/modules/icons';
import {ElOption, ElSelect} from "element-plus";

const props = defineProps<{ modelValue: string, placeholder: string }>();
const emit = defineEmits<{ 'update:modelValue': [modelValue: string] }>();
const icons = useIcons();

const updateValue = (value: string) => {
  emit('update:modelValue', value);
};

const icon = computed({
  get: () => props.modelValue,
  set: (value) => updateValue(value),
});
</script>

<template class='icon-selector'>
  <el-select v-model='icon' :placeholder='props.placeholder' class='w-100 icon-selector'
             popper-class='icon-selector'>
    <el-option
        v-for='name in icons.icons'
        :key='name'
        :label='name'
        :value='name'>
      <component :is='name' class='icon-item'/>
    </el-option>
  </el-select>
</template>

<style lang='scss' scoped>
.icon-selector {
  //.el-select-dropdown__list {
  //  display: flex;
  //  flex-wrap: wrap;
  //  flex-direction: row;
  //  max-width: 500px;
  //}

  .icon-item {
    width: 1.3em;
    height: 1.3em;
  }
}
</style>
<style lang='scss'>
.icon-selector {
  .el-select-dropdown__list {
    display: flex;
    flex-wrap: wrap;
    flex-direction: row;
    max-width: 500px;
  }
}
</style>
