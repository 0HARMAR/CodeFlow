# 🚀 CodeFlow

## 📌 项目概述

**CodeFlow** 是一个博客平台，提供用户认证、文章发布与管理、AI 助手等功能。\
项目采用 **前后端分离 + 微服务架构**：前端基于 Vue 3，后端基于 Spring Boot 3。

## 🏗️ 架构

```
浏览器 ── 8080 ──> Gateway（Spring Cloud Gateway）
                    ├── /api/users/**, /uploads/**   ──> user-service    :8081
                    ├── /api/articles/**, /api/tags/**, /api/comments/**,
                    │   /api/read/**, /api/recommend/** ──> content-service :8082
                    └── /api/agent/**, /api/chat     ──> agent-service   :8083
```

| 服务 | 端口 | 职责 |
|---|---|---|
| **gateway** | 8080 | 统一入口，按路径转发（前端只认 8080） |
| **user-service** | 8081 | 用户注册/登录、账号管理、头像上传 |
| **content-service**（原单体 `backend/CodeFlow`） | 8082 | 文章、标签、评论、阅读统计、搜索、推荐 |
| **agent-service** | 8083 | AI 助手（Nijika/Nazuna）、聊天、会话历史（工具通过 HTTP 调用其他服务） |

**共享基础设施**：三个业务服务共享同一个 MySQL 库（各自只维护自己的表）与 Redis（浏览计数、用户偏好）。JWT 密钥三处一致，任意服务均可校验 token。

## 🛠️ 技术栈

### 🎨 前端技术栈

-   🖼️ **Vue 3** + Vue Router + Axios + Vue CLI

### ⚙️ 后端技术栈

-   ☕ **Spring Boot 3.5.6**、**Java 21**、Spring Data JPA
-   🌉 **Spring Cloud Gateway 2025.0.x**
-   🐬 **MySQL 8.0.33**（生产）/ 💾 **H2**（开发/测试）
-   🔑 JWT（jjwt）、🔐 Spring Security、🟥 Redis

## ✨ 功能特性

### 👤 用户管理
注册、登录、账号管理

### 📝 文章管理
列表、详情、创建、编辑、删除；标签、评论、阅读统计、搜索、个性化推荐

### 🤖 AI 助手
Nijika（虹夏）/ Nazuna（七草荠）双人格对话，可搜索/推荐/统计文章

## 🚀 安装与运行

1.  安装 Java 21、Maven、Node.js，并确保本机 MySQL（`codeflow` 库）与 Redis 已启动
2.  一键启动（网关 + 3 个服务 + 前端）：

    ``` powershell
    ./start.ps1
    ```

3.  或逐个启动：

    ``` bash
    cd gateway && mvn spring-boot:run            # 8080
    cd backend/user-service && mvn spring-boot:run   # 8081
    cd backend/CodeFlow && mvnw spring-boot:run      # 8082
    cd backend/agent-service && mvn spring-boot:run  # 8083
    cd frontend/code-flow && npm run serve           # 80
    ```

## ⚙️ 配置说明

### 🗄️ 数据库配置

默认使用 **H2 内存数据库**。若需切换到 MySQL，请修改各服务 `application.properties` 中相关配置（三个服务连接同一个 `codeflow` 库）。

### 🔗 服务间调用

`agent-service` 通过 HTTP 调用其他服务，目标地址配置在 `application.properties`：

```
service.url.content=http://localhost:8082
service.url.user=http://localhost:8081
```

### 🔑 JWT 与密钥

`jwt.secret` 在三个服务中必须保持一致。DeepSeek API Key 位于各服务 `secrets.properties`（不入库、不提交）。

## 🧑‍💻 开发指南

### ➕ 添加新功能

1.  判断功能归属域（用户 / 内容 / Agent），在对应服务中编写：实体、DTO、Repository、Service、Controller
2.  前端编写：视图组件 + API 文件
3.  跨域调用走网关：新端点加入 `gateway/src/main/resources/application.yml` 路由表

### 🧪 测试

-   后端：Spring Boot Test（各服务独立测试）
-   前端：Jest / Vue Test Utils

## 📄 许可证

本项目使用 **MIT License**。
