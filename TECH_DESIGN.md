# TECH_DESIGN.md

# StudyMate：AI 学习复盘与 Java 实习备考陪伴系统技术设计文档

## 1. 文档说明

本文档用于说明 StudyMate 项目的技术设计方案，主要包括：

1. 技术栈选择。
2. 项目结构设计。
3. 后端优先开发策略。
4. 前后端协作方式。
5. 关键技术点。
6. 开发阶段规划。

本项目采用“后端优先，前端后置”的开发思路。

也就是说，第一阶段先把后端核心功能、数据库结构、接口文档和 AI 解析逻辑做得比较完整，等后端接口基本稳定后，再集中完成前端页面。

这样做的好处是：

1. 先把核心业务逻辑跑通。
2. 先确定数据库表结构和接口格式。
3. 避免前端写完后，后端接口频繁变化导致返工。
4. 更适合 Java 后端实习项目展示。
5. 项目重点更突出，能体现后端开发能力。

本项目第一版核心闭环为：

    用户输入学习记录
        ↓
    后端调用 AI 整理
        ↓
    用户确认保存
        ↓
    后端保存学习记录
        ↓
    后端统计学习数据
        ↓
    后端分析薄弱点
        ↓
    返回学习建议和安慰反馈

---

## 2. 技术栈选择

## 2.1 后端技术栈

由于本项目计划先完成后端功能，因此后端是第一阶段开发重点。

### 2.1.1 核心技术

| 技术 | 说明 |
|---|---|
| Java 17 | 后端主要开发语言 |
| Spring Boot 3 | 后端核心框架 |
| Spring Web | 提供 RESTful API |
| Spring Security | 登录认证和权限控制 |
| JWT | 前后端分离 Token 鉴权 |
| MyBatis Plus | 简化数据库 CRUD |
| MySQL 8 | 主数据库 |
| Redis | 缓存、限流、Token 黑名单，第一版可预留 |
| Lombok | 简化实体类代码 |
| Hibernate Validator | 参数校验 |
| Swagger / Knife4j | 接口文档和接口调试 |
| Maven | 项目依赖管理 |

---

### 2.1.2 后端技术选择理由

本项目主要用于展示 Java 后端开发能力，因此后端技术栈需要贴近真实 Java 实习开发场景。

选择 Spring Boot 3 的原因：

1. Java 后端实习中常见。
2. 项目结构清晰。
3. 适合快速开发 RESTful API。
4. 方便整合 MySQL、Redis、Spring Security 等组件。

选择 Spring Security + JWT 的原因：

1. 可以实现前后端分离登录认证。
2. 可以体现接口权限控制能力。
3. 能防止用户访问他人的学习记录。
4. 面试时比较好讲。

选择 MyBatis Plus 的原因：

1. 可以减少重复 CRUD 代码。
2. 适合个人项目快速开发。
3. 支持复杂查询时继续使用 XML。
4. 和 MySQL 配合简单。

选择 Swagger / Knife4j 的原因：

1. 后端先做时，可以先通过接口文档调试。
2. 前端后续开发时，可以直接根据接口文档联调。
3. 能让项目看起来更完整、规范。

---

### 2.1.3 后端第一阶段需要完成的功能

后端第一阶段需要优先完成以下内容：

1. 用户注册。
2. 用户登录。
3. JWT 认证。
4. 用户信息获取。
5. 学习记录新增。
6. 学习记录查询。
7. 学习记录修改。
8. 学习记录删除。
9. AI 学习记录解析。
10. AI 调用日志记录。
11. 薄弱点提取和保存。
12. 学习分类保存。
13. 首页仪表盘统计接口。
14. 学习时长统计接口。
15. 学习方向占比统计接口。
16. 薄弱点排行统计接口。
17. 情绪状态统计接口。
18. Swagger / Knife4j 接口文档。

---

## 2.2 前端技术栈

前端在后端核心功能基本完成后再集中开发。

### 2.2.1 核心技术

