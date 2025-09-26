# ---------------------------
# 用户表
# ---------------------------
table "system_t_user" {
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

  column "username" {
    type = varchar(100)
    null = false
  }
  column "password" {
    type = varchar(255)
    null = false
  }
  column "nickname" {
    type = varchar(100)
    null = true
  }
  column "email" {
    type = varchar(100)
    null = true
  }
  column "phone" {
    type = varchar(50)
    null = true
  }
  column "status" {
    type    = int
    null    = true
    default = 1
  }
  column "avatar_id" {
    type = varchar(50)
    null = true
  }
  column "locked" {
    type    = boolean
    null    = true
    default = false
  }
  column "department_id" {
    type = varchar(50)
    null = true
  }
  primary_key { columns = [column.id] }
  index "idx_user_create_time" {
    columns = [column.create_time]
    where = "deleted = false"
  }
  index "uk_user_username" {
    unique = true
    columns = [column.username]
    where = "deleted = false"
  }
}