# Sol-Cloud

## 项目介绍

Sol-Cloud是一个基于微服务架构的云服务平台，采用前后端分离设计模式，提供系统管理和文件管理等核心功能。该项目使用Docker容器化技术进行部署，具有高可用性、可扩展性和安全性。

## 系统架构

### 整体架构

项目分为前端（client）和后端（cloud）两大部分：

- **前端**：基于Vue 3的单页面应用
- **后端**：基于Spring Cloud的微服务集群
- **部署**：基于Docker的容器化部署

### 微服务架构

- **API网关**：统一入口，请求路由和过滤
- **系统服务**：用户、角色、权限等系统管理功能
- **文件服务**：文件上传、下载和管理功能
- **公共模块**：工具类、常量、基础模型等共享组件

## 技术栈

### 前端技术栈

- **核心框架**：Vue 3.5.12
- **UI组件库**：Element Plus 2.8.8
- **构建工具**：Vite 5.4.10
- **编程语言**：TypeScript 5.6.3
- **HTTP客户端**：Axios 1.7.7
- **状态管理**：Pinia 2.2.6
- **路由管理**：Vue Router 4.4.5

### 后端技术栈

- **核心框架**：Spring Boot 3.3.4
- **微服务框架**：Spring Cloud 2023.0.3
- **微服务组件**：Spring Cloud Alibaba 2023.0.1.0
- **ORM框架**：MyBatis Plus 3.5.10.1
- **认证框架**：Sa-Token 1.39.0
- **服务注册与配置中心**：Nacos 2.5.0
- **数据库**：PostgreSQL 17.2
- **缓存**：Redis 7.4.2
- **对象存储**：MinIO RELEASE.2024-11-07T00-52-20Z

## 项目结构

### 前端结构

```
client/
├── src/
│   ├── api/          # API接口定义
│   ├── assets/       # 静态资源
│   ├── components/   # 可复用组件
│   ├── composables/  # 组合式API
│   ├── generated/    # 自动生成的代码
│   ├── pinia/        # 状态管理
│   ├── router/       # 路由配置
│   ├── styles/       # 样式文件
│   ├── util/         # 工具函数
│   └── views/        # 页面视图组件
├── public/           # 公共静态资源
├── index.html        # 入口HTML
└── vite.config.ts    # Vite配置
```

### 后端结构

```
cloud/
├── common/                 # 公共模块
│   ├── common-auth/        # 认证授权相关
│   ├── common-base-model/  # 基础数据模型
│   ├── common-base-web/    # Web基础组件
│   ├── common-constant/    # 常量定义
│   └── common-util/        # 通用工具类
├── model/                  # 数据模型定义
├── plugin/                 # 插件模块
├── web/                    # 业务服务模块
│   ├── web-system-8081/    # 系统管理服务
│   └── web-file-8082/      # 文件管理服务
└── gateway-9527/           # API网关
```

## 部署指南

### 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- JDK 17+
- Node.js 20+

### 开发环境部署

1. 克隆代码仓库
   ```bash
   git clone https://github.com/yourusername/sol-cloud.git
   cd sol-cloud
   ```

2. 启动后端服务
   ```bash
   cd docker
   docker-compose -f dev.docker-compose.yml up -d
   ```
   > system和file需要手动启动

3. 启动前端开发服务器
   ```bash
   cd client
   npm install
   npm run dev
   ```

### 生产环境部署

1. 启动所有服务
   ```bash
   docker-compose up -d
   ```

2. 访问系统
   ```
   http://localhost:8080
   ```

3. 安全注意事项
   > **警告**：生产环境部署时，请注意以下安全事项：
   > - 不要将内部服务组件（如Nacos、Redis、PostgreSQL、MinIO等）直接暴露到公网
   > - 确保只有API网关和前端应用对外开放访问
   > - 使用防火墙限制内部服务的访问来源
   > - 所有对外服务应启用HTTPS加密传输
   > - 定期更新密码和密钥
   > - 建议使用反向代理（如Nginx）作为外部访问的唯一入口

## 功能特性

- [x] 用户认证与授权管理
- [x] 角色与权限管理
- [x] 文件上传与管理
- [x] 系统配置管理
- [ ] 日志审计

## 开发指南

### 添加新微服务

1. 在cloud目录下创建新的服务模块
2. 在docker-compose.yml中添加新服务配置
3. 在网关配置中添加新服务路由

### 前端开发

1. 在src/api中添加新的API接口
2. 在src/views中创建新的页面组件
3. 在src/router中配置新的路由

## 贡献指南

1. Fork本仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个Pull Request

## 许可证

本项目采用 [LICENSE](LICENSE) 许可证。 