| 技术 | 说明 |
|---|---|
| Vue 3 | 前端核心框架 |
| Vite | 前端构建工具 |
| TypeScript | 提高代码可维护性 |
| Vue Router | 前端路由管理 |
| Pinia | 全局状态管理 |
| Axios | 请求后端接口 |
| Element Plus | UI 组件库 |
| ECharts | 数据统计图表 |

---

### 2.2.2 前端技术选择理由

前端第一版不需要做得过于复杂，重点是把后端能力展示出来。

Vue 3 + Element Plus 可以快速完成页面搭建，适合实现：

1. 登录注册页面。
2. 首页仪表盘。
3. AI 随手记录页面。
4. AI 整理结果确认页面。
5. 历史记录页面。
6. 统计图表页面。
7. 个人设置页面。

ECharts 用于展示学习数据，例如：

1. 最近 7 天学习时长趋势。
2. 本月学习时长统计。
3. 学习方向占比。
4. 薄弱点排行。
5. 情绪状态趋势。

---

## 2.3 数据库技术栈

### 2.3.1 核心技术

| 技术 | 说明 |
|---|---|
| MySQL 8 | 主数据库 |
| Redis | 缓存和限流，第一版可先预留 |
| Flyway / Liquibase | 数据库版本管理，后续可加入 |

---

### 2.3.2 数据库选择理由

MySQL 用于存储核心业务数据，包括：

1. 用户信息。
2. 学习记录。
3. 学习方向。
4. 薄弱点。
5. AI 整理结果。
6. AI 调用日志。

Redis 第一版可以先不强依赖，但建议后端代码预留扩展位置。

Redis 后续可以用于：

1. AI 接口调用频率限制。
2. Token 黑名单。
3. 首页统计数据缓存。
4. 验证码缓存。
5. 热点数据缓存。

---

## 2.4 AI 能力接入方案

### 2.4.1 AI 的作用

本项目第一版中，AI 是核心能力之一。

AI 主要负责把用户输入的自然语言学习记录整理成结构化数据。

例如用户输入：

    今天学了两个小时 Redis，AOF 和 RDB 还是有点乱，感觉挺累的，明天想整理一下持久化。

AI 需要整理为：

    学习时长：120 分钟
    学习方向：Redis
    学习内容：Redis 持久化、AOF、RDB
    薄弱点：AOF 和 RDB 对比
    情绪状态：有点累
    明日建议：整理 Redis 持久化对比表
    安慰反馈：今天不是没有进步，而是遇到了 Redis 中比较容易混淆的部分。

---

### 2.4.2 AI 需要完成的任务

1. 提取学习时长。
2. 判断学习方向。
3. 总结学习内容。
4. 提取薄弱点。
5. 判断情绪状态。
6. 生成明日小目标。
7. 生成 AI 总结。
8. 生成安慰反馈。

---

### 2.4.3 AI 服务封装

AI 调用必须封装在后端服务中，不能写在 Controller 里。

建议结构：

    AiRecordController
        ↓
    AiRecordAnalyzeService
        ↓
    StudyRecordPromptBuilder
        ↓
    AiClient
        ↓
    AI 模型服务

这样后续如果更换 AI 模型，只需要修改 AiClient，不影响业务层。

---

### 2.4.4 AI 输出格式要求

AI 返回内容尽量使用 JSON 格式，方便后端解析。

    {
      "durationMinutes": 120,
      "categories": ["Redis"],
      "studyContent": "今天主要学习了 Redis 持久化，包括 AOF 和 RDB。",
      "weakPoints": ["AOF 和 RDB 对比", "AOF 重写机制"],
      "emotionStatus": "有点累",
      "tomorrowPlan": "明天可以整理一张 Redis 持久化对比表。",
      "aiSummary": "今天学习了 Redis 持久化，并发现了 AOF 和 RDB 对比这个薄弱点。",
      "aiComfort": "今天不是没有进步，而是遇到了 Redis 中比较容易混淆的部分。能发现问题本身就是进步。"
    }

---

## 3. 系统整体架构

## 3.1 架构模式

本项目采用前后端分离架构。

由于采用后端优先开发，因此前期主要通过 Swagger / Knife4j 和 Postman 调试后端接口。

