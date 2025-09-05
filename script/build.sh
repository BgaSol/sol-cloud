#!/bin/bash

set -e
cd ..
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
    echo -e "${GREEN}✅ $1${RESET}"
}

print_error() {
    echo -e "${RED}❌ $1${RESET}"
}

print_divider() {
    echo -e "${YELLOW}----------------------------------------${RESET}"
}

print_divider
print_step "进入 docker 目录 📁"
cd docker || { print_error "❌ 未找到 docker 目录"; exit 1; }

print_step "关闭现有 Docker Compose 服务 🧹"
docker compose down || { print_error "❌ docker compose down 执行失败"; exit 1; }
print_step "退出 docker 目录 🚪"
cd ..

# 模块配置
BACKEND_MODULES=(
  "gateway-9527"
  "web-system-8081"
  "web-file-8082"
)

# 后端构建阶段
print_divider
print_step "开始后端构建 🏗️"

cd cloud
export MAVEN_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED"
print_info "🔨 执行 Maven 构建..."
mvn clean package -DskipTests
cd ..

print_success "🎉 后端构建成功！"

# 清理输出目录
SERVER_OUTPUT_DIR="docker/output/server"
print_info "🧹 清理后端输出目录..."
rm -rf "${SERVER_OUTPUT_DIR}"
mkdir -p "${SERVER_OUTPUT_DIR}"

# 拷贝构建产物
copy_backend_module() {
  local module=$1
  local module_dir="cloud/web/${module}"
  [[ "${module}" == "gateway-9527" ]] && module_dir="cloud/${module}"

  local output_dir="${SERVER_OUTPUT_DIR}/${module}"
  mkdir -p "${output_dir}/classes"

  print_info "📦 处理模块：${module}"

  rsync -a --exclude='com/bgasol/**' "${module_dir}/target/classes/" "${output_dir}/classes/"
  cp "${module_dir}/target/"*.jar "${output_dir}/app.jar"

  print_success "📁 模块 ${module} 输出完成 → ${output_dir}"
}

print_step "📂 开始复制后端构建产物..."
for module in "${BACKEND_MODULES[@]}"; do
  copy_backend_module "${module}"
done

print_success "🏁 后端构建产物整理完毕"

# 前端构建阶段
print_divider
print_step "开始前端构建 🌐"

cd client
print_info "📦 安装依赖 (npm install)..."
npm install

print_info "🧱 执行构建 (npm run build)..."
npm run build
cd ..

print_success "🎊 前端构建完成"

# 前端构建产物复制
FRONTEND_OUTPUT_DIR="docker/output/client"
print_info "🧹 清理前端输出目录..."
rm -rf "${FRONTEND_OUTPUT_DIR}"
mkdir -p "${FRONTEND_OUTPUT_DIR}"

print_info "📂 拷贝前端构建结果..."
cp -r client/dist/* "${FRONTEND_OUTPUT_DIR}/"

print_success "📁 前端输出完成 → ${FRONTEND_OUTPUT_DIR}"

# 完整构建成功
print_divider
echo -e "${GREEN}🎉🎉🎉 全部构建完成！${RESET}"
echo -e "🔧 后端输出目录: ${SERVER_OUTPUT_DIR}"
echo -e "💻 前端输出目录: ${FRONTEND_OUTPUT_DIR}"
print_divider
