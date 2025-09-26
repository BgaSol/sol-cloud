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
    type = varchar(255)
    null = false
  }
  primary_key { columns = [column.role_id, column.permission_id] }
  foreign_key "fk_role_permission_permission_id" {
    columns = [column.permission_id]
    ref_columns = [table.system_t_permission.column.id]
    on_delete = CASCADE # 跟随权限表删除
  }
  foreign_key "fk_role_permission_role_id" {
    columns = [column.role_id]
    ref_columns = [table.system_t_role.column.id]
    on_delete = CASCADE # 跟随角色表删除
  }
  index "idx_role_permission_role_id" { columns = [column.role_id] }
  index "idx_role_permission_permission_id" { columns = [column.permission_id] }
}