# 🚀 CodeFlow

## 📌 项目概述

**CodeFlow** 是一个博客平台，提供用户认证、文章发布与管理等功能。\
项目采用 **前后端分离架构**：前端基于 Vue 3，后端基于 Spring Boot。

## 🛠️ 技术栈

### 🎨 前端技术栈

-   🖼️ **Vue 3** --- 构建交互式界面\
-   🚦 **Vue Router** --- 路由管理\
-   🔗 **Axios** --- 前后端通信\
-   🧰 **Vue CLI** --- 项目构建与管理

### ⚙️ 后端技术栈

-   ☕ **Spring Boot 3.5.6**\
-   🐱 **Java 21**\
-   🗄️ **Spring Data JPA**\
-   🐬 **MySQL 8.0.33** --- 生产数据库\
-   💾 **H2 Database** --- 开发/测试数据库

## ✨ 功能特性

### 👤 用户管理

-   注册\
-   登录\
-   账号管理

### 📝 文章管理

-   列表展示\
-   文章详情\
-   创建文章\
-   编辑文章\
-   删除文章

## 🚀 安装与运行

### 🔧 后端运行

1.  安装 Java 21 与 Maven\

2.  进入后端目录：

    ``` bash
    cd backend/CodeFlow
    ```

3.  构建项目：

    ``` bash
    mvn clean install
    ```

4.  运行：

    ``` bash
    mvn spring-boot:run
    ```

5.  🌐 服务启动于：http://localhost:8080

### 🎨 前端运行

1.  确保安装 Node.js 与 npm\

2.  进入前端目录：

    ``` bash
    cd frontend/code-flow
    ```

3.  安装依赖：

    ``` bash
    npm install
    ```

4.  启动开发服务器：

    ``` bash
    npm run serve
    ```

5.  🌐 前端默认：http://localhost:8081

## ⚙️ 配置说明

### 🗄️ 数据库配置

默认使用 **H2 内存数据库**。\
若需切换到 MySQL，请修改 `application.properties` 中相关配置。

### 🔗 前端 API 配置

API Base URL 默认指向：\
`http://localhost:8080`

## 🧑‍💻 开发指南

### ➕ 添加新功能

1.  后端编写：实体、DTO、Repository、Service、Controller\
2.  前端编写：视图组件 + API 文件\
3.  更新路由配置

### 🧪 测试

-   后端：Spring Boot Test\
-   前端：Jest / Vue Test Utils

## 🤝 贡献

欢迎提交 **Issue** 或 **PR** 来改善本项目！

## 📄 许可证

本项目使用 **MIT License**。
