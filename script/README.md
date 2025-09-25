# 项目部署工具使用指南

---

## 🚀 快速开始（3步搞定）

### 第一步：现场收集信息
在现场服务器上运行收集脚本：
```bash
  docker/script/collect-layers.sh
```
这会生成一个压缩包：`docker/script/collect/layers-collection.tar.gz`

### 第二步：开发环境生成差异包
把压缩包拿回开发环境，然后运行：
```bash
  cd script
  ./compare-sync.sh
```
这会生成差异包：`docker/script/diff/diff-package.tar.gz`

### 第三步：现场应用更新
把差异包带到现场，解压并运行：
```bash
  tar -xzf diff-package.tar.gz
  cd diff-package
  ./apply-diff.sh
```

**就这么简单！🎉**

---

## 📁 工具说明

### 🔍 collect-layers.sh（收集脚本）
**作用**：告诉我现场环境都有什么文件

**什么时候用**：需要更新现场环境之前

**怎么用**：
```bash
  # 在现场服务器上运行
  docker/script/collect-layers.sh
```

**会发生什么**：
1. 自动扫描所有服务模块
2. 记录每个模块的文件信息
3. 打包成一个压缩文件
4. 你就得到了"现场环境清单"

**输出结果**：
```
docker/script/collect/
├── layers-collection.tar.gz    ← 这个文件带回每个模块的文件信息
└── 生成于xxx时间戳文件           ← 记录生成时间
```

### ⚖️ compare-sync.sh（对比脚本）
**作用**：对比现场和开发环境，找出差异

**什么时候用**：在开发环境，拿到现场收集包之后

**怎么用**：
```bash
  cd script
  ./compare-sync.sh
```

**会发生什么**：
1. 自动找到现场收集包并解压
2. 对比现场和本地的文件差异
3. 把需要更新的文件打包
4. 生成自动安装脚本
5. 你就得到了"差异更新包"

**输出结果**：
```
docker/script/diff/
├── diff-package/              ← 临时文件夹
│   ├── apply-diff.sh         ← 自动安装脚本
│   ├── modules/              ← 服务端更新文件
│   └── client/               ← 前端更新文件
└── diff-package.tar.gz       ← 这个文件带到现场
```

### 🛠️ apply-diff.sh（安装脚本）
**作用**：在现场自动安装更新

**什么时候用**：在现场环境，解压差异包之后

**怎么用**：
```bash
  ./apply-diff.sh
```

**会发生什么**：
1. 自动找到项目位置
2. 删除不需要的旧文件
3. 复制新文件到正确位置
4. 更新前端文件
5. 完成！服务器就更新好了

---

## 💡 实际使用场景

### 场景1：普通更新
```bash
  # 现场：收集信息
  docker/script/collect-layers.sh

  # 开发：生成差异包
  cd script && ./compare-sync.sh

  # 现场：应用更新
  tar -xzf diff-package.tar.gz && cd diff-package && ./apply-diff.sh
```

### 场景2：只更新特定模块
```bash
  # 只更新web-system-8081模块
  ./compare-sync.sh --modules web-system-8081
```

### 场景3：手动指定收集包位置
```bash
  # 如果收集包在其他地方
  ./compare-sync.sh --layers-collection /path/to/your/collection/
```

---

## 📂 项目目录结构

你的项目应该长这样：
```
sol-cloud/
├── docker/
│   ├── script/
│   │   ├── collect-layers.sh     ← 收集脚本
│   │   ├── collect/              ← 收集包存放处
│   │   └── diff/                 ← 差异包存放处
│   └── output/
│       ├── server/               ← 后端服务文件
│       └── client/               ← 前端文件
└── script/
    └── compare-sync.sh           ← 对比脚本
```

---

## 🎯 工作原理

1. **收集阶段**：记录现场每个模块的"文件指纹"
2. **对比阶段**：把现场指纹和开发环境指纹做对比
3. **打包阶段**：把有差异的文件打包，生成安装脚本
4. **应用阶段**：在现场自动替换文件

这样做的好处：
- 🚀  **快**：只传输变化的文件
- 🎯  **准**：自动识别需要更新的内容  
- 🛡️   **稳**：最小化更新风险

