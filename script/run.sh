#!/bin/bash
set -e
cd ..

VOLUMES=(
  pg-data
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

print_divider
print_step "进入 docker 目录 📁"
cd docker || { print_error "❌ 未找到 docker 目录"; exit 1; }

print_step "关闭现有 Docker Compose 服务 🧹"
docker compose down || { print_error "❌ docker compose down 执行失败"; exit 1; }

# 检查 cv_service_1.0.0_amd64.deb 文件是否存在，如果不存在则从FTP获取
DEB_FILE="cv_service_1.0.0_amd64.deb"
if [[ ! -f "./config/cv/${DEB_FILE}" ]]; then
    print_info "🔄 找不到 ${DEB_FILE}，正在从 FTP 服务器下载..."
    curl -u ftpuser:dwst2025 -o "./config/cv/${DEB_FILE}" ftp://139.155.153.83/upload/cv_service_1.0.0_amd64.deb
    if [[ $? -ne 0 ]]; then
        print_error "FTP 下载失败，请检查网络连接和凭证"
        exit 1
    fi
    print_success "✅ 文件下载成功：${DEB_FILE}"
else
    print_info "📂 找到现有文件：${DEB_FILE}"
fi

# 检查服务可执行文件是否存在
SERVICE_BIN="./config/cv/cv_service"
if [[ ! -f "${SERVICE_BIN}" ]]; then
    print_error "找不到服务可执行文件: ${SERVICE_BIN}"
    exit 1
fi

# 授权执行权限
print_info "🔧 授权执行：${SERVICE_BIN}"
chmod +x "${SERVICE_BIN}"

print_step "🔍 检查并创建 Docker Volumes"
for volume in "${VOLUMES[@]}"; do
  if docker volume inspect "$volume" > /dev/null 2>&1; then
    print_info "Volume $volume 已存在"
  else
    print_info "🧱 创建 Volume: $volume"
    docker volume create "$volume" || {
      print_error "无法创建 volume: $volume"
      exit 1
    }
    print_success "Volume $volume 创建成功"
  fi
done

print_step "重新启动 Docker Compose 服务 🚀"
docker compose up -d || { print_error "❌ docker compose up 执行失败"; exit 1; }

print_success "🎉 服务已重新启动成功！使用 docker ps 查看运行状态"

print_divider