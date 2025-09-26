# ---------------------------
# 文件表
# ---------------------------
table "file_t_file" {
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
    null = false
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
    null = false
    default = false
  }

  column "name" {
    type = varchar(255)
    null = false
  }
  column "url" {
    type = varchar(255)
    null = true
  }
  column "size" {
    type = bigint
    null = true
  }
  column "hash" {
    type = varchar(255)
    null = true
  }
  column "status" {
    type = varchar(50)
    null = true
  }
  column "suffix" {
    type = varchar(20)
    null = true
  }
  column "source" {
    type = varchar(50)
    null = false
  }
  column "bucket" {
    type = varchar(50)
    null = false
  }
  primary_key { columns = [column.id] }
  index "idx_file_create_time" {
    columns = [column.create_time]
    where  = "deleted = false"
  }
}