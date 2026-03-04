#!/bin/bash
# 前端打包脚本
set -euo pipefail
cd ..

# ANSI 颜色
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RESET='\033[0m'

print_info() { echo -e "${BLUE}📘 $1${RESET}"; }
print_step() { echo -e "\n${YELLOW}🚀 $1${RESET}"; }
print_success() { echo -e "${GREEN} ✅ $1${RESET}"; }

START_TIME=$(date +%s)

cd client
print_step "开始前端构建 🌐"
print_info "📦 安装依赖 (pnpm install)..."
pnpm install

print_info "🧱 执行构建..."
export NODE_OPTIONS="--max-old-space-size=4096"
pnpm run build
cd ..

FRONTEND_OUTPUT_DIR="docker/output/client"
print_info "复制前端构建产物..."
rm -rf "${FRONTEND_OUTPUT_DIR}"
mkdir -p "${FRONTEND_OUTPUT_DIR}"
rsync -a --delete --no-compress client/dist/ "${FRONTEND_OUTPUT_DIR}/"

END_TIME=$(date +%s)
BUILD_TIME=$((END_TIME - START_TIME))

print_success "🎊 前端构建完成"
print_info "前端输出目录: ${FRONTEND_OUTPUT_DIR}"
print_info "前端构建耗时: ${BUILD_TIME}秒"
