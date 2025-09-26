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
  foreign_key "fk_user_role_user_id" {
    columns     = [column.user_id]
    ref_columns = [table.system_t_user.column.id]
    on_update   = NO_ACTION
    on_delete   = CASCADE
  }
  foreign_key "fk_user_role_role_id" {
    columns     = [column.role_id]
    ref_columns = [table.system_t_role.column.id]
    on_update   = NO_ACTION
    on_delete   = CASCADE
  }
  index "idx_user_role_user_id" { columns = [column.user_id] }
  index "idx_user_role_role_id" { columns = [column.role_id] }
}