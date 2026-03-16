<script lang='ts' setup>
import {onMounted, ref} from "vue";
import {PageVoRequestLogEntity, RequestLogEntity, RequestLogPageDto, Service} from "~/generated/system";
import {ElTable as ElTableRefType} from "element-plus/es/components/table";
import {
  dayjs,
  ElButton,
  ElDatePicker,
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
import {usePage} from "~/composables/PageHook";

const tableData = ref<PageVoRequestLogEntity>({
  total: 0,
  result: [] as RequestLogEntity[]
});
const tableLoading = ref(true);

const getTable = async () => {
  tableLoading.value = true;
  return Service.findPageRequestLog(requestData.value).then((res) => {
    tableData.value = res.data as PageVoRequestLogEntity
  }).finally(() => {
    tableLoading.value = false;
  });
};
onMounted(() => {
  getTable();
});

const defaultRequestData: () => RequestLogPageDto = () => ({
  page: 1,
  size: 20,
  traceId: '',
  serviceName: '',
  nodeName: '',
  nodeIp: '',
  method: '',
  uri: '',
  businessController: '',
  isPrimaryErr: undefined,
  createTime: new Date() as unknown as string
});
const requestData = ref<RequestLogPageDto>(defaultRequestData());

const {
  handleCurrentChange, handleSizeChange,
  resetPageData, search
} = usePage(requestData, getTable);
const tableRef = ref<InstanceType<typeof ElTableRefType>>();

/**
 * 请求方式颜色
 */
const getMethodTagType = (method?: string) => {

  switch (method) {
    case "GET":
      return "success";

    case "POST":
      return "primary";

    case "PUT":
      return "warning";

    case "DELETE":
      return "danger";

    default:
      return "info";
  }
};


/**
 * 状态码颜色
 */
const getStatusTagType = (status?: number) => {

  if (!status) return "info";

  if (status >= 200 && status < 300)
    return "success";

  if (status >= 300 && status < 400)
    return "warning";

  if (status >= 400 && status < 500)
    return "danger";

  if (status >= 500)
    return "danger";

  return "info";
};

</script>

<template>
  <div class='main'>
    <div class='filter-header'>
      <el-form inline>
        <el-form-item label="日期" required class="w-250px">
          <el-date-picker v-model="requestData.createTime" class="important-w-full" type="date">
          </el-date-picker>
        </el-form-item>
        <el-form-item label='链路 ID' class="w-250px">
          <el-input v-model='requestData.traceId' class="important-w-full" clearable placeholder='请输入链路 ID'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item label='服务名' class="w-250px">
          <el-input v-model='requestData.serviceName' class="important-w-full" clearable placeholder='请输入服务名'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item label='节点名' class="w-250px">
          <el-input v-model='requestData.nodeName' class="important-w-full" clearable placeholder='请输入节点名'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item label='节点 IP' class="w-250px">
          <el-input v-model='requestData.nodeIp' class="important-w-full" clearable placeholder='请输入节点 IP'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item label='请求方式' class="w-250px">
          <el-select v-model='requestData.method' class="important-w-full" clearable placeholder='请选择'
                     @change="search">
            <el-option value='GET' label='GET'></el-option>
            <el-option value='POST' label='POST'></el-option>
            <el-option value='PUT' label='PUT'></el-option>
            <el-option value='DELETE' label='DELETE'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label='请求 URI' class="w-250px">
          <el-input v-model='requestData.uri' class="important-w-full" clearable placeholder='请输入 URI'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item label='业务模块' class="w-250px">
          <el-input v-model='requestData.businessController' class="important-w-full" clearable
                    placeholder='请输入业务模块'
                    @change="search">
          </el-input>
        </el-form-item>
        <el-form-item label='重要异常' class="w-250px">
          <el-select v-model='requestData.isPrimaryErr' class="important-w-full" clearable placeholder='请选择'
                     @change="search">
            <el-option :value='false' label='否'></el-option>
            <el-option :value='true' label='是'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type='primary' @click='search'>查询</el-button>
          <el-button @click='resetPageData(defaultRequestData())'>重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class='table'>
      <div class='table-container'>
        <el-table ref='tableRef' v-loading='tableLoading' :data='tableData.result' border
                  height='100%' row-key='id' stripe>
          <el-table-column label='业务' min-width='250'>
            <template #default='{ row }'>
              <el-tag v-show="row.isPrimaryErr" type="danger">异常</el-tag>
              {{ row.businessController }}
              {{ row.businessMethod }}
            </template>
          </el-table-column>
          <el-table-column align='center' label='请求方式' min-width='100' prop='method'>
            <template #default='{ row }'>
              <el-tag :type="getMethodTagType(row.method)">
                {{ row.method }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align='center' label='状态码' min-width='90' prop='status'>
            <template #default='{ row }'>
              <el-tag :type="getStatusTagType(row.status)">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label='服务名' min-width='140' prop='serviceName'></el-table-column>
          <el-table-column label='节点名' min-width='140' prop='nodeName'></el-table-column>
          <el-table-column label='节点 IP' min-width='140' prop='nodeIp'></el-table-column>
          <el-table-column label='请求时间' min-width='400' prop='createTime'>
            <template #default='{ row }'>
              <el-tag w-50px>
                {{ dayjs(row.updateTime).diff(dayjs(row.createTime), 'millisecond') }}ms
              </el-tag>
              {{ dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss.SSS') }}
              →
              {{ dayjs(row.updateTime).format('YYYY-MM-DD HH:mm:ss.SSS') }}

            </template>
          </el-table-column>
          <el-table-column label='请求 URI' min-width='400' prop='uri'></el-table-column>
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
<style lang="scss">
.el-table .error-row {
  --el-table-tr-bg-color: var(--el-color-danger-light-7);
}
</style>