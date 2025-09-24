# 部署脚本

# 分层部署差异同步工具

这是一套用于 Spring Boot 分层部署的差异同步工具，包含现场数据收集和差异包生成两个核心脚本。

## 概述

该工具集主要用于解决以下场景：
- **现场环境**：运行生产服务，需要收集当前部署状态
- **开发环境**：基于现场收集的数据，生成最小化的差异更新包
- **差异部署**：仅更新变化的依赖和业务代码，减少部署风险和时间

## 工具组成

### 1. collect-layers.sh - 现场收集脚本
用于在现场环境收集各模块的 `layers.idx` 文件，这些文件记录了 Spring Boot 分层打包的依赖信息。

### 2. compare-sync.sh - 差异生成脚本  
基于收集的现场数据，在开发环境生成差异应用包，包含需要更新的依赖和业务代码。

---

## collect-layers.sh 详细说明

### 功能描述
自动发现并收集所有模块的 `layers.idx` 文件，用于后续的差异分析。

### 使用方法
```bash
# 在项目根目录执行
./script/collect-layers.sh
```

### 工作流程
1. **自动发现模块**：扫描 `docker/output/server` 路径下的所有模块
2. **收集 layers.idx**：查找每个模块的 `application/BOOT-INF/layers.idx` 文件
3. **创建收集包**：将所有收集的文件打包成 `layers-collection-{timestamp}.tar.gz`
4. **输出结果**：压缩包保存在 `script/collect/` 目录下

### 输出结构
```
script/collect/
└── layers-collection-20250918-093742.tar.gz
    └── layers-collection-20250918-093742/
        ├── module1/
        │   └── layers.idx
        ├── module2/
        │   └── layers.idx
        └── ...
```

### 执行示例
```bash
$ ./script/collect-layers.sh

🚀 开始收集现场 layers.idx 文件
📘 项目根目录: /path/to/sol-cloud
📘 临时目录: /path/to/script/layers-collection-20250918-093742
📘 压缩包目录: /path/to/script/collect

🚀 自动发现模块
📘 扫描: docker/output/server
✅ 发现模块: web-system-8081
✅ 发现模块: web-file-8082
✅ 发现模块: gateway-9527

🚀 收集 layers.idx 文件
✅ 收集: web-system-8081/layers.idx (2.1K)
✅ 收集: web-file-8082/layers.idx (1.8K)
✅ 收集: gateway-9527/layers.idx (1.5K)

🚀 创建压缩包
✅ 已创建压缩包: layers-collection-20250918-093742.tar.gz
📘 文件大小: 8.2K

🚀 收集完成
📘 压缩包位置: script/collect/layers-collection-20250918-093742.tar.gz
📘 收集模块数: 3
📘 执行耗时: 2秒
✅ 现场收集任务完成！
```

---

## compare-sync.sh 详细说明

### 功能描述
基于现场收集的 `layers.idx` 文件，生成差异应用包，包含需要更新的依赖jar文件和业务代码。

### 使用方法
```bash
# 基本用法
./script/compare-sync.sh --layers-collection <收集目录路径>

# 带可选参数
./script/compare-sync.sh --layers-collection <收集目录路径> \
  [--spring-boot-upgraded] \
  [--has-snapshot] \
  [--modules module1,module2]
```

### 参数说明
| 参数 | 必需 | 说明 |
|------|------|------|
| `--layers-collection` | ✅ | 现场收集的解压目录路径 |
| `--spring-boot-upgraded` | ❌ | Spring Boot 版本已升级标志 |
| `--has-snapshot` | ❌ | 包含 SNAPSHOT 版本依赖标志 |
| `--modules` | ❌ | 指定处理特定模块，用逗号分隔 |

### 工作流程
1. **解析参数**：验证收集目录和可选参数
2. **发现模块**：扫描收集目录中的所有模块
3. **比较差异**：对比现场和本地的 `layers.idx` 文件
4. **生成差异包**：
   - 复制变更的依赖jar文件
   - 包含完整的 application 层（业务代码）
   - 生成删除列表（多余的依赖）
5. **创建应用脚本**：生成 `apply-diff.sh` 自动化应用脚本

### 输出结构
```
script/diff/
└── diff-package-20250918-094857.tar.gz
    └── diff-package-20250918-094857/
        ├── apply-diff.sh              # 自动应用脚本
        └── modules/
            ├── web-system-8081/
            │   ├── application/       # 业务代码层
            │   ├── files/            # 新增的jar文件
            │   │   ├── spring-boot-starter-web-3.2.1.jar
            │   │   └── ...
            │   └── delete-list.txt   # 需要删除的文件列表
            └── web-file-8082/
                └── ...
```

### 使用示例

