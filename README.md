# StudyMate：AI 学习复盘与 Java 实习备考陪伴系统

StudyMate 是一个面向 Java 实习备考人群的 AI 学习复盘与情绪陪伴系统。用户每天学习结束后，可以用自然语言记录学习情况，系统通过 AI 自动整理学习内容、提取薄弱点、分析情绪状态，并生成明日建议和安慰反馈。

当前项目采用“后端优先”的开发策略：先完成稳定的后端接口、数据权限、安全鉴权、AI 整理和统计分析能力，再集中开发前端页面。

## 项目目标

第一版重点完成一个真实可用的学习复盘闭环：

1. 用户注册登录。
2. 用户输入自然语言学习记录。
3. 后端调用 AI 整理学习记录。
4. 用户确认并保存结构化学习记录。
5. 用户查看历史记录。
6. 系统统计学习时长、学习方向、薄弱点和情绪趋势。
7. 系统返回轻量、具体、低压力的明日建议和安慰反馈。

## 当前进度

已完成：

1. Day 1：后端基础骨架。
2. Day 2：统一返回结果、全局异常处理、基础配置、用户基础实体和 Mapper。
3. Day 3：用户注册、用户登录、BCrypt 密码加密、JWT Token 签发、认证接口测试。
4. Day 4：JWT 鉴权闭环、当前登录用户上下文、用户个人信息接口。
5. Day 5：学习记录数据库表、实体、DTO、VO 和基础 Mapper。
6. Day 6：学习记录新增、列表、详情、修改、删除接口。
7. Day 7：AI 学习记录解析接口、Prompt 管理、mock AI 模式。
8. Day 8：真实 AI 接入、AI 调用日志、AI 异常处理。
9. Day 9：统计分析接口，包括首页仪表盘、学习趋势、学习方向占比、薄弱点排行和情绪趋势。

暂未完成：

1. 前端页面开发。
2. 前后端联调。
3. Redis 缓存、限流和 Token 黑名单等增强能力。
4. 社区、好友、排行榜等后续扩展功能。

## 技术栈

后端：

1. Java 17
2. Spring Boot 3
3. Spring Web
4. Spring Security
5. JWT
6. MyBatis Plus
7. MySQL 8
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
        │   │   │   ├── ai
        │   │   │   ├── health
        │   │   │   ├── statistics
        │   │   │   ├── study
        │   │   │   └── user
        │   │   └── enums
        │   └── resources
        └── test
