#!/bin/bash

# Atlas Schema 差异比较脚本
# 功能: 比较数据库实际结构与HCL文件定义的差异

set -e

# 构建数据库连接字符串
DB_URL="postgres://${DB_USER}:${DB_PASSWORD}@${DB_HOST}:${DB_PORT}/${DB_NAME}?sslmode=disable"
DEV_ENV="postgres://${DB_USER}:${DB_PASSWORD}@${DB_HOST}:${DB_PORT}/atlas?search_path=public&sslmode=disable"

# 检查atlas命令是否存在
if ! command -v atlas &> /dev/null; then
    echo "错误: atlas 命令未找到，请确保已安装 Atlas CLI"
    exit 1
fi

# 自动发现HCL文件（包括当前目录和子目录）
HCL_FILES=$(find . -name "*.hcl" -type f | sort)
if [ -z "$HCL_FILES" ]; then
    echo "错误: 未找到任何HCL文件"
    exit 1
fi

# 构建参数
TO_ARGS=""
for hcl_file in $HCL_FILES; do
    TO_ARGS="$TO_ARGS --to file://$hcl_file"
done

# 执行atlas diff命令
atlas schema diff --from "${DB_URL}"$TO_ARGS --dev-url "${DEV_ENV}"