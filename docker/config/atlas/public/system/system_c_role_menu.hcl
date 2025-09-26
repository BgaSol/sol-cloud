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
  foreign_key "fk_role_menu_role_id" {
    columns = [column.role_id]
    ref_columns = [table.system_t_role.column.id]
    on_delete = CASCADE # 删除角色时 删除角色-菜单关联
  }
  foreign_key "fk_role_menu_menu_id" {
    columns = [column.menu_id]
    ref_columns = [table.system_t_menu.column.id]
    on_delete = CASCADE # 删除菜单时 删除角色-菜单关联
  }

  index "idx_role_menu_role_id" { columns = [column.role_id] }
  index "idx_role_menu_menu_id" { columns = [column.menu_id] }
}