# StudyMate 开发备注

## 当前提交备注

本次准备推送的是 StudyMate 当前后端阶段成果，包含项目文档、后端基础骨架、通用能力以及 Day 3 用户注册登录能力。

## 已完成内容

1. 保留项目规范文档：`AGENTS.md`、`PRD.md`、`TECH_DESIGN.md`、`VIBECODING_PLAN.md`。
2. 后端项目位于 `studymate-backend`。
3. 已建立 Spring Boot 3 + Java 17 + MyBatis Plus + MySQL 的基础后端结构。
4. 已实现统一响应 `Result`。
5. 已实现全局异常处理。
6. 已实现用户表实体、Mapper 和基础 Service。
7. 已实现用户注册接口。
8. 已实现用户登录接口。
9. 已实现 BCrypt 密码加密。
10. 已实现 JWT Token 签发。
11. 已补充 Swagger / Knife4j 接口说明和 DTO/VO 字段说明。
12. 已补充认证模块测试。

## 测试备注

最近一次完整测试命令：

```bash
cd studymate-backend
mvn test
```

测试结果：

```text
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```

## 接口调试备注

注册接口：

```http
POST /api/auth/register
```

登录接口：

```http
POST /api/auth/login
```

登录成功后返回的 `token` 是 JWT，当前阶段只负责签发，不做完整鉴权拦截。

## 数据安全备注

1. 注册响应不会返回 `password`。
2. 数据库中的 `password` 使用 BCrypt 加密保存。
3. 后续实现学习记录、薄弱点和统计模块时，必须从 Token 中解析当前用户 ID。
4. 后续业务接口不能信任前端传入的 `userId`。
5. 仓库中不提交本地数据库密码，开发环境通过 `STUDYMATE_DB_PASSWORD` 等环境变量配置。

## 后续开发备注

建议下一阶段执行 Day 4：

1. 实现 JWT Token 解析。
2. 实现认证拦截器或 Spring Security Filter。
3. 实现当前登录用户上下文。
4. 实现 `GET /api/user/profile`。
5. 补充未登录、Token 无效、Token 过期等测试。

Day 4 完成后，再进入学习记录模块 CRUD，这样用户数据隔离会更稳。
