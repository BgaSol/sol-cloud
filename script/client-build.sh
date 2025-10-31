#!/bin/bash
# 前端打包脚本
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

# 前端构建阶段
print_divider
print_step "开始前端构建 🌐"

cd ../client
print_info "📦 安装依赖 (npm install)..."
npm install

print_info "🧱 执行构建..."
# 设置Node.js内存限制
export NODE_OPTIONS="--max-old-space-size=4096"
npm run build
cd ..

print_success "🎊 前端构建完成"

# 前端构建产物复制
FRONTEND_OUTPUT_DIR="../docker/output/client"
print_info "复制前端构建产物..."
rm -rf "${FRONTEND_OUTPUT_DIR}"
mkdir -p "${FRONTEND_OUTPUT_DIR}"
# 使用rsync复制文件
rsync -a --delete --no-compress client/dist/ "${FRONTEND_OUTPUT_DIR}/"
print_success "前端构建产物复制完成"

# 计算构建时间
END_TIME=$(date +%s)
BUILD_TIME=$((END_TIME - START_TIME))
echo -e "${GREEN}🎉🎉🎉 前端构建完成！${RESET}"
echo -e "💻 前端输出目录: ${FRONTEND_OUTPUT_DIR}"
print_info "构建耗时: ${BUILD_TIME}秒"