# AI 图片搜索一体工程

基于 `AI图片搜索项目计划架构书.md` 生成的最小工程骨架。

## 目录

- `frontend`：Vue 3 图片搜索工作台。
- `backend`：Spring Boot Java API 骨架，当前返回 mock 数据。
- `docs/sql/mysql-schema.sql`：MySQL 表结构设计。
- `ai-agent`：Python Agent 骨架，组合 DeepSeek 查询改写和 Chinese-CLIP/Qdrant 向量检索。
- `docs/superpowers/plans`：实现计划记录。

## 前端

```bash
cd frontend
npm install
npm run dev
```

默认访问 `http://localhost:5173`。前端会将 `/api` 代理到 `http://localhost:8080`。如果 Java 后端未启动，页面会自动使用本地 mock 数据。

## 后端

```bash
cd backend
mvn spring-boot:run
```

本机当前没有全局 Maven 时，可以在 IDE 中导入 `backend/pom.xml`，或后续补充 Maven Wrapper。

## 已预留的 Java API

- `GET /api/images`
- `POST /api/images`
- `POST /api/search/text`
- `POST /api/search/image`
- `GET /api/vector-tasks`

## 后续对接点

1. 将 `MockVectorAgentClient` 替换为真实 HTTP 或 MQ 客户端。
2. 将上传接口接入 OSS，生成真实 `oss_bucket`、`oss_key`、`oss_url`。
3. 使用 `docs/sql/mysql-schema.sql` 创建数据库表，并添加 Repository/Mapper。
4. 在 `ai-agent` 中把 mock `ClipEncoder` 替换成真实 Chinese-CLIP。
5. 配置 `DEEPSEEK_API_KEY` 后启用 DeepSeek 查询改写。