整体架构：

    Vue 3 前端
        ↓
    Axios HTTP 请求
        ↓
    Spring Boot 后端接口
        ↓
    MySQL 数据库

    Spring Boot 后端
        ↓
    AI 模型服务

    Spring Boot 后端
        ↓
    Redis 缓存，第一版可预留

---

## 3.2 后端优先开发流程

后端优先开发流程如下：

    第一步：设计数据库表结构
        ↓
    第二步：搭建 Spring Boot 后端基础框架
        ↓
    第三步：完成用户注册登录和 JWT 鉴权
        ↓
    第四步：完成学习记录 CRUD
        ↓
    第五步：完成 AI 整理接口
        ↓
    第六步：完成薄弱点保存和统计
        ↓
    第七步：完成首页统计接口
        ↓
    第八步：完善 Swagger / Knife4j 接口文档
        ↓
    第九步：用 Postman 或 Swagger 完整测试接口
        ↓
    第十步：后端接口稳定后再开发前端

---

## 3.3 前后端协作方式

因为先做后端，所以后端需要提前定义好接口格式。

后端每完成一个模块，都需要同步完成：

1. 接口路径。
2. 请求方法。
3. 请求参数。
4. 响应格式。
5. 错误码。
6. Swagger 文档。
7. Postman 测试结果。

前端开发时直接根据接口文档对接，不需要再反复猜接口字段。

---

## 4. 项目结构设计

## 4.1 后端项目结构

后端采用单体分层架构，第一版不拆微服务。

    studymate-backend
    ├── pom.xml
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── com
    │   │   │       └── studymate
    │   │   │           ├── StudyMateApplication.java
    │   │   │           ├── common
    │   │   │           ├── config
    │   │   │           ├── security
    │   │   │           ├── module
    │   │   │           │   ├── user
    │   │   │           │   ├── study
    │   │   │           │   ├── ai
    │   │   │           │   ├── statistics
    │   │   │           │   └── category
    │   │   │           └── enums
    │   │   └── resources
    │   │       ├── application.yml
    │   │       ├── application-dev.yml
    │   │       ├── application-prod.yml
    │   │       └── mapper
    │   └── test

---

## 4.2 后端目录说明

### 4.2.1 common 目录

用于存放通用代码。

    common
    ├── result
    │   ├── Result.java
    │   └── ResultCode.java
    ├── exception
    │   ├── BusinessException.java
    │   └── GlobalExceptionHandler.java
    ├── constants
    │   └── CommonConstants.java
    └── utils
        ├── JwtUtil.java
        └── DateTimeUtil.java

---

### 4.2.2 config 目录

用于存放项目配置类。

    config
    ├── SecurityConfig.java
    ├── CorsConfig.java
    ├── MybatisPlusConfig.java
    └── SwaggerConfig.java

---

### 4.2.3 security 目录

用于存放登录认证和权限控制相关代码。

    security
    ├── JwtAuthenticationFilter.java
    ├── LoginUser.java
    └── UserDetailsServiceImpl.java

---

### 4.2.4 module/user 用户模块

    user
    ├── controller
    │   └── UserController.java
    ├── service
    │   ├── UserService.java
    │   └── impl
    │       └── UserServiceImpl.java
    ├── mapper
    │   └── UserMapper.java
    ├── entity
    │   └── User.java
    ├── dto
    │   ├── LoginDTO.java
    │   └── RegisterDTO.java
    └── vo
        └── UserInfoVO.java

---

### 4.2.5 module/study 学习记录模块

    study
    ├── controller
    │   └── StudyRecordController.java
    ├── service
    │   ├── StudyRecordService.java
    │   └── impl
    │       └── StudyRecordServiceImpl.java
    ├── mapper
    │   └── StudyRecordMapper.java
    ├── entity
    │   └── StudyRecord.java
    ├── dto
    │   ├── StudyRecordCreateDTO.java
    │   ├── StudyRecordUpdateDTO.java
    │   └── StudyRecordQueryDTO.java
    └── vo
        ├── StudyRecordVO.java
        └── StudyRecordDetailVO.java

