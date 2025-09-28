# ---------------------------
# 权限表
# ---------------------------
table "system_t_permission" {
  schema = schema.public
  column "id" {
    type = varchar(255)
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
    type = varchar(255)
    null = true
  }

  column "name" {
    type = varchar(255)
    null = false
  }
  column "code" {
    type = varchar(255)
    null = true
  }
  column "path" {
    type = varchar(1000)
    null = true
  }
  column "micro_service" {
    type = varchar(100)
    null = true
  }

  primary_key {
    columns = [column.id]
  }

  index "idx_permission_create_time" {
    columns = [column.create_time]
  }
  index "idx_premission_parent_id" {
    columns = [column.parent_id]
  }
  index "idx_permission_name" {
    columns = [column.name]
  }
  index "idx_permission_code" {
    columns = [column.code]
  }
  index "idx_permission_path" {
    columns = [column.path]
  }
  index "idx_permission_micro_service" {
    columns = [column.micro_service]
  }

  index "uk_permission_code" {
    unique = true
    columns = [column.code]
  }

  // 添加外键约束，实现删除父权限时级联删除子权限
  foreign_key "fk_permission_parent_id" {
    columns = [column.parent_id]
    ref_columns = [column.id]
    on_delete = CASCADE # 删父权限，子权限级联删除
  }
}