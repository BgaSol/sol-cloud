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
    null = true
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
  index "idx_department_create_time" { columns = [column.deleted, column.create_time] }
  unique "idx_department_code" { columns = [column.code] }
}