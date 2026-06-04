# StudyMate：AI 学习复盘与 Java 实习备考陪伴系统

StudyMate 是一个面向 Java 实习备考人群的 AI 学习复盘与情绪陪伴系统。用户每天学习结束后，可以用自然语言记录学习情况，系统后续会通过 AI 自动整理学习内容、提取薄弱点、分析情绪状态，并生成明日建议和安慰反馈。

当前项目采用后端优先策略，先完成稳定的后端接口、数据权限和核心业务闭环，再集中开发前端页面。

## 项目目标

第一版重点完成一个真实可用的学习复盘闭环：

1. 用户注册登录。
2. 用户输入自然语言学习记录。
3. 后端调用 AI 整理记录。
4. 用户确认并保存结构化学习记录。
5. 系统统计学习时长、学习方向和薄弱点。
6. 系统返回轻量、具体、低压力的明日建议和安慰反馈。

## 当前进度

已完成：

1. Day 1：后端基础骨架。
2. Day 2：通用返回结果、全局异常处理、基础配置、用户基础实体和 Mapper。
3. Day 3：用户注册、用户登录、BCrypt 密码加密、JWT Token 签发、认证接口测试。

暂未完成：

1. 完整 JWT 鉴权拦截。
2. 获取当前登录用户接口。
3. 学习记录 CRUD。
4. AI 学习记录解析。
5. 薄弱点统计和学习数据统计。
6. 前端页面。

## 技术栈

后端：

1. Java 17
2. Spring Boot 3
3. Spring Web
4. MyBatis Plus
5. MySQL 8
6. Spring Security Crypto
7. JWT
8. Lombok
9. Hibernate Validator
10. Swagger / Knife4j
11. Maven

前端规划：

1. Vue 3
2. Vite
3. TypeScript
4. Vue Router
5. Pinia
6. Axios
7. Element Plus
8. ECharts

## 项目结构

```text
studyRecord
├── AGENTS.md
├── PRD.md
├── TECH_DESIGN.md
├── VIBECODING_PLAN.md
├── README.md
├── NOTES.md
└── studymate-backend
    ├── pom.xml
    └── src
        ├── main
        │   ├── java/com/studymate
        │   │   ├── common
        │   │   ├── config
        │   │   ├── security
        │   │   ├── module
        │   │   └── enums
        │   └── resources
        └── test
```

## 后端模块说明

当前后端已包含：

1. `common`：统一响应结果和全局异常处理。
2. `config`：密码加密、Swagger / Knife4j 等配置。
3. `security`：JWT 配置和 Token 签发工具。
4. `module/health`：健康检查接口。
5. `module/user`：用户实体、注册登录 DTO/VO、认证 Controller 和 Service。

## 已实现接口

### 健康检查

```http
GET /api/health
```

### 用户注册

```http
POST /api/auth/register
Content-Type: application/json
```

请求示例：

```json
{
  "username": "java_rookie",
  "email": "java_rookie@example.com",
  "password": "StudyMate123"
}
```

成功响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "java_rookie",
    "email": "java_rookie@example.com",
    "nickname": null,
    "avatarUrl": null,
    "targetPosition": null,
    "dailyTargetMinutes": 120,
    "studyStage": null
  }
}
```

说明：注册成功不会返回 `password` 字段，数据库中的密码使用 BCrypt 加密保存。

### 用户登录

```http
POST /api/auth/login
Content-Type: application/json
```

请求示例：

```json
{
  "usernameOrEmail": "java_rookie",
  "password": "StudyMate123"
}
```

成功响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "JWT_TOKEN",
    "userInfo": {
      "id": 1,
      "username": "java_rookie",
      "email": "java_rookie@example.com",
      "nickname": null,
      "avatarUrl": null,
      "targetPosition": null,
      "dailyTargetMinutes": 120,
      "studyStage": null
    }
  }
}
```

## 本地运行

进入后端目录：

```bash
cd studymate-backend
```

确认 MySQL 已创建并可连接 `studymate` 数据库，默认开发配置位于：

```text
studymate-backend/src/main/resources/application-dev.yml
```

开发环境数据库连接通过环境变量配置，避免把本地密码提交到仓库：

```bash
set STUDYMATE_DB_URL=jdbc:mysql://localhost:3306/studymate?useUnicode=true^&characterEncoding=utf8^&serverTimezone=Asia/Shanghai^&useSSL=false^&allowPublicKeyRetrieval=true
set STUDYMATE_DB_USERNAME=root
set STUDYMATE_DB_PASSWORD=你的本地数据库密码
```

启动项目：

```bash
mvn spring-boot:run
```

访问接口文档：

```text
http://localhost:8080/doc.html
http://localhost:8080/swagger-ui.html
```

## 运行测试

```bash
cd studymate-backend
mvn test
```

当前测试覆盖：

1. 统一异常响应。
2. 健康检查接口。
3. 用户注册成功。
4. 重复用户名注册。
5. 重复邮箱注册。
6. 密码为空校验。
7. 用户登录成功。
8. 密码错误登录失败。
9. 登录成功返回 JWT Token。
10. 注册后密码不是明文保存。

## 开发原则

1. 后端优先，接口稳定后再做前端。
2. Controller 只负责接收请求、参数校验、调用 Service、返回统一结果。
3. 业务逻辑放在 Service。
4. Entity 不直接返回给前端。
5. 用户数据必须隔离，后续业务不能信任前端传入的 `userId`。
6. 密码不能明文保存，也不能在接口响应中返回。
7. AI 建议要温暖、具体、轻量，不能制造学习焦虑。
8. 第一版优先完成核心闭环，不堆社区、好友、排行榜等扩展功能。

## 下一步计划

Day 4 建议继续完成 JWT 鉴权闭环：

1. 支持 Token 解析与校验。
2. 实现登录用户上下文。
3. 新增认证拦截器或 Spring Security Filter。
4. 新增 `GET /api/user/profile` 获取当前用户信息。
5. 为后续学习记录模块打好用户数据隔离基础。
