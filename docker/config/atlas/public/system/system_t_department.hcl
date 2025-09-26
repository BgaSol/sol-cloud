# ---------------------------
# 部门表
# ---------------------------
table "system_t_department" {
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
  column "deleted" {
    type    = boolean
    null    = true
    default = false
  }

  column "parent_id" {
    type = varchar(50)
    null = true
  }

  column "name" {
    type = varchar(100)
    null = false
  }
  column "code" {
    type = varchar(100)
    null = true
  }
  column "domain" {
    type = varchar(255)
    null = true
  }
  column "address" {
    type = varchar(255)
    null = true
  }
  column "phone" {
    type = varchar(50)
    null = true
  }
  column "html" {
    type = text
    null = true
  }
  column "icon_id" {
    type = varchar(50)
    null = true
  }
  primary_key { columns = [column.id] }
  index "idx_department_parent_id" {
    columns = [column.parent_id]
    where = "deleted = false"
  }
  index "idx_department_create_time" {
    columns = [column.create_time]
    where = "deleted = false"
  }
  index "uk_department_code" {
    unique = true
    columns = [column.code]
    where  = "deleted = false"
  }
}