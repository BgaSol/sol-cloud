<script lang='ts' setup>
import {onMounted, ref} from "vue";
import {PageVoUserEntity, Service, UserEntity, UserPageDto} from "~/generated/system";
import BaseDelete from "~/components/BaseDelete.vue";
import CreateUser from "~/views/admin/pages/system/user/CreateUser.vue";
import UpdateUser from "~/views/admin/pages/system/user/UpdateUser.vue";
import ResetPassword from "~/views/admin/pages/system/user/ResetPassword.vue";
import {usePage} from "~/composables/PageHook";
import {ElTable as ElTableRefType} from "element-plus/es/components/table";
import {
  dayjs, ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElOption,
  ElPagination,
  ElSelect,
  ElTable,
  ElTableColumn,
  ElTag
} from "element-plus";
import BaseBatchDelete from "~/components/BaseBatchDelete.vue";

const tableData = ref<PageVoUserEntity>({
  total: 0,
  result: [] as UserEntity[]
});
const tableLoading = ref(true);

const getTable = async () => {
  tableLoading.value = true;
  return Service.findPageUser(requestData.value).then((res) => {
    tableData.value = res.data as PageVoUserEntity
  }).finally(() => {
    tableLoading.value = false;
  });
};
onMounted(() => {
  getTable();
});

const defaultRequestData: () => UserPageDto = () => ({
  page: 1,
  size: 20,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  locked: undefined,
});
const requestData = ref<UserPageDto>(defaultRequestData());

const {
  handleCurrentChange, handleSizeChange,
  resetPageData, search
} = usePage(requestData, getTable);
const tableRef = ref<InstanceType<typeof ElTableRefType>>();
</script>

<template>
  <div class='main'>
    <div class='filter-header'>
      <el-form inline>
        <el-form-item label='用户名' class="w-250px">
          <el-input v-model='requestData.username' class="important-w-full" clearable placeholder='请输入用户'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item label='昵称' class="w-250px">
          <el-input v-model='requestData.nickname' class="important-w-full" clearable placeholder='请输入昵称'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item label='邮箱' class="w-250px">
          <el-input v-model='requestData.email' class="important-w-full" clearable placeholder='请输入邮箱'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item label='账户锁定' class="w-250px">
          <el-select v-model='requestData.locked' class="important-w-full" clearable placeholder='请选择'
                     @change="search">
            <el-option :value='false' label='启用'></el-option>
            <el-option :value='true' label='锁定'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label='手机号' class="w-250px">
          <el-input v-model='requestData.phone' class="important-w-full" clearable placeholder='请输入手机号'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type='primary' @click='search'>查询</el-button>
          <el-button @click='resetPageData(defaultRequestData())'>重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class='controllers'>
      <create-user @success="getTable"></create-user>
      <base-batch-delete v-if="tableRef"
                         :api="Service.deleteUser" :table="tableRef" @success='getTable'></base-batch-delete>
    </div>
    <div class='table'>
      <div class='table-container'>
        <el-table ref='tableRef' v-loading='tableLoading' :data='tableData.result' border height='100%'
                  row-key='id' stripe>
          <el-table-column align="center" type='selection' width='44'></el-table-column>
          <el-table-column align='center' label='用户名' min-width='180' prop='username'>
          </el-table-column>
          <el-table-column align='center' label='昵称' min-width='180' prop='nickname'>
          </el-table-column>
          <el-table-column align='center' label='邮箱' min-width='180' prop='email'>
          </el-table-column>
          <el-table-column align='center' label='手机号' min-width='180' prop='phone'>
          </el-table-column>
          <el-table-column align='center' label='创建时间' min-width='180' prop='createTime'>
            <template #default='{ row }'>
              {{ dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') }}
            </template>
          </el-table-column>
          <el-table-column align='center' label='更新时间' min-width='180' prop='updateTime'>
            <template #default='{ row }'>
              {{ dayjs(row.updateTime).format('YYYY-MM-DD HH:mm:ss') }}
            </template>
          </el-table-column>
          <el-table-column align='center' label='账户锁定' min-width='90'>
            <template #default='{ row }'>
              <el-tag :type="row.locked?'danger':'success'">
                {{ row.locked ? '锁定' : '启用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align='center' label='角色' prop='roles'>
            <template #default='{ row }'>
              <el-tag v-for='role in row.roles' :key='role.id' class='mx-1'>
                {{ role.name }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align='center' fixed="right" label='操作' width='230'>
            <template #default='{ row }'>
              <reset-password :id="row.id" @success="getTable"></reset-password>
              <update-user :id="row.id" @success="getTable"></update-user>
              <base-delete :id="row.id" :api="Service.deleteUser" @success="getTable"/>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div class='page-footer'>
      <el-pagination :current-page='requestData.page' :page-size='requestData.size'
                     :page-sizes="[10, 20, 50, 100, 200]" :total='tableData.total'
                     background layout="prev, pager, next, jumper, total, sizes"
                     @current-change='handleCurrentChange' @size-change="handleSizeChange">
      </el-pagination>
    </div>
  </div>
</template>
