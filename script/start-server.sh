#!/bin/bash
# 启动 Docker 服务
set -e
cd ..

BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
RESET='\033[0m'

print_info() { echo -e "${BLUE}📘 $1${RESET}"; }
print_success() { echo -e "${GREEN}✅ $1${RESET}"; }
print_step() { echo -e "\n${YELLOW}🚀 $1${RESET}"; }
print_error() { echo -e "${RED}❌ $1${RESET}"; }

cd docker || exit 1

print_step "重新启动 Docker Compose 服务 🚀"
docker compose -f infra.docker-compose.yml up -d
print_info "正在等待服务组件启动..."
sleep 10

print_info "💾 正在同步数据库..."
docker compose -f atlas.apply.docker-compose.yml up
docker compose -f atlas.apply.docker-compose.yml down

print_info "🚀 启动应用服务..."
docker compose -f app.docker-compose.yml up -d

print_success "🎉 服务已启动成功！使用 docker ps 查看运行状态"