---

### 4.2.6 module/ai AI 模块

    ai
    ├── controller
    │   └── AiRecordController.java
    ├── service
    │   ├── AiRecordAnalyzeService.java
    │   └── impl
    │       └── AiRecordAnalyzeServiceImpl.java
    ├── client
    │   └── AiClient.java
    ├── prompt
    │   └── StudyRecordPromptBuilder.java
    ├── dto
    │   └── AiAnalyzeRequestDTO.java
    └── vo
        └── AiAnalyzeResultVO.java

---

### 4.2.7 module/statistics 统计模块

    statistics
    ├── controller
    │   └── StatisticsController.java
    ├── service
    │   ├── StatisticsService.java
    │   └── impl
    │       └── StatisticsServiceImpl.java
    └── vo
        ├── DashboardVO.java
        ├── StudyTrendVO.java
        ├── CategoryRatioVO.java
        ├── WeakPointRankVO.java
        └── EmotionTrendVO.java

---

## 4.3 后端分层说明

### 4.3.1 Controller 层

负责接收请求和返回响应。

Controller 层只做：

1. 接收参数。
2. 参数校验。
3. 调用 Service。
4. 返回统一结果。

Controller 层不写复杂业务逻辑。

---

### 4.3.2 Service 层

负责核心业务逻辑。

Service 层主要处理：

1. 用户注册登录。
2. 学习记录保存。
3. AI 结果解析。
4. 薄弱点保存。
5. 学习统计计算。
6. 用户数据权限校验。

---

### 4.3.3 Mapper 层

负责数据库访问。

简单 CRUD 使用 MyBatis Plus。

复杂统计查询可以使用 Mapper XML。

---

### 4.3.4 Entity 层

Entity 对应数据库表。

Entity 不直接返回给前端，避免暴露数据库结构。

---

### 4.3.5 DTO 层

DTO 用于接收前端请求参数。

例如：

1. LoginDTO。
2. RegisterDTO。
3. StudyRecordCreateDTO。
4. AiAnalyzeRequestDTO。

---

### 4.3.6 VO 层

VO 用于返回前端数据。

例如：

1. UserInfoVO。
2. StudyRecordVO。
3. DashboardVO。
4. AiAnalyzeResultVO。

---

## 4.4 前端项目结构

前端在后端接口基本完成后再开发。

    studymate-frontend
    ├── package.json
    ├── vite.config.ts
    ├── tsconfig.json
    ├── index.html
    ├── src
    │   ├── main.ts
    │   ├── App.vue
    │   ├── api
    │   │   ├── request.ts
    │   │   ├── user.ts
    │   │   ├── studyRecord.ts
    │   │   ├── ai.ts
    │   │   └── statistics.ts
    │   ├── assets
    │   ├── components
    │   ├── router
    │   ├── stores
    │   ├── types
    │   ├── utils
    │   └── views
    │       ├── login
    │       ├── dashboard
    │       ├── record
    │       ├── statistics
    │       └── user

---

## 4.5 前端开发原则

由于前端后置，前端开发时应遵循以下原则：

1. 严格按照后端 Swagger 文档对接接口。
2. 前端不自己虚构字段。
3. Axios 请求统一封装。
4. Token 统一在请求拦截器中携带。
5. 页面先保证功能完整，再优化样式。
6. AI 随手记录页面要作为核心页面重点设计。
7. 统计图表的数据全部来自后端接口。
8. 前端只做展示和交互，不做复杂业务计算。

---

## 5. 数据库设计概览

本技术设计文档只给出表结构方向，不写具体 SQL。

## 5.1 用户表 user

| 字段 | 说明 |
|---|---|
| id | 用户 ID |
| username | 用户名 |
| email | 邮箱 |
| password | 加密后的密码 |
| nickname | 昵称 |
| target_position | 目标岗位 |
| daily_target_minutes | 每日目标学习时长 |
| study_stage | 当前学习阶段 |
| status | 账号状态 |
| create_time | 创建时间 |
| update_time | 更新时间 |

---

## 5.2 学习记录表 study_record

