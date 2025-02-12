<script lang='ts' setup>

import {onMounted, ref} from "vue";
import {DepartmentEntity, Service} from "~/generated/system";
import BaseDelete from "~/components/BaseDelete.vue";
import {getImageUrl} from "~/api/HttpRequest";
import CreateDepartment from "~/views/admin/pages/system/department/DepartmentCreate.vue";
import UpdateDepartment from "~/views/admin/pages/system/department/DepartmentUpdate.vue";
import {ElTable as ElTableRefType} from "element-plus/es/components/table";
import {ElTable, ElTableColumn} from "element-plus";
import BaseBatchDelete from "~/components/BaseBatchDelete.vue";

const tableData = ref<DepartmentEntity[]>([]);
const tableLoading = ref(true);

const getTable = () => {
  tableLoading.value = true;
  Service.findAllDepartment().then((res) => {
    tableData.value = res.data as DepartmentEntity[];
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
      <create-department @success="getTable"></create-department>
      <base-batch-delete v-if="tableRef"
                         :api="Service.deleteDepartment" :table="tableRef" @success='getTable'></base-batch-delete>
    </div>
    <div class='table'>
      <div class='table-container'>
        <el-table ref='tableRef' v-loading='tableLoading' :data='tableData' border default-expand-all height='100%'
                  row-key='id' stripe>
          <el-table-column align="center" type='selection' width='44'></el-table-column>
          <el-table-column label='部门名称' min-width="180" prop='name'>
          </el-table-column>
          <el-table-column label='部门描述' min-width="180" prop='description'>
          </el-table-column>
          <el-table-column label='部门编码' min-width="180" prop='code'>
          </el-table-column>
          <el-table-column label='部门域名' min-width="180" prop='domain'>
          </el-table-column>
          <el-table-column label='部门地址' min-width="180" prop='address'>
          </el-table-column>
          <el-table-column label='部门电话' min-width="180" prop='phone'>
          </el-table-column>
          <el-table-column label='部门图标' min-width="180" prop='icon'>
            <template #default="{row}">
              <el-image v-if="row.icon?.id" :src="getImageUrl(row.icon.id)" class="w-full"></el-image>
            </template>
          </el-table-column>
          <el-table-column align='center' fixed="right" label='操作' width="160">
            <template #default='{ row }'>
              <update-department :id="row.id" @success="getTable"></update-department>
              <base-delete :id="row.id" :api="Service.deleteDepartment" @success="getTable"/>
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
