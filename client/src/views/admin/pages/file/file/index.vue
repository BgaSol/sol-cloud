<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import {
  FileEntity,
  FilePageDto,
  PageVoFileEntity,
  Service,
} from '~/generated/file';
import BaseDelete from '~/components/BaseDelete.vue';
import { usePage } from '~/composables/PageHook';
import FileCreate from '~/views/admin/pages/file/file/FileCreate.vue';
import { ElTable as ElTableRefType } from 'element-plus/es/components/table';
import {
  dayjs,
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElPagination,
  ElTable,
  ElTableColumn,
  ElImage,
} from 'element-plus';
import BaseBatchDelete from '~/components/BaseBatchDelete.vue';
import DownloadFile from '~/views/admin/pages/file/components/DownloadFile.vue';

const tableData = ref<PageVoFileEntity>({
  total: 0,
  result: [] as FileEntity[],
});

const defaultRequestData: () => FilePageDto = () => ({
  page: 1,
  size: 20,
  name: '',
  url: '',
  maxLen: null as unknown as number,
  minLen: null as unknown as number,
  hash: '',
  status: '',
  suffix: '',
  source: '',
  bucket: '',
});
const requestData = ref<FilePageDto>(defaultRequestData());

const tableLoading = ref(true);

const getTable = async () => {
  tableLoading.value = true;
  return Service.findPageFile(requestData.value)
    .then((res) => {
      tableData.value = res.data as PageVoFileEntity;
      pageKey.value++;
    })
    .finally(() => {
      tableLoading.value = false;
    });
};

onMounted(() => {
  getTable();
});

const { handleCurrentChange, handleSizeChange, resetPageData, search } =
  usePage(requestData, getTable);
// 用于强制刷新组件
const pageKey = ref(0);

const tableRef = ref<InstanceType<typeof ElTableRefType>>();
</script>

<template>
  <div class="main">
    <div class="filter-header">
      <el-form inline>
        <el-form-item label="名称" class="w-250px">
          <el-input
            v-model="requestData.name"
            class="important-w-full"
            clearable
            placeholder="请输入名称"
            @change="search"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="状态" class="w-250px">
          <el-input
            v-model="requestData.status"
            class="important-w-full"
            clearable
            placeholder="请输入状态"
            @change="search"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="后缀" class="w-250px">
          <el-input
            v-model="requestData.suffix"
            class="important-w-full"
            clearable
            placeholder="请输入后缀"
            @change="search"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="HASH" class="w-250px">
          <el-input
            v-model="requestData.hash"
            class="important-w-full"
            clearable
            placeholder="请输入HASH"
            @change="search"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="大小(MB)" class="w-250px">
          <el-input
            type="number"
            v-model="requestData.minLen"
            class="important-w-40%"
            clearable
            placeholder="最小值"
            @change="search"
          >
          </el-input>
          <div class="flex-1 text-center">—</div>
          <el-input
            type="number"
            v-model="requestData.maxLen"
            class="important-w-40%"
            clearable
            placeholder="最大值"
            @change="search"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="来源" class="w-250px">
          <el-input
            v-model="requestData.source"
            class="important-w-full"
            clearable
            placeholder="请输入来源"
            @change="search"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="桶" class="w-250px">
          <el-input
            v-model="requestData.bucket"
            class="important-w-full"
            clearable
            placeholder="请输入桶"
            @change="search"
          >
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetPageData(defaultRequestData())"
            >重置</el-button
          >
        </el-form-item>
      </el-form>
    </div>
    <div class="controllers">
      <file-create @success="getTable"></file-create>
      <base-batch-delete
        v-if="tableRef"
        :api="Service.deleteImage"
        :table="tableRef"
        @success="getTable"
      ></base-batch-delete>
    </div>
    <div class="table">
      <div class="table-container">
        <el-table
          ref="tableRef"
          v-loading="tableLoading"
          :data="tableData.result"
          border
          height="100%"
          row-key="id"
          stripe
        >
          <el-table-column
            align="center"
            type="selection"
            width="44"
          ></el-table-column>
          <el-table-column
            align="center"
            label="名称"
            min-width="80"
            prop="name"
          ></el-table-column>
          <el-table-column
            align="center"
            label="类型"
            min-width="80"
            prop="type"
          ></el-table-column>
          <el-table-column
            align="center"
            label="文件地址"
            min-width="140"
            prop="url"
          ></el-table-column>
          <el-table-column
            align="center"
            label="文件大小"
            min-width="140"
            prop="size"
            :formatter="(row) => (row.size / 1024 / 1024).toFixed(2) + 'M'"
          ></el-table-column>
          <el-table-column
            align="center"
            label="创建时间"
            prop="createTime"
            width="180"
            :formatter="
              (row) => dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss')
            "
          ></el-table-column>
          <el-table-column
            align="center"
            fixed="right"
            label="操作"
            width="160"
          >
            <template #default="{ row }">
              <download-file
                :fileId="row.id"
                :fileName="row.name"
              ></download-file>
              <base-delete
                :id="row.id"
                :api="Service.deleteFile"
                @success="getTable"
              ></base-delete>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div class="page-footer">
      <el-pagination
        :current-page="requestData.page"
        :page-size="requestData.size"
        :page-sizes="[10, 20, 50, 100, 200]"
        :total="tableData.total"
        background
        layout="prev, pager, next, jumper, total, sizes"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
      >
      </el-pagination>
    </div>
  </div>
</template>
