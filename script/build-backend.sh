#!/bin/bash
# 后端打包脚本
set -euo pipefail
cd ..

# 解析命令行参数
DISCOVERY_TYPE="${DISCOVERY_TYPE:-consul}"  # 默认值为 consul

while [[ $# -gt 0 ]]; do
  case $1 in
    --discovery-type|-d)
      DISCOVERY_TYPE="$2"
      shift 2
      ;;
    *)
      echo "未知参数: $1" >&2
      exit 1
      ;;
  esac
done

# ANSI 颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
RESET='\033[0m'

print_info() { echo -e "${BLUE}📘 $1${RESET}"; }
print_step() { echo -e "\n${YELLOW}🚀 $1${RESET}"; }
print_success() { echo -e "${GREEN} ✅ $1${RESET}"; }
print_error() { echo -e "${RED}❌ $1${RESET}"; }
print_divider() { echo -e "${YELLOW}----------------------------------------${RESET}"; }

START_TIME=$(date +%s)

BACKEND_MODULES=( "gateway-9527" "web-system-8081" "web-file-8082" )
SERVER_OUTPUT_DIR="docker/output/server"
SERVER_ROOT_DIR="${SERVER_OUTPUT_DIR}"

print_step "🧹 清理输出目录并准备分层结构..."
rm -rf "${SERVER_OUTPUT_DIR}"
mkdir -p "${SERVER_OUTPUT_DIR}"

LAYERS=("dependencies" "spring-boot-loader" "snapshot-dependencies" "application")
print_info "创建分层目录结构..."
for layer in "${LAYERS[@]}"; do
  mkdir -p "${SERVER_ROOT_DIR}/${layer}"
  for module in "${BACKEND_MODULES[@]}"; do
    mkdir -p "${SERVER_ROOT_DIR}/${layer}/${module}"
  done
done
print_success "分层目录结构创建完成"

print_divider
print_step "开始后端构建 🏗️"
cd cloud
export MAVEN_OPTS="--add-opens=java.base/java.lang=ALL-UNNAMED -Xmx2g -XX:+UseG1GC"
print_info "🔨 执行 Maven 构建...(单线程) discovery.type=${DISCOVERY_TYPE}"
mvn clean package -DskipTests -T 1 -Dspring-boot.repackage.layers.enabled=true -Ddiscovery.type="${DISCOVERY_TYPE}"
cd ..
print_success "🎉 后端构建成功！"

copy_backend_module() {
  local module=$1
  local module_dir
  [[ "${module}" == "gateway-9527" ]] && module_dir="cloud/${module}" || module_dir="cloud/web/${module}"

  print_info "📦 开始分层解压：${module}"
  local fat_jar=$(ls "${module_dir}/target/${module}"-*.jar 2>/dev/null | head -n1)
  [[ ! -f "$fat_jar" ]] && { print_error "❌ 未找到 jar：${module_dir}/target/${module}-*.jar"; exit 1; }

  local tmp=$(mktemp -d)
  java -Djarmode=tools -jar "$fat_jar" extract --layers --launcher --destination "$tmp"

  for layer in dependencies spring-boot-loader snapshot-dependencies application; do
    [[ -d "$tmp/$layer" ]] || continue
    local layer_module_dir="${SERVER_ROOT_DIR}/${layer}/${module}"
    mkdir -p "${layer_module_dir}"
    rsync -a --delete --no-compress --inplace --whole-file "$tmp/$layer/" "${layer_module_dir}/"
  done

  [[ -f "$tmp/layers.idx" ]] && cp "$tmp/layers.idx" "${SERVER_ROOT_DIR}/application/${module}/"
  rm -rf "$tmp"
  print_success "✅ 模块 ${module} 分层完成"
}

print_step "📂 开始复制后端构建产物..."
for module in "${BACKEND_MODULES[@]}"; do
  copy_backend_module "${module}" &
done
wait
print_success "后端构建产物整理完毕"

END_TIME=$(date +%s)
BUILD_TIME=$((END_TIME - START_TIME))
print_info "后端构建耗时: ${BUILD_TIME}秒"
print_success "🎉 后端构建完成，输出目录: ${SERVER_OUTPUT_DIR}"
