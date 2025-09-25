# ---------------------------
# 用户-角色关联表
# ---------------------------
table "system_c_user_role" {
  schema = schema.public
  column "user_id" {
    type = varchar(50)
    null = false
  }
  column "role_id" {
    type = varchar(50)
    null = false
  }
  primary_key { columns = [column.user_id, column.role_id] }
  index "idx_user_role_user_id" { columns = [column.user_id] }
}