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
docker compose -f app.docker-compose.yml down || { print_error "❌ docker compose down app.docker-compose.yml 执行失败"; exit 1; }
docker compose -f infra.docker-compose.yml down || { print_error "❌ docker compose down infra.docker-compose.yml 执行失败"; exit 1; }

# 检查并创建 Docker Volumes
print_step "🔍 检查并创建 Docker Volumes"
for volume in "${VOLUMES[@]}"; do
  if needs_recreate "$volume"; then
    print_info "🔄 强制重建卷: $volume"
    docker volume rm "$volume" 2>/dev/null || print_info "卷 $volume 不存在，无需删除"
    docker volume create "$volume" || { print_error "无法创建卷: $volume"; exit 1; }
    print_success "卷 $volume 已重新创建"
  else
    if docker volume inspect "$volume" > /dev/null 2>&1; then
      print_info "卷 $volume 已存在"
    else
      print_info "🧱 创建卷: $volume"
      docker volume create "$volume" || { print_error "无法创建卷: $volume"; exit 1; }
      print_success "卷 $volume 创建成功"
    fi
  fi
done

print_step "🔍 检查并创建 Docker 网络"
if docker network inspect cloud-app > /dev/null 2>&1; then
  print_info "网络 cloud-app 已存在"
else
  print_info "🌐 创建网络: cloud-app"
  docker network create cloud-app || { print_error "无法创建网络: cloud-app"; exit 1; }
  print_success "网络 cloud-app 创建成功"
fi

print_step "重新启动 Docker Compose 服务 🚀"
docker compose -f infra.docker-compose.yml up -d || { print_error "❌ docker compose up infra.compose.yml 执行失败"; exit 1; }
print_info "🚀 正在等待服务组件启动..."
sleep 10
print_info "💾 正在同步数据库..."
docker compose -f atlas.apply.docker-compose.yml up || { print_error "❌ docker compose up atlas.apply.docker-compose.yml 执行失败"; exit 1; }
docker compose -f atlas.apply.docker-compose.yml down || { print_error "❌ docker compose up atlas.apply.docker-compose.yml down 执行失败"; exit 1; }
print_info "🚀 启动服务应用程序..."
docker compose -f app.docker-compose.yml up -d || { print_error "❌ docker compose up app.docker-compose.yml 执行失败"; exit 1; }

print_success "🎉 服务已启动成功！使用 docker ps 查看运行状态"

print_divider