| 字段 | 说明 |
|---|---|
| id | 记录 ID |
| user_id | 用户 ID |
| study_date | 学习日期 |
| raw_content | 用户原始输入 |
| duration_minutes | 学习时长 |
| study_content | AI 整理后的学习内容 |
| emotion_status | 情绪状态 |
| tomorrow_plan | 明日计划 |
| ai_summary | AI 总结 |
| ai_comfort | AI 安慰反馈 |
| remark | 用户备注 |
| create_time | 创建时间 |
| update_time | 更新时间 |

---

## 5.3 学习记录分类表 study_record_category

一条学习记录可能对应多个学习方向，所以使用中间表。

| 字段 | 说明 |
|---|---|
| id | 主键 |
| record_id | 学习记录 ID |
| category_name | 学习方向名称 |
| duration_minutes | 当前方向学习时长，可选 |
| create_time | 创建时间 |

---

## 5.4 薄弱点表 weak_point

| 字段 | 说明 |
|---|---|
| id | 主键 |
| user_id | 用户 ID |
| record_id | 学习记录 ID |
| category_name | 所属学习方向 |
| point_name | 薄弱点名称 |
| description | 薄弱点描述 |
| resolved | 是否已解决 |
| create_time | 创建时间 |
| update_time | 更新时间 |

---

## 5.5 AI 调用日志表 ai_call_log

| 字段 | 说明 |
|---|---|
| id | 主键 |
| user_id | 用户 ID |
| request_type | 请求类型 |
| prompt | 提示词 |
| response | AI 原始响应 |
| success | 是否成功 |
| error_message | 错误信息 |
| cost_time_ms | 耗时 |
| create_time | 创建时间 |

---

## 5.6 学习分类表 study_category

| 字段 | 说明 |
|---|---|
| id | 主键 |
| name | 分类名称 |
| description | 分类说明 |
| sort_order | 排序 |
| enabled | 是否启用 |
| create_time | 创建时间 |
| update_time | 更新时间 |

---

## 6. 接口设计概览

## 6.1 用户接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/login | 用户登录 |
| GET | /api/user/profile | 获取个人信息 |
| PUT | /api/user/profile | 修改个人信息 |

---

## 6.2 AI 记录接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /api/ai/record/analyze | AI 整理学习记录 |

### 请求示例

    {
      "rawContent": "今天学了两个小时 Redis，AOF 还是有点乱，感觉有点累。"
    }

### 响应示例

    {
      "durationMinutes": 120,
      "categories": ["Redis"],
      "studyContent": "今天主要学习了 Redis AOF。",
      "weakPoints": ["AOF 机制"],
      "emotionStatus": "有点累",
      "tomorrowPlan": "整理 Redis AOF 和 RDB 对比表。",
      "aiSummary": "今天你学习了 Redis 持久化相关内容。",
      "aiComfort": "你不是没有进步，而是在理解比较抽象的知识点。"
    }

---

## 6.3 学习记录接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /api/study-records | 新增学习记录 |
| GET | /api/study-records | 查询学习记录列表 |
| GET | /api/study-records/{id} | 查询学习记录详情 |
| PUT | /api/study-records/{id} | 修改学习记录 |
| DELETE | /api/study-records/{id} | 删除学习记录 |

---

## 6.4 统计接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /api/statistics/dashboard | 首页仪表盘数据 |
| GET | /api/statistics/trend | 学习时长趋势 |
| GET | /api/statistics/category | 学习方向占比 |
| GET | /api/statistics/weak-points | 薄弱点排行 |
| GET | /api/statistics/emotion | 情绪状态趋势 |

---

## 7. 后端优先开发计划

## 7.1 第一阶段：后端基础框架

### 目标

先把后端项目基础能力搭起来。

### 任务

1. 创建 Spring Boot 项目。
2. 配置 MySQL。
3. 配置 MyBatis Plus。
4. 配置统一返回结果 Result。
5. 配置全局异常处理。
6. 配置参数校验。
7. 配置 Swagger / Knife4j。
8. 配置跨域。
9. 创建基础包结构。