```

## 后端模块说明

当前后端主要模块：

1. `common`：统一响应结果、业务异常和全局异常处理。
2. `config`：密码加密、MyBatis Plus、Swagger / Knife4j 等配置。
3. `security`：JWT 配置、Token 工具、认证过滤器、当前登录用户工具。
4. `module/health`：健康检查接口。
5. `module/user`：用户注册、登录、当前用户信息和个人资料修改。
6. `module/study`：学习记录 CRUD、学习方向关联、薄弱点保存。
7. `module/ai`：AI 学习记录解析、Prompt 构建、AI 客户端、AI 调用日志和异常处理。
8. `module/statistics`：首页仪表盘、学习趋势、学习方向占比、薄弱点排行、情绪趋势。

## 已实现接口

### 健康检查

```http
GET /api/health
```

### 用户认证

```http
POST /api/auth/register
POST /api/auth/login
```

注册成功不会返回 `password` 字段，数据库中的密码使用 BCrypt 加密保存。

### 当前用户

```http
GET /api/user/profile
PUT /api/user/profile
```

需要在请求头中携带：

```http
Authorization: Bearer <JWT_TOKEN>
```

### 学习记录

```http
POST   /api/study-records
GET    /api/study-records
GET    /api/study-records/{id}
PUT    /api/study-records/{id}
DELETE /api/study-records/{id}
```

学习记录接口只允许当前登录用户访问自己的数据。后端从 Token 中解析当前用户 ID，不信任前端传入的 `userId`。

### AI 整理

```http
POST /api/ai/record/analyze
```

AI 整理结果包含：

1. `durationMinutes`：学习时长，单位分钟。
2. `categories`：学习方向数组。
3. `studyContent`：学习内容总结。
4. `weakPoints`：薄弱点数组。
5. `emotionStatus`：情绪状态。
6. `tomorrowPlan`：明日计划。
7. `aiSummary`：AI 总结。
8. `aiComfort`：安慰反馈。

### 统计分析

```http
GET /api/statistics/dashboard
GET /api/statistics/trend
GET /api/statistics/category
GET /api/statistics/weak-points
GET /api/statistics/emotion
```

统计接口全部需要登录，并且只统计当前登录用户的数据。

#### 首页仪表盘

`GET /api/statistics/dashboard`

返回字段：

1. `todayDurationMinutes`：今日学习时长。
2. `weekDurationMinutes`：本周学习时长。
3. `monthDurationMinutes`：本月学习时长。
4. `totalDurationMinutes`：累计学习时长。
5. `continuousStudyDays`：连续学习天数。
6. `recentWeakPoints`：最近薄弱点排行。
7. `recentEmotionStatus`：最近情绪状态。
8. `recentTrend`：最近 7 天学习趋势。

#### 学习趋势

`GET /api/statistics/trend`

默认返回最近 7 天学习时长趋势。没有学习记录的日期返回 `0`。

#### 学习方向占比

`GET /api/statistics/category`

统计当前用户各学习方向的记录数量、累计学习时长和占比。一条学习记录可以关联多个学习方向，每个方向都会参与统计。

#### 薄弱点排行

`GET /api/statistics/weak-points`

按薄弱点内容出现次数倒序排序，过滤已删除薄弱点。

#### 情绪趋势

`GET /api/statistics/emotion`

默认返回最近 7 天情绪趋势。每天取当天最近一条学习记录的情绪状态，没有情绪数据时返回“平静”。

## 统计口径

1. 今日学习时长：统计 `record_date = 今天` 且 `deleted = 0` 的学习记录时长。
2. 本周学习时长：从本周一统计到今天。
3. 本月学习时长：从当月 1 日统计到今天。
4. 累计学习时长：统计当前用户所有未删除学习记录。
5. 连续学习天数：按学习日期去重；如果今天有记录，从今天向前连续计算；如果今天没有记录，从最近有记录的一天向前连续计算。
6. 最近 7 天趋势：后端生成完整日期序列，缺失日期补 `0`。
7. 学习方向占比：通过 `study_record_category` 关联学习记录和学习方向，过滤已删除学习记录。
8. 薄弱点排行：按 `weak_point.content` 分组，按出现次数倒序。
9. 情绪趋势：每天取 `create_time` 最新的一条学习记录情绪。

## 本地运行

进入后端目录：

```bash
cd studymate-backend
```

确认 MySQL 已创建并可连接 `studymate` 数据库。数据库建表脚本位于：

```text
studymate-backend/src/main/resources/db/schema.sql
```

开发环境数据库连接通过环境变量配置，避免把本地密码提交到仓库：

```bash
set STUDYMATE_DB_URL=jdbc:mysql://localhost:3306/studymate?useUnicode=true^&characterEncoding=utf8^&serverTimezone=Asia/Shanghai^&useSSL=false^&allowPublicKeyRetrieval=true
set STUDYMATE_DB_USERNAME=root
set STUDYMATE_DB_PASSWORD=你的本地数据库密码
```

启动后端：

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
2. 用户注册、登录、密码加密和 JWT Token。
3. 未登录访问受保护接口返回 401。
4. 当前用户信息读取与修改。
5. 学习记录新增、列表、详情、修改、删除。
6. 用户数据隔离和越权访问防护。
7. AI Prompt 构建、AI 结果解析、AI 调用异常处理。
8. 统计分析接口、统计默认值、用户隔离、删除数据过滤、多方向统计、薄弱点排行和情绪趋势。

最近一次验证结果：

```text
Tests run: 73, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Apifox 调试建议

1. 先调用 `POST /api/auth/login` 获取 Token。
2. 在 Apifox 环境变量中保存 Token。
3. 对需要登录的接口统一添加请求头：

```http
Authorization: Bearer {{token}}
```

4. 推荐按以下顺序测试：
   1. 注册用户。
   2. 登录获取 Token。
   3. 调用 AI 整理接口。
   4. 保存学习记录。
   5. 查询学习记录列表和详情。
   6. 修改、删除学习记录。
   7. 查看首页仪表盘和统计分析接口。
   8. 去掉 Token 验证 401 响应。

## 开发原则

1. 后端优先，接口稳定后再做前端。
2. Controller 只负责接收请求、参数校验、获取当前用户、调用 Service、返回统一结果。
3. 核心业务逻辑放在 Service。
4. Mapper 只负责数据库访问。
5. Entity 不直接返回给前端，接口返回使用 VO。
6. 用户数据必须隔离，不能信任前端传入的 `userId`。
7. `deleted = 1` 的数据不参与业务查询和统计。
8. AI 建议要温暖、具体、轻量，不能制造学习焦虑。
9. 第一版优先完成核心闭环，不扩展社区、好友、排行榜等功能。

## 下一步计划

后端 Day 1-9 核心能力已基本完成，下一阶段建议：

1. 用 Apifox 完整测试注册、登录、AI 整理、保存记录、历史记录和统计分析闭环。
2. 整理接口字段和错误响应示例。
3. 修正联调过程中发现的后端问题。
4. 后端接口稳定后，再开始 Vue 3 前端页面开发。
