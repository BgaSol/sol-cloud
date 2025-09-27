#!/bin/bash

set -euo pipefail

# 开发环境差异生成脚本
# 基于现场收集的 layers.idx 文件生成差异应用包
# 用法： script/compare-sync.sh [--layers-collection <path>] [--spring-boot-upgraded] [--has-snapshot] [--modules module1,module2]
# 注意：如果不指定--layers-collection参数，会自动解压docker/script/collect/layers-collection.tar.gz

# 计算项目根目录（脚本所在目录的上一级）
BASE_DIR="$(cd "$(dirname "$0")"/.. && pwd)"

# 参数解析
LAYERS_COLLECTION_DIR=""
SPRING_BOOT_UPGRADED=false
HAS_SNAPSHOT=false
SPECIFIC_MODULES=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --layers-collection)
      shift; LAYERS_COLLECTION_DIR="$1" ;;
    --spring-boot-upgraded)
      SPRING_BOOT_UPGRADED=true ;;
    --has-snapshot)
      HAS_SNAPSHOT=true ;;
    --modules)
      shift; SPECIFIC_MODULES="$1" ;;
    *)
      echo -e "${RED}❌ 未知参数: $1${RESET}"
      exit 1
      ;;
  esac
  shift || true
done

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

# 自动查找并解压收集的文件
if [[ -z "$LAYERS_COLLECTION_DIR" ]]; then
  AUTO_ARCHIVE="$BASE_DIR/docker/script/collect/layers-collection.tar.gz"
  ALT_ARCHIVE="$(dirname "$0")/../docker/script/collect/layers-collection.tar.gz"
  
  if [[ -f "$AUTO_ARCHIVE" ]]; then
    ARCHIVE="$AUTO_ARCHIVE"
  elif [[ -f "$ALT_ARCHIVE" ]]; then
    ARCHIVE="$ALT_ARCHIVE"
  else
    print_error "未找到压缩包，请先运行 docker/script/collect-layers.sh"
    exit 2
  fi
  
  COLLECT_DIR="$(dirname "$ARCHIVE")"
  tar -xzf "$ARCHIVE" -C "$COLLECT_DIR"
  LAYERS_COLLECTION_DIR="$COLLECT_DIR/layers-collection"
fi

if [[ ! -d "$LAYERS_COLLECTION_DIR" ]]; then
  print_error "收集目录不存在: $LAYERS_COLLECTION_DIR"
  exit 2
fi

# 生成输出目录
DIFF_PACKAGE_DIR="diff-package"
# diff目录用于存放最终压缩包（与collect同级）
DIFF_DIR="$BASE_DIR/docker/script/diff"
# 临时工作目录也放在docker/script/diff下
OUTPUT_DIR="$DIFF_DIR/$DIFF_PACKAGE_DIR"
# 压缩包固定名称
ARCHIVE_NAME="diff-package.tar.gz"

print_step "开始生成差异包"

# 创建工作目录
rm -rf "$OUTPUT_DIR" 2>/dev/null || true
mkdir -p "$OUTPUT_DIR/modules" "$OUTPUT_DIR/client" "$DIFF_DIR"

# 发现模块
print_step "发现模块"
collected_modules=()

