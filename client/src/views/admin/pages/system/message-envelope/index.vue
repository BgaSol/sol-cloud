<script setup lang="ts">
import {onMounted, ref} from 'vue';
import type {TagProps} from 'element-plus';
import {
  dayjs,
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElOption,
  ElPagination,
  ElSelect,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';
import type {ElTable as ElTableRefType} from 'element-plus/es/components/table';
import {
  MessageEnvelopeEntityObject,
  MessageEnvelopePageDto,
  PageVoMessageEnvelopeEntityObject,
  Service,
} from '~/generated/system';
import {usePage} from '~/composables/PageHook';
import BatchRead from '~/views/admin/pages/system/message-envelope/BatchRead.vue';
import Read from '~/views/admin/pages/system/message-envelope/Read.vue';
import {BusinessTypeKey, BusinessTypes} from '~/views/admin/pages/system/message-envelope/message';

type StatusTag = {
  type: TagProps['type'];
  text: string;
};

const statusTag = (status: MessageEnvelopeEntityObject.status): StatusTag => {
  switch (status) {
    case MessageEnvelopeEntityObject.status.UNREAD:
      return {
        type: 'info',
        text: '未读',
      };
    case MessageEnvelopeEntityObject.status.READ:
      return {
        type: 'success',
        text: '已读',
      };
    case MessageEnvelopeEntityObject.status.IGNORE:
      return {
        type: 'info',
        text: '忽略',
      };
    default:
      return {
        type: 'info',
        text: '未知',
      };
  }
};

const defaultRequestData: () => MessageEnvelopePageDto = () => ({
  page: 1,
  size: 20,
  title: undefined,
  status: undefined,
  businessType: undefined,
  recipientId: undefined,
});
const requestData = ref<MessageEnvelopePageDto>(defaultRequestData());

const tableData = ref<PageVoMessageEnvelopeEntityObject>({
  total: 0,
  result: [] as MessageEnvelopeEntityObject[],
});
const tableLoading = ref(true);

const getTable = async () => {
  tableLoading.value = true;
  return Service.findPageMessageEnvelope(requestData.value)
      .then((res) => {
        tableData.value = res.data as PageVoMessageEnvelopeEntityObject;
      })
      .finally(() => {
        tableLoading.value = false;
      });
};

const {handleCurrentChange, handleSizeChange, resetPageData, search} =
    usePage(requestData, getTable);
const tableRef = ref<InstanceType<typeof ElTableRefType>>();

const userList = ref<Map<string, string>>(new Map());

onMounted(() => {
  getTable();
});
</script>

<template>
  <div class="main">
    <div class="filter-header">
      <el-form inline>
        <el-form-item label="接收用户" class="w-250px">
          <el-select
              v-model="requestData.recipientId"
              placeholder="请选择消息状态"
              clearable
          >
            <el-option
                :label="user[1]"
                :value="user[0]"
                v-for="user in userList"
                :key="user[0]"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="消息类型" class="w-250px">
          <el-select v-model="requestData.businessType" clearable>
            <el-option
                v-for="(value, key) in BusinessTypes"
                :label="value"
                :value="key"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="消息标题" class="w-250px">
          <el-input
              v-model="requestData.title"
              placeholder="请输入消息标题"
              clearable
          />
        </el-form-item>
        <el-form-item label="消息状态" class="w-250px">
          <el-select
              v-model="requestData.status"
              placeholder="请选择消息状态"
              clearable
          >
            <el-option
                label="未读"
                :value="MessageEnvelopePageDto.status.UNREAD"
            />
            <el-option
                label="已读"
                :value="MessageEnvelopePageDto.status.READ"
            />
            <el-option
                label="忽略"
                :value="MessageEnvelopePageDto.status.IGNORE"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetPageData(defaultRequestData())"
          >重置
          </el-button
          >
        </el-form-item>
      </el-form>
    </div>
    <div class="controllers">
      <batch-read
          :table="tableRef"
          v-if="tableRef"
          @success="getTable"
      ></batch-read>
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
          <el-table-column prop="businessType" label="消息类型" align="center">
            <template #default="{ row }">
              <el-tag type="info">{{ BusinessTypes[row.businessType as BusinessTypeKey] || row.businessType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="消息标题" align="center"/>
          <el-table-column prop="content" label="消息内容" align="center"/>
          <el-table-column
              prop="content"
              label="接收用户"
              align="center"
              :formatter="(row) => userList.get(row.recipientId ?? '') || '-'"
          />
          <el-table-column prop="status" label="消息状态" align="center">
            <template #default="{ row }">
              <el-tag
                  :type="
                  statusTag(row.status as MessageEnvelopeEntityObject.status)
                    .type
                "
              >
                {{
                  statusTag(row.status as MessageEnvelopeEntityObject.status)
                      .text
                }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
              prop="createTime"
              label="创建时间"
              width="180"
              :formatter="
              (row) => dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss')
            "
          />
          <el-table-column
              align="center"
              fixed="right"
              label="操作"
              width="230"
          >
            <template #default="{ row }">
              <read
                  :id="row.id"
                  @success="getTable"
                  v-if="row.status === MessageEnvelopeEntityObject.status.UNREAD"
              />
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
