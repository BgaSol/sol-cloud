# ---------------------------
# 角色-菜单关联表
# ---------------------------
table "system_c_role_menu" {
  schema = schema.public
  column "role_id" {
    type = varchar(50)
    null = false
  }
  column "menu_id" {
    type = varchar(50)
    null = false
  }
  primary_key { columns = [column.role_id, column.menu_id] }
  index "idx_role_menu_role_id" { columns = [column.role_id] }
}