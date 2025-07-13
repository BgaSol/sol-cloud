#!/bin/bash

set -e

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

print_divider
print_step "进入 docker 目录 📁"
cd docker || { print_error "❌ 未找到 docker 目录"; exit 1; }

print_step "关闭现有 Docker Compose 服务 🧹"
docker compose down || { print_error "❌ docker compose down 执行失败"; exit 1; }

print_step "重新启动 Docker Compose 服务 🚀"
docker compose up -d || { print_error "❌ docker compose up 执行失败"; exit 1; }

print_success "🎉 服务已重新启动成功！使用 docker ps 查看运行状态"

print_divider