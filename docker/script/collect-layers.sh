#!/bin/bash

set -euo pipefail

# 现场收集 layers.idx 脚本
# 自动发现并收集所有模块的 layers.idx 文件


# 计算项目根目录 (从 docker/script/ 回到项目根目录)
BASE_DIR="$(cd "$(dirname "$0")"/../.. && pwd)"

# 颜色与日志函数
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
RESET='\033[0m'

print_info()    { echo -e "${BLUE}📘 $1${RESET}"; }
print_success() { echo -e "${GREEN}✅ $1${RESET}"; }
print_error()   { echo -e "${RED}❌ $1${RESET}"; }
print_step()    { echo -e "\n${YELLOW}🚀 $1${RESET}"; }
print_divider() { echo -e "${YELLOW}----------------------------------------${RESET}"; }

# 固定目录名
COLLECTION_DIR="layers-collection"
# docker/script 目录作为临时工作目录
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUTPUT_DIR="$SCRIPT_DIR/$COLLECTION_DIR"
# script/collect目录用于存放最终压缩包（与compare-sync.sh脚本同级）
COLLECT_DIR="$(cd "$(dirname "$0")"/../script && pwd)/collect"

print_step "开始收集 layers.idx 文件"

# 清理并创建目录
rm -rf "$COLLECT_DIR"/* 2>/dev/null || true
mkdir -p "$OUTPUT_DIR" "$COLLECT_DIR"

found_modules=()

# 发现模块
print_step "发现模块"
full_scan_path="$BASE_DIR/docker/output/server"
if [[ -d "$full_scan_path" ]]; then
  while IFS= read -r -d '' layers_file; do
    # 提取模块名：.../application/module-name/BOOT-INF/layers.idx
    module_name=$(basename "$(dirname "$(dirname "$layers_file")")")
    print_success "发现模块: $module_name"
    found_modules+=("$module_name:$layers_file")
  done < <(find "$full_scan_path" -name "layers.idx" -path "*/application/*/BOOT-INF/layers.idx" -print0 2>/dev/null || true)
else
  print_error "构建输出目录不存在: $full_scan_path"
  exit 1
fi

if [[ ${#found_modules[@]} -eq 0 ]]; then
  print_error "未发现任何模块，请先运行构建脚本"
  exit 1
fi

print_info "共发现 ${#found_modules[@]} 个模块"

# 收集文件
print_step "收集文件"
for module_info in "${found_modules[@]}"; do
  module_name="${module_info%%:*}"
  layers_file="${module_info#*:}"
  
  module_output_dir="$OUTPUT_DIR/$module_name"
  mkdir -p "$module_output_dir"
  cp "$layers_file" "$module_output_dir/layers.idx"
  print_info "收集: $module_name"
done

# 创建压缩包
print_step "创建压缩包"
cd "$SCRIPT_DIR"
ARCHIVE_NAME="layers-collection.tar.gz"
tar -czf "$COLLECT_DIR/$ARCHIVE_NAME" "$COLLECTION_DIR"

# 清理临时目录
rm -rf "$OUTPUT_DIR"

print_success "收集完成: $COLLECT_DIR/$ARCHIVE_NAME"
