<script lang='ts' setup>

import {onMounted, ref} from "vue";
import {MenuEntity, Service} from "~/generated/system";
import BaseDelete from "~/components/BaseDelete.vue";
import {ElTable as ElTableRefType} from "element-plus/es/components/table";
import {ElTable, ElTableColumn, ElTag} from "element-plus";
import BaseBatchDelete from "~/components/BaseBatchDelete.vue";

const tableData = ref<MenuEntity[]>([]);
const tableLoading = ref(true);

const getTable = () => {
  tableLoading.value = true;
  Service.findAllMenu().then((res) => {
    tableData.value = res.data as MenuEntity[];
  }).finally(() => {
    tableLoading.value = false;
  });
};

onMounted(() => {
  getTable();
});
const tableRef = ref<InstanceType<typeof ElTableRefType>>();
</script>

<template>
  <div class='main'>
    <div class="controllers">
      <base-batch-delete v-if="tableRef"
                         :api="Service.deleteMenu" :table="tableRef" @success='getTable'></base-batch-delete>
    </div>
    <div class='table'>
      <div class='table-container'>
        <el-table ref='tableRef' v-loading='tableLoading' :data='tableData' border default-expand-all height='100%'
                  row-key='id' stripe>
          <el-table-column align="center" type='selection' width='44'></el-table-column>
          <el-table-column label='菜单名称' prop='name' min-width="210">
          </el-table-column>
          <el-table-column align='center' label='菜单类型' min-width="90">
            <template #default='{ row }'>
              <el-tag v-if="row.menuType === MenuEntity.menuType.MENU">菜单</el-tag>
              <el-tag v-else-if="row.menuType === MenuEntity.menuType.BUTTON" type='success'>按钮</el-tag>
              <el-tag v-else-if="row.menuType === MenuEntity.menuType.PAGE" type='warning'>页面</el-tag>
            </template>
          </el-table-column>
          <el-table-column align='center' label='图标' min-width="90">
            <template #default='{ row }'>
              <component :is='row.icon' class="menu-icon"/>
            </template>
          </el-table-column>
          <el-table-column align='center' label='路由名' prop='routeName' min-width="180">
          </el-table-column>
          <el-table-column align='center' label='是否停用（置灰）' prop='isDisabled' min-width="150">
            <template #default='{ row }'>
              <el-tag :type="row.isDisabled?'danger':'success'">
                {{ row.isDisabled ? '停用' : '启用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align='center' label='是否隐藏（不显示）' prop='isHidden' min-width="170">
            <template #default='{ row }'>
              <el-tag :type="row.isHidden?'danger':'success'">
                {{ row.isHidden ? '隐藏' : '显示' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align='center' label='描述' prop='description'>
          </el-table-column>
          <el-table-column align='center' fixed="right" label='操作' width="90">
            <template #default='{ row }'>
              <base-delete :id="row.id" :api="Service.deleteMenu" @success="getTable"/>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<style lang='scss' scoped>
.menu-icon {
  width: 1.2em;
  height: 1.2em;
}
</style>
