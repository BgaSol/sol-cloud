#!/bin/bash
# 停止 Docker Compose 服务
set -euo pipefail
cd ..

# ANSI 颜色
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
RESET='\033[0m'

print_info() {
    echo -e "${BLUE}📘 $1${RESET}"
}

print_step() {
    echo -e "\n${YELLOW}🚀 $1${RESET}"
}

print_error() {
    echo -e "${RED}❌ $1${RESET}"
}

print_divider() {
    echo -e "${YELLOW}----------------------------------------${RESET}"
}

print_divider
print_step "关闭现有 Docker Compose 服务 🧹"
cd docker || { print_error "❌ 未找到 docker 目录"; exit 1; }

docker compose -f minio.apply.docker-compose.yml down 2>/dev/null
docker compose -f monitor.docker-compose.yml down 2>/dev/null || print_info "monitor服务未运行"

cd ..
print_divider
print_info "Docker 服务已停止"
