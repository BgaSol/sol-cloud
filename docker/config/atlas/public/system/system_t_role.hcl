# ---------------------------
# 角色表
# ---------------------------
table "system_t_role" {
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
  column "status" {
    type    = int
    null    = true
    default = 1
  }
  primary_key { columns = [column.id] }
  index "idx_role_create_time" {
    columns = [column.create_time]
    where = "deleted = false"
  }
  index "uk_role_code" {
    unique = true
    columns = [column.code]
    where  = "deleted = false"
  }
}