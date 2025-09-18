#!/bin/bash

set -euo pipefail

# 现场收集 layers.idx 脚本
# 自动发现并收集所有模块的 layers.idx 文件

# 记录开始时间
START_TIME=$(date +%s)

# 计算项目根目录
BASE_DIR="$(cd "$(dirname "$0")"/.. && pwd)"

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

# 生成时间戳
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
COLLECTION_DIR="layers-collection-$TIMESTAMP"
# 脚本同级目录作为临时工作目录
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
OUTPUT_DIR="$SCRIPT_DIR/$COLLECTION_DIR"
# collect目录用于存放最终压缩包
COLLECT_DIR="$SCRIPT_DIR/collect"

print_step "开始收集现场 layers.idx 文件"
print_info "项目根目录: $BASE_DIR"
print_info "临时目录: $OUTPUT_DIR"
print_info "压缩包目录: $COLLECT_DIR"

# 创建输出目录和collect目录
mkdir -p "$OUTPUT_DIR"
mkdir -p "$COLLECT_DIR"

# 扫描路径列表
SCAN_PATHS=(
  "docker/output/server"
)

found_modules=()

# 自动发现模块
print_step "自动发现模块"
for scan_path in "${SCAN_PATHS[@]}"; do
  full_scan_path="$BASE_DIR/$scan_path"
  if [[ -d "$full_scan_path" ]]; then
    print_info "扫描: $scan_path"
    
    # 查找包含 layers.idx 的模块
    while IFS= read -r -d '' layers_file; do
      # 提取模块路径 (从 layers.idx 往上找到模块根目录)
      # 路径格式: .../module-name/application/BOOT-INF/layers.idx
      module_dir=$(dirname "$(dirname "$(dirname "$layers_file")")")
      module_name=$(basename "$module_dir")
      
      print_success "发现模块: $module_name"
      found_modules+=("$module_name:$layers_file")
      
    done < <(find "$full_scan_path" -name "layers.idx" -path "*/application/BOOT-INF/layers.idx" -print0 2>/dev/null || true)
  else
    print_info "跳过不存在的路径: $scan_path"
  fi
done

if [[ ${#found_modules[@]} -eq 0 ]]; then
  print_error "未发现任何模块的 layers.idx 文件"
  print_info "请检查以下路径是否存在模块:"
  for scan_path in "${SCAN_PATHS[@]}"; do
    echo "  - $scan_path/*/application/BOOT-INF/layers.idx"
  done
  exit 1
fi

print_divider
print_info "共发现 ${#found_modules[@]} 个模块:"
for module_info in "${found_modules[@]}"; do
  module_name="${module_info%%:*}"
  echo "  - $module_name"
done

# 收集 layers.idx 文件
print_step "收集 layers.idx 文件"
collected_count=0

for module_info in "${found_modules[@]}"; do
  module_name="${module_info%%:*}"
  layers_file="${module_info#*:}"
  
  # 创建模块目录
  module_output_dir="$OUTPUT_DIR/$module_name"
  mkdir -p "$module_output_dir"
  
  # 复制 layers.idx 文件
  if [[ -f "$layers_file" ]]; then
    cp "$layers_file" "$module_output_dir/layers.idx"
    # 显示文件大小
    file_size=$(du -h "$layers_file" | cut -f1)
    print_success "收集: $module_name/layers.idx ($file_size)"
    collected_count=$((collected_count + 1))
  else
    print_error "文件不存在: $layers_file"
  fi
done


# 验证收集结果
if [[ $collected_count -eq 0 ]]; then
  print_error "没有成功收集任何文件"
  rm -rf "$OUTPUT_DIR"
  exit 1
fi

# 创建压缩包
print_step "创建压缩包"
cd "$SCRIPT_DIR"
if tar -czf "$COLLECT_DIR/$COLLECTION_DIR.tar.gz" "$COLLECTION_DIR"; then
  print_success "已创建压缩包: $COLLECTION_DIR.tar.gz"
else
  print_error "创建压缩包失败"
  rm -rf "$OUTPUT_DIR"
  exit 1
fi

# 显示文件大小
file_size=$(du -h "$COLLECT_DIR/$COLLECTION_DIR.tar.gz" | cut -f1)
print_info "文件大小: $file_size"

# 清理临时目录
rm -rf "$OUTPUT_DIR"
print_info "已清理临时目录"

print_divider
# 计算执行时间
END_TIME=$(date +%s)
EXEC_TIME=$((END_TIME - START_TIME))

print_step "收集完成"
print_info "压缩包位置: $COLLECT_DIR/$COLLECTION_DIR.tar.gz"
print_info "收集模块数: $collected_count"
print_info "执行耗时: ${EXEC_TIME}秒"
print_info "请将此文件发送给开发人员进行差异分析"

print_success "现场收集任务完成！"