#### 1. 基本使用流程
```bash
# 步骤1: 解压现场收集的文件
tar -xzf script/collect/layers-collection-20250918-093742.tar.gz -C /tmp/

# 步骤2: 生成差异包
./script/compare-sync.sh --layers-collection /tmp/layers-collection-20250918-093742/

# 步骤3: 将生成的差异包发送到现场
# script/diff/diff-package-20250918-094857.tar.gz
```

#### 2. 指定特定模块
```bash
./script/compare-sync.sh \
  --layers-collection /tmp/layers-collection-20250918-093742/ \
  --modules web-system-8081,gateway-9527
```

#### 3. 包含可选标志
```bash
./script/compare-sync.sh \
  --layers-collection /tmp/layers-collection-20250918-093742/ \
  --spring-boot-upgraded \
  --has-snapshot
```

### 执行示例
```bash
$ ./script/compare-sync.sh --layers-collection /tmp/layers-collection-20250918-093742/

🚀 开始生成差异包
📘 收集目录: /tmp/layers-collection-20250918-093742/
📘 临时目录: /path/to/script/diff-package-20250918-094857
📘 差异包目录: /path/to/script/diff

🚀 发现收集的模块
✅ 发现模块: web-system-8081
✅ 发现模块: web-file-8082
✅ 发现模块: gateway-9527

🚀 开始处理模块差异

🚀 处理模块: web-system-8081
📘 模块 web-system-8081: 发现 layers.idx 差异
📘 差异: +5 -2
[DEP] 完成，复制 5 个 jar

🚀 处理模块: web-file-8082
✅ 模块 web-file-8082: layers.idx 一致，但仍需更新 application 层

🚀 创建压缩包
✅ 已创建差异包: diff-package-20250918-094857.tar.gz
📘 文件大小: 125M

🚀 生成完成
📘 差异包位置: script/diff/diff-package-20250918-094857.tar.gz
📘 处理模块数: 3
📘 包含模块: web-system-8081 web-file-8082 gateway-9527
✅ 请将差异包发送给现场执行
```

---

## 现场应用差异包

### 应用步骤
1. **接收差异包**：从开发环境获取 `diff-package-*.tar.gz`
2. **解压差异包**：
   ```bash
   tar -xzf diff-package-20250918-094857.tar.gz
   cd diff-package-20250918-094857/
   ```
3. **执行应用脚本**：
   ```bash
   ./apply-diff.sh
   ```

### apply-diff.sh 功能
- **自动定位项目**：向上查找包含 `docker/output` 的项目根目录
- **处理每个模块**：
  - 删除多余的依赖文件
  - 复制新增的jar文件到对应目录
  - 更新 application 层（业务代码）
- **智能分类**：根据jar文件名自动判断是否为SNAPSHOT版本

---

## 最佳实践

### 1. 版本控制
- 保留收集包和差异包，便于问题追溯
- 建议按时间戳命名，避免文件覆盖

### 2. 安全考虑
- 现场收集前先备份重要数据
- 差异应用前测试 `apply-diff.sh` 脚本权限

### 3. 性能优化
- 对于大型项目，可使用 `--modules` 参数只处理变更的模块
- 网络传输时压缩差异包以减少传输时间

### 4. 故障排除
- 检查项目目录结构是否符合预期
- 确认 `docker/output/server` 目录下有对应的模块
- 验证 `layers.idx` 文件格式正确

---

## 目录结构要求

```
sol-cloud/
├── docker/
│   └── output/
│       ├── server/                    # 服务端模块
│       │   ├── web-system-8081/
│       │   │   ├── application/       # 业务代码层
│       │   │   │   └── BOOT-INF/
│       │   │   │       └── layers.idx
│       │   │   ├── dependencies/      # 普通依赖层
│       │   │   │   └── BOOT-INF/lib/
│       │   │   └── snapshot-dependencies/ # SNAPSHOT依赖层
│       │   │       └── BOOT-INF/lib/
│       │   └── ...
│       └── client/                    # 客户端模块（可选）
└── script/
    ├── collect-layers.sh
    ├── compare-sync.sh
    ├── collect/                       # 收集包输出目录
    └── diff/                         # 差异包输出目录
```

---

## 错误处理

### 常见错误及解决方案

1. **未发现任何模块**
   ```
   ❌ 未发现任何模块的 layers.idx 文件
   ```
   - 检查 `docker/output/server` 目录是否存在
   - 确认模块目录结构正确


2. **收集目录不存在**
   ```
   ❌ 收集目录不存在: /path/to/collection
   ```
   - 检查路径是否正确
   - 确认已正确解压收集包


3. **无法找到项目根目录**
   ```
   ❌ 无法找到项目根目录（包含docker/output的目录）
   ```
   - 确认在正确的项目目录下执行脚本
   - 检查项目结构是否完整

---

