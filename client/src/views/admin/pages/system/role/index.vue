<script lang='ts' setup>
import {onMounted, ref} from 'vue';
import {RoleEntity, Service} from "~/generated/system";
import BaseDelete from "~/components/BaseDelete.vue";
import RoleCreate from "~/views/admin/pages/system/role/RoleCreate.vue";
import RoleUpdate from "~/views/admin/pages/system/role/RoleUpdate.vue";
import {ElTable as ElTableRefType} from "element-plus/es/components/table";
import {dayjs, ElTable, ElTableColumn} from "element-plus";
import BaseBatchDelete from "~/components/BaseBatchDelete.vue";

const tableData = ref<RoleEntity[]>([]);
const tableLoading = ref(true);

const getTable = async () => {
  tableLoading.value = true;
  return Service.findAllRole().then((res) => {
    tableData.value = res.data as RoleEntity[];
  }).finally(() => {
    tableLoading.value = false;
  });
};
onMounted(async () => {
  await getTable();
});
const tableRef = ref<InstanceType<typeof ElTableRefType>>();
</script>

<template>
  <div class='main'>
    <div class='controllers'>
      <role-create @success="getTable"></role-create>
      <base-batch-delete v-if="tableRef"
                         :api="Service.deleteRole" :table="tableRef" @success='getTable'></base-batch-delete>
    </div>
    <div class='table'>
      <div class='table-container'>
        <el-table ref='tableRef' v-loading='tableLoading' :data='tableData' border height='100%'
                  row-key='id' stripe>
          <el-table-column align="center" type='selection' width='44'></el-table-column>
          <el-table-column align='center' label='角色名称' prop='name' min-width='200'>
          </el-table-column>
          <el-table-column align='center' label='创建时间' prop='createTime' min-width='180'>
            <template #default='{row}'>
              {{ dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') }}
            </template>
          </el-table-column>
          <el-table-column align='center' label='更新时间' prop='updateTime' min-width='180'>
            <template #default='{row}'>
              {{ dayjs(row.updateTime).format('YYYY-MM-DD HH:mm:ss') }}
            </template>
          </el-table-column>
          <el-table-column align='center' label='角色描述' prop='description'>
          </el-table-column>
          <el-table-column align='center' fixed="right" label='操作' width='160'>
            <template #default='{row}'>
              <role-update :id="row.id" @success="getTable"></role-update>
              <base-delete :id="row.id" :api="Service.deleteRole" @success="getTable"/>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>
