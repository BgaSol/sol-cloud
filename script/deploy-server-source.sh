#!/bin/bash
# 源码自动部署脚本
set -euo pipefail

# ===========================
# 加锁防止并发执行
# ===========================
LOCK_FILE="/tmp/sol-cloud-deploy.lock"
exec 200>"$LOCK_FILE"
flock -n 200 || { echo "$(date) 🔒 已有部署实例在运行，退出"; exit 1; }

# ===========================
# ANSI 颜色
# ===========================
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
RESET='\033[0m'

print_info() { echo -e "${BLUE}📘 $1${RESET}"; }
print_success() { echo -e "${GREEN}✅ $1${RESET}"; }
print_error() { echo -e "${RED}❌ $1${RESET}"; }
print_step() { echo -e "\n${YELLOW}🚀 $1${RESET}"; }

# ===========================
# 1️⃣ 获取当前 commit hash
# ===========================
OLD_HASH=$(git rev-parse HEAD)
print_info "当前 commit: $OLD_HASH"

# ===========================
# 2️⃣ 拉取最新代码
# ===========================
print_step "拉取最新代码"
git pull origin main
NEW_HASH=$(git rev-parse HEAD)
print_info "最新 commit: $NEW_HASH"

# ===========================
# 3️⃣ 判断是否有变更
# ===========================
if [ "$OLD_HASH" = "$NEW_HASH" ]; then
  print_info "没有新提交，跳过部署"
  exit 0
fi

print_step "检测到新提交，调用 deploy-server.sh 执行部署"

START_TIME=$(date +%s)

if [ -f "./deploy-server.sh" ]; then
    bash ./deploy-server.sh
else
    print_error "未找到 deploy-server.sh，无法执行部署"
    exit 1
fi

END_TIME=$(date +%s)
TOTAL_TIME=$((END_TIME - START_TIME))

print_success "🎉 部署完成！"
print_info "总耗时: ${TOTAL_TIME} 秒"
