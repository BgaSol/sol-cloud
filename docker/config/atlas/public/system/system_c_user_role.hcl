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
    columns = [column.user_id]
    ref_columns = [table.system_t_user.column.id]
    on_delete = CASCADE   # 删除用户时，删除用户和角色的关联
  }
  foreign_key "fk_user_role_role_id" {
    columns = [column.role_id]
    ref_columns = [table.system_t_role.column.id]
    on_delete = RESTRICT  # 阻止删除有用户正在引用的角色
  }
  index "idx_user_role_user_id" { columns = [column.user_id] }
  index "idx_user_role_role_id" { columns = [column.role_id] }
}