### 阶段完成标准

1. 后端项目可以正常启动。
2. Swagger / Knife4j 可以正常访问。
3. 数据库可以正常连接。
4. 基础测试接口可以正常返回。

---

## 7.2 第二阶段：用户和认证模块

### 目标

完成登录注册和 JWT 鉴权。

### 任务

1. 用户注册接口。
2. 用户登录接口。
3. 密码加密存储。
4. JWT 生成。
5. JWT 解析。
6. Spring Security 过滤器。
7. 获取当前登录用户。
8. 用户信息接口。

### 阶段完成标准

1. 用户可以注册。
2. 用户可以登录。
3. 登录成功可以获取 Token。
4. 携带 Token 才能访问受保护接口。
5. 未登录访问接口返回 401。

---

## 7.3 第三阶段：学习记录核心模块

### 目标

完成学习记录的基础 CRUD。

### 任务

1. 设计 study_record 表。
2. 设计 study_record_category 表。
3. 设计 weak_point 表。
4. 新增学习记录接口。
5. 查询学习记录列表接口。
6. 查询学习记录详情接口。
7. 修改学习记录接口。
8. 删除学习记录接口。
9. 校验用户只能操作自己的记录。

### 阶段完成标准

1. 可以新增学习记录。
2. 可以查询自己的学习记录。
3. 可以修改自己的学习记录。
4. 可以删除自己的学习记录。
5. 不能访问其他用户的学习记录。

---

## 7.4 第四阶段：AI 整理模块

### 目标

完成 AI 随手记录解析能力。

### 任务

1. 封装 AiClient。
2. 编写 StudyRecordPromptBuilder。
3. 实现 AI 整理接口。
4. 解析 AI JSON 返回。
5. 处理 AI 返回格式异常。
6. 保存 AI 调用日志。
7. AI 调用失败时返回友好提示。
8. AI 整理结果返回给前端确认。

### 阶段完成标准

1. 输入自然语言后，可以得到结构化整理结果。
2. AI 结果包含学习时长、学习方向、学习内容、薄弱点、情绪状态、明日计划、安慰反馈。
3. AI 调用失败时用户原始输入不会丢失。
4. AI 调用日志可以正常保存。

---

## 7.5 第五阶段：统计分析模块

### 目标

完成首页和统计页需要的后端接口。

### 任务

1. 今日学习时长统计。
2. 本周学习时长统计。
3. 本月学习时长统计。
4. 累计学习时长统计。
5. 连续学习天数统计。
6. 学习方向占比统计。
7. 最近 7 天学习趋势统计。
8. 薄弱点排行统计。
9. 情绪状态趋势统计。
10. 首页仪表盘聚合接口。

### 阶段完成标准

1. 首页所需数据可以通过一个接口返回。
2. 统计页面所需数据接口完整。
3. 所有统计数据只统计当前登录用户。
4. 后端接口通过 Swagger / Postman 测试。

---

## 7.6 第六阶段：后端整体测试和接口冻结

### 目标

在前端开发前，让后端接口基本稳定。

### 任务

1. 检查所有接口路径。
2. 检查所有请求参数。
3. 检查所有响应字段。
4. 检查错误码和错误提示。
5. 使用 Swagger / Postman 完整测试。
6. 修复后端明显问题。
7. 整理接口文档。
8. 暂时冻结第一版接口格式。

### 阶段完成标准

1. 后端核心接口可以完整跑通。
2. 接口文档清晰。
3. 前端可以直接根据接口文档开发。
4. 第一版不再频繁改字段名和接口路径。

---

## 7.7 第七阶段：前端开发

### 目标

在后端接口稳定后，集中完成前端页面。

### 任务

1. 创建 Vue 3 + Vite 项目。
2. 配置路由。
3. 配置 Pinia。
4. 配置 Axios。
5. 完成登录注册页面。
6. 完成首页仪表盘页面。
7. 完成 AI 随手记录页面。
8. 完成 AI 结果确认页面。
9. 完成历史记录页面。
10. 完成统计页面。
11. 完成个人设置页面。
12. 统一处理 loading、错误提示和 Token 失效。

