# ---------------------------
# POI导出记录表
# ---------------------------
table "poi_t_export_history" {
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

  column "export_server" {
    type = varchar(100)
    null = false
  }
  column "export_name" {
    type = varchar(100)
    null = true
  }
  column "params" {
    type = text
    null = true
  }
  column "status" {
    type    = int
    null    = false
    default = 0
  }
  column "file_id" {
    type = varchar(50)
    null = true
  }
  column "error_message" {
    type = text
    null = true
  }
  primary_key {
    columns = [column.id]
  }
  column "department_id" {
    type = varchar(50)
    null = true
  }
  column "user_name" {
    type = varchar(50)
    null = true
  }
  column "file_size" {
    type = bigint
    null = true
  }
  column "file_num" {
    type = bigint
    null = true
  }

  index "idx_export_server" {
    columns = [column.export_server]
  }
  index "idx_export_status" {
    columns = [column.status]
  }
  index "idx_export_time" {
    columns = [column.create_time]
  }
}
