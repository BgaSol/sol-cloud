# ---------------------------
# 视频表
# ---------------------------
table "file_t_video" {
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

  column "name" {
    type = varchar(255)
    null = false
  }
  column "width" { 
    type = int
    null = true
  }
  column "height" { 
    type = int
    null = true
  }
  column "duration" { 
    type = int
    null = true
  }
  column "format" { 
    type = varchar(50)
    null = true
  }
  column "bitrate" { 
    type = int
    null = true
  }
  column "fps" { 
    type = int
    null = true
  }
  column "codec" { 
    type = varchar(50)
    null = true
  }
  column "file_id" { 
    type = varchar(50)
    null = true
  }
  primary_key { columns = [column.id] }
  index "idx_video_create_time" { columns = [column.deleted, column.create_time] }
}