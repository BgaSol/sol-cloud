#!/bin/bash
# 管理 Docker 卷
set -e
cd ..

VOLUMES=( pg-data )
FORCE_RECREATE_VOLUMES=( )

BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
RESET='\033[0m'

print_info() { echo -e "${BLUE}📘 $1${RESET}"; }
print_success() { echo -e "${GREEN}✅ $1${RESET}"; }
print_step() { echo -e "\n${YELLOW}🚀 $1${RESET}"; }

needs_recreate() {
    local vol="$1"
    for fvol in "${FORCE_RECREATE_VOLUMES[@]}"; do
        [[ "$vol" == "$fvol" ]] && return 0
    done
    return 1
}

cd docker || exit 1
print_step "检查并创建 Docker Volumes"
for volume in "${VOLUMES[@]}"; do
  if needs_recreate "$volume"; then
    print_info "🔄 强制重建卷: $volume"
    docker volume rm "$volume" 2>/dev/null || print_info "卷 $volume 不存在"
    docker volume create "$volume"
    print_success "卷 $volume 已重新创建"
  else
    if docker volume inspect "$volume" > /dev/null 2>&1; then
      print_info "卷 $volume 已存在"
    else
      docker volume create "$volume"
      print_success "卷 $volume 创建成功"
    fi
  fi
done
