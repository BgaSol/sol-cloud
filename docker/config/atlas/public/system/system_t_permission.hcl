# ---------------------------
# 权限表
# ---------------------------
table "system_t_permission" {
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
  column "name" {
    type = varchar(100)
    null = false
  }
  column "code" {
    type = varchar(100)
    null = true
  }
  column "path" {
    type = varchar(255)
    null = true
  }
  column "micro_service" {
    type = varchar(100)
    null = true
  }
  primary_key { columns = [column.id] }
  index "idx_premission_parent_id" {
    columns = [column.parent_id]
  }
  index "idx_permission_create_time" {
    columns = [column.create_time]
  }
  index "uk_permission_code" {
    unique = true
    columns = [column.code]
  }
}