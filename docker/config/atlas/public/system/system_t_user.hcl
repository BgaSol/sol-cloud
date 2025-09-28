# ---------------------------
# 用户表
# ---------------------------
table "system_t_user" {
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
  column "username" {
    type = varchar(100)
    null = false
  }
  column "password" {
    type = varchar(255)
    null = false
  }
  column "nickname" {
    type = varchar(100)
    null = true
  }
  column "email" {
    type = varchar(100)
    null = true
  }
  column "phone" {
    type = varchar(50)
    null = true
  }
  column "status" {
    type = varchar(50)
    null = true
  }
  column "avatar_id" {
    type = varchar(50)
    null = true
  }
  column "locked" {
    type    = boolean
    null    = true
    default = false
  }
  column "department_id" {
    type = varchar(50)
    null = true
  }

  primary_key {
    columns = [column.id]
  }

  index "idx_user_create_time" {
    columns = [column.create_time]
  }
  index "idx_user_username" {
    columns = [column.username]
  }
  index "idx_user_nickname" {
    columns = [column.nickname]
  }
  index "idx_user_phone" {
    columns = [column.phone]
  }
  index "idx_user_email" {
    columns = [column.email]
  }
  index "idx_user_department_id" {
    columns = [column.department_id]
  }

  index "uk_user_username" {
    unique = true
    columns = [column.username]
  }
  foreign_key "fk_user_department_id" {
    columns = [column.department_id]
    ref_columns = [table.system_t_department.column.id]
    on_delete = RESTRICT # 如果用户正在引用部门，禁止删除部门
  }
}