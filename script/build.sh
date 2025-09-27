#!/bin/bash
# 打包脚本
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

print_step "关闭现有 Docker Compose 服务 🧹"
cd docker || { print_error "❌ 未找到 docker 目录"; exit 1; }
# 停止Docker服务，允许失败
docker compose -f app.docker-compose.yml down 2>/dev/null || print_info "app服务未运行"
docker compose -f infra.docker-compose.yml down 2>/dev/null || print_info "infra服务未运行"
cd ..

# 模块配置
# 启用Docker BuildKit
export DOCKER_BUILDKIT=${DOCKER_BUILDKIT:-1}
BACKEND_MODULES=(
  "gateway-9527"
  "web-system-8081"
  "web-file-8082"
)

# 准备输出目录
SERVER_OUTPUT_DIR="docker/output/server"
SERVER_ROOT_DIR="${SERVER_OUTPUT_DIR}"

# 清理输出目录
print_step "🧹 清理输出目录并准备分层结构..."
rm -rf "${SERVER_OUTPUT_DIR}"
mkdir -p "${SERVER_OUTPUT_DIR}"

# 创建分层目录
LAYERS=("dependencies" "spring-boot-loader" "snapshot-dependencies" "application")
print_info "创建分层目录结构..."
for layer in "${LAYERS[@]}"; do
  mkdir -p "${SERVER_ROOT_DIR}/${layer}"
  for module in "${BACKEND_MODULES[@]}"; do
    mkdir -p "${SERVER_ROOT_DIR}/${layer}/${module}"
  done
done
print_success "分层目录结构创建完成"

# 后端构建阶段
print_divider
print_step "开始后端构建 🏗️"

cd cloud
export MAVEN_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED -Xmx2g -XX:+UseG1GC"
print_info "🔨 执行 Maven 构建..."
mvn clean package \
    -DskipTests \
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

  print_info "📦 开始分层解压：${module}"

  # 找到构建的jar文件
  local fat_jar=$(ls "${module_dir}/target/${module}"-*.jar 2>/dev/null | head -n1)
  if [[ ! -f "$fat_jar" ]]; then
    print_error "❌ 未找到 jar：${module_dir}/target/${module}-*.jar"
    exit 1
  fi

  # 创建临时目录
  local tmp=$(mktemp -d)

  # 使用Spring Boot layertools解压jar
  java -Djarmode=tools -jar "$fat_jar" extract --layers --launcher --destination "$tmp"

  # 复制各层到新的目录结构
  for layer in dependencies spring-boot-loader snapshot-dependencies application; do
    [[ -d "$tmp/$layer" ]] || continue
    local layer_module_dir="${SERVER_ROOT_DIR}/${layer}/${module}"
    mkdir -p "${layer_module_dir}"
    # 优化rsync参数以提升性能
    rsync -a --delete --no-compress --inplace --whole-file "$tmp/$layer/" "${layer_module_dir}/"
  done

  # 复制layers.idx到application层
  if [[ -f "$tmp/layers.idx" ]]; then
    cp "$tmp/layers.idx" "${SERVER_ROOT_DIR}/application/${module}/"
  fi

  # 清理临时文件
  rm -rf "$tmp"

  print_success "✅ 模块 ${module} 分层完成"
}


print_step "📂 开始复制后端构建产物..."
# 并行处理所有模块
print_info "并行处理 ${#BACKEND_MODULES[@]} 个模块..."
for module in "${BACKEND_MODULES[@]}"; do
  copy_backend_module "${module}" &
done
# 等待所有任务完成
wait

print_success "后端构建产物整理完毕"

# 前端构建阶段
print_divider
print_step "开始前端构建 🌐"

cd client
print_info "📦 安装依赖 (npm install)..."
npm install

print_info "🧱 执行构建..."
# 设置Node.js内存限制
export NODE_OPTIONS="--max-old-space-size=4096"
npm run build
cd ..

print_success "🎊 前端构建完成"

# 前端构建产物复制
FRONTEND_OUTPUT_DIR="docker/output/client"
print_info "复制前端构建产物..."
rm -rf "${FRONTEND_OUTPUT_DIR}"
mkdir -p "${FRONTEND_OUTPUT_DIR}"
# 使用rsync复制文件
rsync -a --delete --no-compress client/dist/ "${FRONTEND_OUTPUT_DIR}/"
print_success "前端构建产物复制完成"

# 完整构建成功
print_divider
# 计算构建时间
END_TIME=$(date +%s)
BUILD_TIME=$((END_TIME - START_TIME))
echo -e "${GREEN}🎉🎉🎉 全部构建完成！${RESET}"
echo -e "🔧 后端输出目录: ${SERVER_OUTPUT_DIR}"
echo -e "💻 前端输出目录: ${FRONTEND_OUTPUT_DIR}"
print_info "构建耗时: ${BUILD_TIME}秒"
