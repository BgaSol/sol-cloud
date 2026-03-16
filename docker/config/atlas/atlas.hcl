data "hcl_schema" "app" {
  paths = fileset("public/**/*.hcl")   # ** 代表遞迴所有子目錄
}

env "docker" {
  src = data.hcl_schema.app.url   # 自動合併所有匹配的 .hcl 成一個 schema

  url = "postgres://${getenv("DB_USER")}:${getenv("DB_PASSWORD")}@${getenv("DB_HOST")}:${getenv("DB_PORT")}/${getenv("DB_NAME")}?sslmode=disable"

  dev = "postgres://${getenv("DB_USER")}:${getenv("DB_PASSWORD")}@${getenv("DB_HOST")}:${getenv("DB_PORT")}/atlas?search_path=public&sslmode=disable"

  exclude = [
    "public.*_d_*"
  ]
}
