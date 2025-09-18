#!/bin/bash

set -euo pipefail
cd ..

# 记录开始时间
START_TIME=$(date +%s)
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

print_divider
print_step "进入 docker 目录 📁"
cd docker || { print_error "❌ 未找到 docker 目录"; exit 1; }

print_step "关闭现有 Docker Compose 服务 🧹"
docker compose down || { print_error "❌ docker compose down 执行失败"; exit 1; }
print_step "退出 docker 目录 🚪"
cd ..

# 模块配置
# 启用 BuildKit 提升构建速度与并发处理
export DOCKER_BUILDKIT=${DOCKER_BUILDKIT:-1}
BACKEND_MODULES=(
  "gateway-9527"
  "web-system-8081"
  "web-file-8082"
)

# 准备输出目录
SERVER_OUTPUT_DIR="docker/output/server"
SERVER_ROOT_DIR="${SERVER_OUTPUT_DIR}"

# 在构建开始前清理所有模块目录
print_step "🧹 清理所有模块目录..."
rm -rf "${SERVER_OUTPUT_DIR}"
mkdir -p "${SERVER_OUTPUT_DIR}"
for module in "${BACKEND_MODULES[@]}"; do
  mkdir -p "${SERVER_ROOT_DIR}/${module}"
  print_info "准备模块目录: ${module}"
done

# 后端构建阶段
print_divider
print_step "开始后端构建 🏗️"

cd cloud
export MAVEN_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED"
print_info "🔨 执行 Maven 构建..."
mvn package \
    -DskipTests \
    -Dmaven.build.cache.enabled=true \
    -T 6 \
    -Dspring-boot.repackage.layers.enabled=true
cd ..

print_success "🎉 后端构建成功！"

copy_backend_module() {
  local module=$1                                    # 模块名
  local module_dir                                   # 源码路径
  [[ "${module}" == "gateway-9527" ]] \
    && module_dir="cloud/${module}" \
    || module_dir="cloud/web/${module}"

  local module_root="${SERVER_ROOT_DIR}/${module}"

  print_info "📦 开始分层解压：${module}"

  # 1. 找到唯一 fat-jar
  local fat_jar=$(ls "${module_dir}/target/${module}"-*.jar 2>/dev/null | head -n1)
  if [[ ! -f "$fat_jar" ]]; then
    print_error "❌ 未找到 jar：${module_dir}/target/${module}-*.jar"
    exit 1
  fi

  # 2. 临时目录
  local tmp=$(mktemp -d)

  # 3. 官方 layertools 一次性解开四层
  java -Djarmode=tools -jar "$fat_jar" extract --layers --launcher --destination "$tmp"

  # 4. 整包同步到模块目录（四层 + 索引）
  for layer in dependencies spring-boot-loader snapshot-dependencies application; do
    [[ -d "$tmp/$layer" ]] || continue
    mkdir -p "${module_root}/$layer"
    rsync -a --delete "$tmp/$layer/" "${module_root}/$layer/"
  done

  # 5. 保留索引（启动器需要）
  [[ -f "$tmp/layers.idx" ]] && cp "$tmp/layers.idx" "${module_root}/"

  # 6. 清理临时目录
  rm -rf "$tmp"

  print_success "✅ 模块 ${module} 分层完成 → ${module_root}"
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
# 计算构建时间
END_TIME=$(date +%s)
BUILD_TIME=$((END_TIME - START_TIME))
echo -e "${GREEN}🎉🎉🎉 全部构建完成！${RESET}"
echo -e "🔧 后端输出目录: ${SERVER_OUTPUT_DIR}"
echo -e "💻 前端输出目录: ${FRONTEND_OUTPUT_DIR}"
print_info "构建耗时: ${BUILD_TIME}秒"
