#!/bin/bash
# 启动脚本
set -e
cd ..

VOLUMES=(
  pg-data
  redis-data
)

# 需要强制重建的卷
FORCE_RECREATE_VOLUMES=(
  redis-data
  pg-data
)

# ANSI 颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
RESET='\033[0m'

print_info() {
    echo -e "${BLUE}📘 $1${RESET}"
}

print_success() {
    echo -e "${GREEN}✅ $1${RESET}"
}

print_error() {
    echo -e "${RED}❌ $1${RESET}"
}

print_step() {
    echo -e "\n${YELLOW}🚀 $1${RESET}"
}

print_divider() {
    echo -e "${YELLOW}----------------------------------------${RESET}"
}

# 判断是否需要强制重建卷
needs_recreate() {
    local vol="$1"
    for fvol in "${FORCE_RECREATE_VOLUMES[@]}"; do
        if [[ "$vol" == "$fvol" ]]; then
            return 0
        fi
    done
    return 1
}

print_divider
print_step "进入 docker 目录 📁"
cd docker || { print_error "❌ 未找到 docker 目录"; exit 1; }

print_step "关闭现有 Docker Compose 服务 🧹"
docker compose -f monitor.docker-compose.yml down || { print_error "❌ docker compose down monitor.docker-compose.yml 执行失败"; exit 1; }

print_step "初始化minio_bucket"
docker compose -f minio.apply.docker-compose.yml up || { print_error "❌ docker compose up minio.apply.docker-compose.yml 执行失败"; exit 1; }
docker compose -f minio.apply.docker-compose.yml down || { print_error "❌ docker compose down minio.apply.docker-compose.yml 执行失败"; exit 1; }

print_step "启动 Docker Compose 服务 🚀"
docker compose -f monitor.docker-compose.yml up -d || { print_error "❌ docker compose up monitor.docker-compose.yml 执行失败"; exit 1; }