#!/bin/bash
# 打包脚本
set -euo pipefail

# ANSI 颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
RESET='\033[0m'

# 打印函数
print_info() {
    echo -e "${BLUE}📘 $1${RESET}"
}

print_step() {
    echo -e "\n${YELLOW}🚀 $1${RESET}"
}

print_success() {
    echo -e "${GREEN} ✅ $1${RESET}"
}

print_error() {
    echo -e "${RED}❌ $1${RESET}"
}

print_divider() {
    echo -e "${YELLOW}----------------------------------------${RESET}"
}

# 记录开始时间
START_TIME=$(date +%s)

print_divider

print_step "关闭现有 Docker Compose 服务 🧹"
cd ../docker || { print_error "❌ 未找到 docker 目录"; exit 1; }
# 停止Docker服务，允许失败
docker compose -f app.docker-compose.yml down 2>/dev/null || print_info "app服务未运行"
docker compose -f infra.docker-compose.yml down 2>/dev/null || print_info "infra服务未运行"
cd ..

print_step "开始执行完整构建流程"

print_info "开始后端构建..."
./server-build.sh

print_info "开始前端构建..."
./client-build.sh

# 完整构建成功
print_divider
# 计算构建时间
END_TIME=$(date +%s)
BUILD_TIME=$((END_TIME - START_TIME))
echo -e "${GREEN}🎉🎉🎉 全部构建完成！${RESET}"
print_info "构建耗时: ${BUILD_TIME}秒"
