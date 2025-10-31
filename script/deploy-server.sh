#!/bin/bash
# 一键部署脚本
set -euo pipefail

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
print_divider() { echo -e "${YELLOW}----------------------------------------${RESET}"; }

START_TIME=$(date +%s)
print_divider

# ===========================
# 1️⃣ 停止 Docker 服务
# ===========================
print_step "关闭现有 Docker 服务"
bash ./stop-server.sh

# ===========================
# 2️⃣ 管理卷
# ===========================
print_step "检查/创建 Docker 卷"
bash ./manage-volumes.sh

# ===========================
# 3️⃣ 管理网络
# ===========================
print_step "检查/创建 Docker 网络"
bash ./manage-network.sh

# ===========================
# 4️⃣ 构建后端
# ===========================
print_step "构建后端"
bash ./build-backend.sh

# ===========================
# 5️⃣ 构建前端
# ===========================
print_step "构建前端"
bash ./build-frontend.sh

# ===========================
# 6️⃣ 启动服务
# ===========================
print_step "启动服务"
bash ./start-server.sh

END_TIME=$(date +%s)
TOTAL_TIME=$((END_TIME - START_TIME))

print_divider
print_success "🎉 一键部署完成！"
print_info "总耗时: ${TOTAL_TIME}秒"
print_info "可以使用 'docker ps' 查看运行状态"
print_divider