### 阶段完成标准

1. 前端页面可以完整访问。
2. 登录后可以进入系统。
3. 可以输入自然语言并调用 AI 整理。
4. 可以保存学习记录。
5. 可以查看历史记录。
6. 可以查看统计图表。
7. 页面风格简洁、干净、温暖。

---

## 8. 关键技术点

## 8.1 后端接口设计要稳定

因为前端后置，所以后端接口一旦开发完成，需要尽量稳定。

需要注意：

1. 接口路径不要频繁变化。
2. 字段命名要统一。
3. 响应格式要统一。
4. 错误码要统一。
5. Swagger 文档要及时更新。

统一返回格式：

    {
      "code": 200,
      "message": "success",
      "data": {}
    }

错误返回格式：

    {
      "code": 400,
      "message": "参数错误",
      "data": null
    }

---

## 8.2 AI 返回内容不稳定

### 问题

AI 可能返回非标准 JSON，也可能字段缺失。

### 解决方案

1. 提示词中明确要求只返回 JSON。
2. 后端做 JSON 解析异常处理。
3. 字段缺失时使用默认值。
4. 保留 AI 原始响应。
5. AI 结果必须允许用户确认和修改。

---

## 8.3 AI 调用失败处理

### 问题

AI 接口可能超时、限流或失败。

### 解决方案

1. 设置请求超时时间。
2. 调用失败时返回明确错误。
3. 保存失败日志。
4. 前端后续提供重新生成按钮。
5. 原始输入不能丢失。

---

## 8.4 用户数据隔离

### 问题

学习记录是用户私人数据，不能被其他用户访问。

### 解决方案

1. 当前用户 ID 从 Token 中获取。
2. 不信任前端传入的 user_id。
3. 查询记录时必须带当前用户 ID。
4. 修改和删除前必须校验记录归属。
5. 统计数据也只能统计当前用户。

---

## 8.5 JWT 认证

### 问题

系统需要保护用户接口和学习记录接口。

### 解决方案

1. 登录成功后生成 JWT。
2. 后端过滤器解析 JWT。
3. 解析成功后设置当前登录用户。
4. Token 过期返回 401。
5. 前端收到 401 后跳转登录页。

---

## 8.6 学习统计计算

### 问题

首页和统计页需要多个统计数据。

### 解决方案

1. 第一版直接从 MySQL 查询计算。
2. 统计逻辑统一放在 StatisticsService。
3. 时间范围计算统一封装工具类。
4. 后续数据量大时再引入 Redis 缓存。
5. 不要在前端做复杂统计，前端只负责展示。

---

## 8.7 薄弱点统计

### 问题

一条学习记录可能有多个薄弱点，需要单独存储和统计。

### 解决方案

1. AI 解析出 weakPoints 数组。
2. 保存学习记录时同步写入 weak_point 表。
3. 按 point_name 聚合统计出现次数。
4. 支持最近 7 天和最近 30 天筛选。
5. 后续支持“已解决”状态。

---

## 8.8 防止重复提交

### 问题

用户可能重复点击保存按钮，导致重复数据。

### 解决方案

1. 前端保存按钮进入 loading 状态。
2. 后端根据 user_id、study_date、raw_content 做简单去重。
3. 同一天可以有多条记录，但完全相同内容不重复保存。
4. 后端保存接口需要有幂等意识。

---

## 8.9 后端先行时的接口测试

### 问题

前端还没完成时，需要验证后端功能是否可用。

### 解决方案

1. 使用 Swagger / Knife4j 调试接口。
2. 使用 Postman 保存接口测试集合。
3. 准备测试用户数据。
4. 准备测试学习记录数据。
5. AI 接口可以先支持 mock 模式。
6. 每个模块完成后都要自己测试完整流程。

---

## 9. 开发注意事项

## 9.1 后端优先不是只写接口

后端优先并不是简单写 Controller，而是要把后端业务闭环跑通。

每个模块都要做到：

