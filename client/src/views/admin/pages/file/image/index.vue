<script lang='ts' setup>
import {onMounted, ref} from "vue";
import {ImageEntity, ImagePageDto, PageVoImageEntity, Service} from "~/generated/file";
import BaseDelete from "~/components/BaseDelete.vue";
import {usePage} from "~/composables/PageHook";
import ImageCreate from "~/views/admin/pages/file/image/ImageCreate.vue";
import {getImageUrl} from "~/api/HttpRequest";
import ImageUpdate from "~/views/admin/pages/file/image/ImageUpdate.vue";
import {ElTable as ElTableRefType} from "element-plus/es/components/table";
import {ElButton, ElForm, ElFormItem, ElImage, ElInput, ElPagination, ElTable, ElTableColumn} from "element-plus";
import BaseBatchDelete from "~/components/BaseBatchDelete.vue";

const tableData = ref<PageVoImageEntity>({
  total: 0,
  result: [] as ImageEntity[]
});

const defaultRequestData: () => ImagePageDto = () => ({
  page: 1,
  size: 20,
  name: '',
});
const requestData = ref<ImagePageDto>(defaultRequestData());

const tableLoading = ref(true);

const getTable = async () => {
  tableLoading.value = true;
  return Service.findPageImage(requestData.value).then((res) => {
    tableData.value = res.data as PageVoImageEntity;
    pageKey.value++;
  }).finally(() => {
    tableLoading.value = false;
  });
};

onMounted(() => {
  getTable();
});

const {
  handleCurrentChange, handleSizeChange,
  resetPageData, search
} = usePage(requestData, getTable);
// 用于强制刷新组件
const pageKey = ref(0);

const tableRef = ref<InstanceType<typeof ElTableRefType>>();
</script>

<template>
  <div class='main'>
    <div class='filter-header'>
      <el-form inline>
        <el-form-item label='名称' class="w-250px">
          <el-input v-model='requestData.name' class="important-w-full" clearable placeholder='请输入名称'
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
      <image-create @success="getTable"></image-create>
      <base-batch-delete v-if="tableRef"
                         :api="Service.deleteImage" :table="tableRef" @success='getTable'></base-batch-delete>
    </div>
    <div class='table'>
      <div class='table-container'>
        <el-table ref='tableRef' v-loading='tableLoading' :data='tableData.result' border height='100%'
                  row-key='id' stripe>
          <el-table-column align="center" type='selection' width='44'></el-table-column>
          <el-table-column align='center' label='名称' min-width="200" prop='name'></el-table-column>
          <el-table-column align='center' label='类型' min-width="200" prop='type'></el-table-column>
          <el-table-column align='center' label="宽x高" min-width="150">
            <template #default='{ row }'>
              {{ row.width }} x {{ row.height }}
            </template>
          </el-table-column>
          <el-table-column align='center' label='图片' min-width="200">
            <template #default='{ row }'>
              <el-image :src="getImageUrl(row.id)+'&'+pageKey" class="h-20"></el-image>
            </template>
          </el-table-column>
          <el-table-column align='center' fixed="right" label='操作' width='160'>
            <template #default='{ row }'>
              <image-update :id="row.id" @success="getTable"></image-update>
              <base-delete :id="row.id" :api="Service.deleteImage" @success='getTable'></base-delete>
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
