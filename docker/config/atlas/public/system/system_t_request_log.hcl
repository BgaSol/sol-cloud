# ---------------------------
# 请求日志表
# ---------------------------
table "system_t_request_log" {
  schema = schema.public

  column "id" {
    type = varchar(50)
    null = false
  }
  column "type" {
    type = varchar(50)
    null = true
  }
  column "sort" {
    type = int
    null = true
  }
  column "create_time" {
    type = timestamp(6)
    null = true
    default = sql("now()")
  }
  column "update_time" {
    type = timestamp(6)
    null = true
  }
  column "description" {
    type = text
    null = true
  }

  column "parent_id" {
    type = varchar(50)
    null = true
  }

  column "trace_id" {
    type = varchar(64)
    null = true
  }
  column "service_name" {
    type = varchar(100)
    null = true
  }
  column "node_name" {
    type = varchar(100)
    null = true
  }
  column "node_ip" {
    type = varchar(50)
    null = true
  }
  column "method" {
    type = varchar(10)
    null = true
  }
  column "uri" {
    type = varchar(2000)
    null = true
  }
  column "query_string" {
    type = text
    null = true
  }
  column "status" {
    type = int
    null = true
  }
  column "thread_id" {
    type = bigint
    null = true
  }
  column "error_log" {
    type = text
    null = true
  }
  column "is_primary_err" {
    type = boolean
    null = true
  }
  column "business_method" {
    type = varchar(255)
    null = true
  }
  column "business_controller" {
    type = varchar(255)
    null = true
  }
  column "user_id" {
    type = varchar(255)
    null = true
  }

  primary_key {
    columns = [column.id]
  }

  # 索引
  index "idx_request_log_create_time" {
    columns = [column.create_time]
  }
  index "idx_request_log_parent_id" {
    columns = [column.parent_id]
  }
  index "idx_request_log_trace_id" {
    columns = [column.trace_id]
  }
}