1. 数据库表设计合理。
2. Entity、DTO、VO 清晰。
3. Service 逻辑完整。
4. Mapper 查询正确。
5. 接口文档清楚。
6. 错误处理完善。
7. 用户权限校验完整。

---

## 9.2 前端不要太早介入复杂页面

前端可以晚一点做，但可以提前准备：

1. 页面草图。
2. 路由规划。
3. 接口字段确认。
4. UI 风格参考。

不要一开始就花太多时间做页面，否则后端字段变化会导致前端返工。

---

## 9.3 AI 模块要支持 mock

开发早期可以先使用 mock AI 返回。

例如：

    用户输入任意内容
        ↓
    后端返回固定 AI 整理结果
        ↓
    先测试保存记录和统计功能

这样可以避免 AI 接口不稳定影响整体开发。

---

## 9.4 保留原始输入

无论 AI 是否解析成功，都要保留用户原始输入 raw_content。

原因：

1. AI 失败时可以重新解析。
2. AI 解析错误时可以人工修改。
3. 后续优化 AI 提示词时可以参考。
4. 用户可以看到自己最初写的内容。

---

## 10. 部署方案

## 10.1 开发环境

| 模块 | 技术 |
|---|---|
| 后端 | 本地 Spring Boot 启动 |
| 数据库 | 本地 MySQL |
| AI | 真实 AI 接口或 mock 模式 |
| 前端 | 后期本地 Vite 启动 |
| 接口文档 | Swagger / Knife4j |
| 接口测试 | Postman |

---

## 10.2 生产环境

第一版可以采用简单部署方式。

| 模块 | 部署方式 |
|---|---|
| 前端 | Nginx 静态资源部署 |
| 后端 | Spring Boot Jar 包部署 |
| 数据库 | 云服务器 MySQL 或云数据库 |
| Redis | 云服务器 Redis，第一版可不用 |
| 反向代理 | Nginx |

---

## 10.3 后续 Docker 扩展

后续可以使用 Docker Compose。

    docker-compose.yml
    ├── frontend
    ├── backend
    ├── mysql
    └── redis

第一版可以先不强制 Docker 化，先保证核心功能完整。

---

## 11. 代码规范

## 11.1 后端代码规范

1. Controller 不写复杂业务逻辑。
2. Service 负责业务逻辑。
3. Mapper 负责数据库访问。
4. DTO 用于接收请求参数。
5. VO 用于返回前端数据。
6. Entity 不直接返回给前端。
7. 所有接口统一返回 Result。
8. 所有参数需要校验。
9. 所有用户数据操作必须校验当前用户。
10. AI 提示词单独管理。
11. 关键业务逻辑需要注释。
12. 统计逻辑独立封装。

---

## 11.2 前端代码规范

1. 页面组件放在 views 目录。
2. 通用组件放在 components 目录。
3. 接口请求统一放在 api 目录。
4. 类型定义统一放在 types 目录。
5. 全局状态统一使用 Pinia。
6. Axios 统一处理 Token。
7. 页面样式保持简洁。
8. AI 记录入口要突出。
9. 错误提示要友好。
10. 加载状态要清晰。

---

## 12. 总结

本项目采用后端优先开发策略。

第一阶段重点不是做漂亮页面，而是先把 Java 后端核心能力做扎实：

1. 数据库设计。
2. 用户注册登录。
3. JWT 鉴权。
4. 学习记录 CRUD。
5. AI 解析接口。
6. 薄弱点保存和统计。
7. 学习数据统计。
8. Swagger 接口文档。
9. 用户数据权限控制。

推荐开发顺序为：

    后端基础框架
        ↓
    用户认证模块
        ↓
    学习记录模块
        ↓
    AI 整理模块
        ↓
    统计分析模块
        ↓
    后端接口测试
        ↓
    接口文档整理
        ↓
    前端页面开发
        ↓
    前后端联调

第一版最重要的是把核心闭环跑通：

    用户随手输入
        ↓
    AI 自动整理
        ↓
    用户确认保存
        ↓
    系统统计分析
        ↓
    系统给出建议和安慰

这样开发更符合你的目标：先展示 Java 后端能力，再用前端把项目完整呈现出来。