for module_dir in "$LAYERS_COLLECTION_DIR"/*/; do
  [[ -d "$module_dir" ]] || continue
  module_name=$(basename "$module_dir")
  
  # 跳过无效模块
  [[ "$module_name" =~ ^(BOOT-INF|META-INF|\.|\..)$ ]] && continue
  [[ -f "$module_dir/layers.idx" ]] || continue
  
  # 检查模块过滤
  if [[ -n "$SPECIFIC_MODULES" && ",$SPECIFIC_MODULES," != *",$module_name,"* ]]; then
    continue
  fi
  
  collected_modules+=("$module_name")
  print_info "发现模块: $module_name"
done

[[ ${#collected_modules[@]} -gt 0 ]] || { print_error "未发现任何模块"; exit 1; }

# === 分类处理函数 ===
process_dependencies() {
  local diff_list_file="$1" module_name="$2" module_output_dir="$3"
  local diff_files_dir="$module_output_dir/files"
  mkdir -p "$diff_files_dir"

  local copied=0
  while IFS= read -r entry || [[ -n "$entry" ]]; do
    entry="${entry%$'\r'}"; entry="${entry#"${entry%%[![:space:]]*}"}"
    entry="${entry#- }"; entry="${entry#-}"; entry="${entry#- }"
    entry="${entry#\"}"; entry="${entry%\"}"
    entry="${entry%"${entry##*[![:space:]]}"}"

    case "$entry" in
      BOOT-INF/lib/*.jar)
        # 从server的分层结构中查找对应的jar文件
        local dependencies_dir="$BASE_DIR/docker/output/server/dependencies/$module_name"
        local src_path="$dependencies_dir/$entry"
        if [[ -f "$src_path" ]]; then
          local rel_name="${entry#BOOT-INF/lib/}"
          local dst_path="$diff_files_dir/$rel_name"
          mkdir -p "$(dirname "$dst_path")"
          cp -f "$src_path" "$dst_path"
          echo "[DEP] $entry -> $dst_path"
          copied=$((copied+1))
        else
          echo "[WARN][DEP] 缺少源文件: $src_path"
        fi
        ;;
    esac
  done < "$diff_list_file"
  echo "[DEP] 完成，复制 $copied 个 jar"
}


# === 主流程 - 处理所有收集的模块 ===
print_step "开始处理模块差异"

# 初始化apply-diff.sh脚本
apply_script="$OUTPUT_DIR/apply-diff.sh"
cat > "$apply_script" <<'EOF'
#!/bin/bash

set -e
shopt -s nullglob

# 现场差异应用脚本 - 自动生成
# 包含所有模块的差异应用操作

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

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

print_step "=== 现场差异应用脚本 ==="
print_info "生成时间: $(date)"
print_info "脚本位置: $SCRIPT_DIR"
print_divider

EOF
chmod +x "$apply_script"

processed_modules=0

# 处理每个收集的模块
for module_name in "${collected_modules[@]}"; do
  print_step "处理模块: $module_name"
  
  # 现场收集的layers.idx
  collected_layers_idx="$LAYERS_COLLECTION_DIR/$module_name/layers.idx"
  
  # 本地模块的layers.idx (新分层结构中在application层的BOOT-INF下)
  local_application_dir="$BASE_DIR/docker/output/server/application/$module_name"
  local_layers_idx="$local_application_dir/BOOT-INF/layers.idx"
  
  if [[ ! -f "$local_layers_idx" ]]; then
    print_error "本地缺少 layers.idx: $local_layers_idx"
    continue
  fi
  
  # 创建模块输出目录
  module_output_dir="$OUTPUT_DIR/modules/$module_name"
  mkdir -p "$module_output_dir"
  
  # 比较layers.idx
  tmp_new_sorted=$(mktemp)
  tmp_collected_sorted=$(mktemp)
  trap 'rm -f "$tmp_new_sorted" "$tmp_collected_sorted"' EXIT
  
  sed -e 's/\r$//' -e 's/^\s\+//;s/\s\+$//' -e '/^$/d' "$local_layers_idx" | sort -u > "$tmp_new_sorted"
  sed -e 's/\r$//' -e 's/^\s\+//;s/\s\+$//' -e '/^$/d' "$collected_layers_idx" | sort -u > "$tmp_collected_sorted"
  
  # 复制application层
  if [[ -d "$local_application_dir" ]]; then
    mkdir -p "$module_output_dir/application"
    (cd "$BASE_DIR" && rsync -a --delete "$local_application_dir/" "$module_output_dir/application/")
  else
    print_error "模块 $module_name 缺少 application 目录"
    continue
  fi
  
  # 初始化差异文件变量
  only_in_new=$(mktemp)
  only_in_collected=$(mktemp)
  has_dependency_diff=false
  
  # 比较 layers.idx 处理依赖差异
  if cmp -s "$tmp_new_sorted" "$tmp_collected_sorted"; then
    print_success "模块 $module_name: layers.idx 一致，但仍需更新 application 层"
    # 创建空的差异文件
    > "$only_in_new"
    > "$only_in_collected"
  else
    print_info "模块 $module_name: 发现 layers.idx 差异"
    comm -13 "$tmp_collected_sorted" "$tmp_new_sorted" > "$only_in_new"
    comm -23 "$tmp_collected_sorted" "$tmp_new_sorted" > "$only_in_collected"
    has_dependency_diff=true
    
    print_info "差异: +$(wc -l < "$only_in_new") -$(wc -l < "$only_in_collected")"
    
    # 处理新增的依赖文件
    if [[ -s "$only_in_new" ]]; then
      process_dependencies "$only_in_new" "$module_name" "$module_output_dir"
    fi
    
    # 生成删除列表
    if [[ -s "$only_in_collected" ]]; then
      cp "$only_in_collected" "$module_output_dir/delete-list.txt"
    fi
  fi
  
  # 添加到apply脚本
  cat >> "$apply_script" <<EOF

# === 模块: $module_name ===
print_step "处理模块: $module_name"

# 查找项目根目录（通过查找特征目录来确定）
print_info "当前脚本目录: \$SCRIPT_DIR"

# 向上查找项目根目录，直到找到 docker 目录
current_dir="\$SCRIPT_DIR"
PROJECT_ROOT=""
for i in {1..5}; do
  parent_dir="\$(dirname "\$current_dir")"
  if [[ -d "\$parent_dir/docker" && -d "\$parent_dir/docker/output" ]]; then
    PROJECT_ROOT="\$parent_dir"
    break
  fi
  current_dir="\$parent_dir"
done

if [[ -z "\$PROJECT_ROOT" ]]; then
  print_error "无法找到项目根目录（包含docker/output的目录）"
  print_info "当前脚本路径: \$SCRIPT_DIR"
  print_info "查找范围: 向上5级目录"
  exit 1
fi

print_info "找到项目根目录: \$PROJECT_ROOT"

SERVER_ROOT="\$PROJECT_ROOT/docker/output/server"
if [[ ! -d "\$SERVER_ROOT" ]]; then
  print_error "未找到服务端输出目录: \$SERVER_ROOT"
  print_info "项目根目录: \$PROJECT_ROOT"
  exit 1
fi

print_info "服务端根目录: \$SERVER_ROOT"

EOF

  # 添加删除操作
  if [[ -s "$only_in_collected" ]]; then
    cat >> "$apply_script" <<'EOF'
# 删除多余文件
if [[ -f "$SCRIPT_DIR/modules/EOF
    echo "$module_name/delete-list.txt" >> "$apply_script"
    cat >> "$apply_script" <<'EOF'
" ]]; then
  print_info "删除多余文件..."
  while IFS= read -r file_path; do
    file_path="${file_path#- }"; file_path="${file_path#-}"; file_path="${file_path#\"}"; file_path="${file_path%\"}"
    file_path="${file_path%"${file_path##*[![:space:]]}"}"
    if [[ -n "$file_path" ]]; then
      # 根据文件路径确定在哪个分层目录中删除
      if [[ "\$file_path" == BOOT-INF/lib/* ]]; then
        if [[ "\$file_path" == *SNAPSHOT* ]]; then
          rm -f "\$SERVER_ROOT/snapshot-dependencies/$module_name/\$file_path" || true
        else
          rm -f "\$SERVER_ROOT/dependencies/$module_name/\$file_path" || true
        fi
        print_info "删除: \$file_path"
      fi
    fi
EOF
    echo "  done < \"\$SCRIPT_DIR/modules/$module_name/delete-list.txt\"" >> "$apply_script"
    cat >> "$apply_script" <<'EOF'
fi

EOF
  fi
  
  # 添加复制操作
  cat >> "$apply_script" <<EOF
# 复制新增jar文件
if [[ -d "\$SCRIPT_DIR/modules/$module_name/files" ]]; then
  print_info "复制新增jar文件..."
  for jar_file in "\$SCRIPT_DIR/modules/$module_name/files"/*.jar; do
    [[ -f "\$jar_file" ]] || continue
    jar_name=\$(basename "\$jar_file")
    
    # 根据文件名决定目标目录（新分层结构）
    if [[ "\$jar_name" == *SNAPSHOT* ]]; then
      target_dir="\$SERVER_ROOT/snapshot-dependencies/$module_name/BOOT-INF/lib"
    else
      target_dir="\$SERVER_ROOT/dependencies/$module_name/BOOT-INF/lib"
    fi
    
    mkdir -p "\$target_dir"
    cp -f "\$jar_file" "\$target_dir/\$jar_name"
    print_info "复制: \$jar_name -> \$target_dir/"
  done
fi

# 更新 application 层（业务代码）
if [[ -d "\$SCRIPT_DIR/modules/$module_name/application" ]]; then
  print_info "更新 application 层..."
  mkdir -p "\$SERVER_ROOT/application/$module_name"
  rsync -a --delete "\$SCRIPT_DIR/modules/$module_name/application/" "\$SERVER_ROOT/application/$module_name/"
  print_success "application 层更新完成"
fi

print_success "模块 $module_name 处理完成"
print_divider

EOF
  
  processed_modules=$((processed_modules + 1))
  rm -f "$only_in_new" "$only_in_collected"
done

# 复制前端代码
print_step "复制前端代码"
CLIENT_SOURCE_DIR="$BASE_DIR/docker/output/client"
if [[ -d "$CLIENT_SOURCE_DIR" ]]; then
  mkdir -p "$OUTPUT_DIR/client"
  (cd "$BASE_DIR" && rsync -a --delete "$CLIENT_SOURCE_DIR/" "$OUTPUT_DIR/client/")
fi

# 完成apply脚本
cat >> "$apply_script" <<EOF

print_step "=== 所有模块处理完成 ==="
print_success "共处理 $processed_modules 个服务端模块"
print_info "如需验证结果，请检查各模块的jar文件是否正确更新"

# === 处理前端代码 ===
print_step "处理前端代码"

if [[ -d "\$SCRIPT_DIR/client" ]]; then
  print_info "发现前端代码，开始更新..."
  
  # 查找项目根目录中的client目录
  CLIENT_TARGET_DIR=""
  if [[ -d "\$PROJECT_ROOT/docker/output/client" ]]; then
    CLIENT_TARGET_DIR="\$PROJECT_ROOT/docker/output/client"
  elif [[ -d "\$PROJECT_ROOT/client" ]]; then
    CLIENT_TARGET_DIR="\$PROJECT_ROOT/client"
  fi
  
  if [[ -n "\$CLIENT_TARGET_DIR" ]]; then
    print_info "目标client目录: \$CLIENT_TARGET_DIR"
    
    # 删除现有client目录
    if [[ -d "\$CLIENT_TARGET_DIR" ]]; then
      print_info "删除现有client目录: \$CLIENT_TARGET_DIR"
      rm -rf "\$CLIENT_TARGET_DIR"
    fi
    
    # 复制新的client目录
    print_info "复制新的client目录..."
    mkdir -p "\$(dirname "\$CLIENT_TARGET_DIR")"
    cp -r "\$SCRIPT_DIR/client" "\$CLIENT_TARGET_DIR"
    print_success "client目录更新完成"
  else
    print_error "未找到目标client目录位置"
    print_info "请手动将 \$SCRIPT_DIR/client 复制到正确位置"
  fi
else
  print_info "差异包中未包含client目录，跳过处理"
fi

EOF

# 创建压缩包
print_step "创建压缩包"
cd "$DIFF_DIR"
tar -czf "$ARCHIVE_NAME" "$DIFF_PACKAGE_DIR"
rm -rf "$OUTPUT_DIR"

print_success "差异包已生成: $DIFF_DIR/$ARCHIVE_NAME"
