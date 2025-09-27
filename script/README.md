# 项目部署工具使用指南

## 🚀 快速开始（3步搞定）

### 1️⃣ 构建项目
```bash
  cd script && ./build.sh
```

### 2️⃣ 现场收集信息
```bash
  docker/script/collect-layers.sh
```

### 3️⃣ 生成差异包
```bash
  cd script && ./compare-sync.sh
```

### 4️⃣ 现场应用更新
```bash
  tar -xzf diff-package.tar.gz && cd diff-package && ./apply-diff.sh
```

**就这么简单！🎉**

---

## 📁 工具说明

### 🏗️ build.sh（构建脚本）
**作用**：编译和打包整个项目

**特性**：
- 🚀 并行构建提升速度
- 📦 分层打包优化Docker缓存
- 🎯 自动资源优化
- 🧹 智能清理

### 🔍 collect-layers.sh（收集脚本）
**作用**：收集现场环境的文件指纹

**特性**：
- 🎯 精准模块识别
- 📝 文件指纹记录
- 📦 自动打包压缩

**输出**：
```bash
  docker/script/collect/layers-collection.tar.gz
```

### ⚖️ compare-sync.sh（对比脚本）
**作用**：智能差异分析和打包

**特性**：
- 🔍 自动解压收集包
- 📊 精准差异对比
- 📦 增量更新打包
- 🤖 自动生成安装脚本

**输出**：
```bash
  docker/script/diff/diff-package.tar.gz
```

### 🛠️ apply-diff.sh（安装脚本）
**作用**：现场自动化部署

**特性**：
- 🎯 自动定位项目
- 🗑️ 智能清理旧文件
- 📁 精确文件替换
- ✅ 一键完成部署

---

## 💡 使用场景

### 🔄 完整更新流程
```bash
  # 1. 构建项目
  cd script && ./build.sh

  # 2. 现场收集
  docker/script/collect-layers.sh

  # 3. 生成差异包
  cd script && ./compare-sync.sh

  # 4. 现场部署
  tar -xzf diff-package.tar.gz && cd diff-package && ./apply-diff.sh
```

### 🎯 高级用法
```bash
  # 只更新特定模块
  ./compare-sync.sh --modules web-system-8081

  # 手动指定收集包
  ./compare-sync.sh --layers-collection /path/to/collection/

  # 强制重新构建
  FORCE_REBUILD=true ./build.sh
```

---

## 📂 目录结构
```bash
  sol-cloud/
  ├── script/
  │   ├── build.sh              # 构建脚本
  │   └── compare-sync.sh       # 差异分析脚本
  ├── docker/
  │   ├── script/
  │   │   └── collect-layers.sh # 收集脚本
  │   └── output/               # 构建输出
  │       ├── server/           # 后端分层文件
  │       └── client/           # 前端文件
  ├── cloud/                    # Java后端项目
  └── client/                   # Vue前端项目
```

