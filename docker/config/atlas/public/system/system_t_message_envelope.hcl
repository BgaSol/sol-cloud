# ---------------------------
# 消息信封表
# ---------------------------
table "system_t_message_envelope" {
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
  
  column "business_type" {
    type = varchar(100)
    null = true
  }
  column "message_recipient_type" {
    type = varchar(50)
    null = true
  }
  column "recipient_id" {
    type = varchar(50)
    null = true
  }
  column "title" {
    type = varchar(255)
    null = true
  }
  column "content" {
    type = text
    null = true
  }
  column "handler" {
    type = varchar(100)
    null = true
  }
  column "metadata" {
    type = text
    null = true
  }
  column "status" {
    type = varchar(50)
    null = true
  }

  primary_key {
    columns = [column.id]
  }

  index "idx_message_envelope_create_time" {
    columns = [column.create_time]
  }
  index "idx_message_envelope_business_type" {
    columns = [column.business_type]
  }
  index "idx_message_envelope_recipient_type" {
    columns = [column.message_recipient_type]
  }
  index "idx_message_envelope_recipient_id" {
    columns = [column.recipient_id]
  }
  index "idx_message_envelope_status" {
    columns = [column.status]
  }
  index "idx_message_envelope_handler" {
    columns = [column.handler]
  }
}