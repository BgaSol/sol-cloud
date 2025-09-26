# ---------------------------
# 菜单表
# ---------------------------
table "system_t_menu" {
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
  column "status" {
    type    = int
    null    = true
    default = 1
  }
  column "menu_type" {
    type = varchar(50)
    null = true
  }
  column "route_path" {
    type = varchar(255)
    null = true
  }
  column "icon" {
    type = varchar(100)
    null = true
  }
  column "route_name" {
    type = varchar(100)
    null = true
  }
  column "button_code" {
    type = varchar(100)
    null = true
  }
  column "is_external" {
    type    = boolean
    null    = true
    default = false
  }
  column "external_url" {
    type = varchar(255)
    null = true
  }
  column "is_external_open" {
    type    = boolean
    null    = true
    default = false
  }
  column "is_disabled" {
    type    = boolean
    null    = true
    default = false
  }
  column "is_hidden" {
    type    = boolean
    null    = true
    default = false
  }
  column "menu_group" {
    type = varchar(100)
    null = true
  }
  primary_key { columns = [column.id] }
  index "idx_menu_parent_id" {
    columns = [column.parent_id]
  }
  index "idx_menu_create_time" {
    columns = [column.create_time]
  }
}