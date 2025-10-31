#!/bin/bash
# 管理 Docker 网络
set -e
cd ..

BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RESET='\033[0m'

print_info() { echo -e "${BLUE}📘 $1${RESET}"; }
print_success() { echo -e "${GREEN}✅ $1${RESET}"; }
print_step() { echo -e "\n${YELLOW}🚀 $1${RESET}"; }

cd docker || exit 1
print_step "检查并创建 Docker 网络"
if docker network inspect cloud-app > /dev/null 2>&1; then
  print_info "网络 cloud-app 已存在"
else
  docker network create cloud-app
  print_success "网络 cloud-app 创建成功"
fi
