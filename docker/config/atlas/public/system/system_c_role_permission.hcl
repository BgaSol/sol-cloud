# ---------------------------
# 角色-权限关联表
# ---------------------------
table "system_c_role_permission" {
  schema = schema.public
  column "role_id" {
    type = varchar(50)
    null = false
  }
  column "permission_id" {
    type = varchar(50)
    null = false
  }
  primary_key { columns = [column.role_id, column.permission_id] }
  index "idx_role_permission_role_id" { columns = [column.role_id] }
}