# AI Agent

该目录采用组合方式：

- `QueryAgent`：调用 DeepSeek，对用户文本做查询改写、意图压缩。
- `VectorAgent`：使用 Chinese-CLIP 生成文本/图片向量，并由 Python 独占 Qdrant 写入和检索。

当前 `clip_encoder.py` 使用 mock 512 维向量，便于先跑通服务链路。后续把该文件替换为真实 Chinese-CLIP 推理即可。

## 启动

```bash
cd ai-agent
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

DeepSeek 查询改写通过环境变量启用：

```bash
set DEEPSEEK_API_KEY=你的 key
set ENABLE_QUERY_REWRITE=true
```

如果没有 `DEEPSEEK_API_KEY`，服务会自动跳过查询改写，直接使用原始搜索词。

## 文件说明

- `app/main.py`：FastAPI 入口，编排入库、文搜图、图搜图流程。
- `app/query_agent.py`：DeepSeek 查询改写。
- `app/clip_encoder.py`：Chinese-CLIP 编码器预留，目前是 mock 向量。
- `app/qdrant_store.py`：Qdrant collection、upsert、search 封装。
- `app/image_loader.py`：从 OSS URL 下载图片。
- `app/schemas.py`：Java 调 Agent 的请求/响应结构。

## Java 调 Python 预留接口

### 图片入库

`POST /internal/vector/index-image`

```json
{
  "image_id": "img_10001",
  "tenant_id": "tenant_1",
  "user_id": "user_1",
  "oss_url": "https://oss.example.com/bucket/a.jpg"
}
```

### 文搜图

`POST /internal/search/text`

```json
{
  "tenant_id": "tenant_1",
  "user_id": "user_1",
  "text": "红色连衣裙",
  "top_k": 50,
  "rewrite": true
}
```

返回：

```json
{
  "items": [
    {
      "image_id": "img_10001",
      "score": 0.8732
    }
  ],
  "rewritten_text": "红色 连衣裙 女装 商品图"
}
```

### 图搜图

`POST /internal/search/image`

```json
{
  "tenant_id": "tenant_1",
  "user_id": "user_1",
  "oss_url": "https://oss.example.com/tmp/query.jpg",
  "top_k": 50,
  "filter": {
    "status": "active"
  }
}
```

## Qdrant Collection

- collection: `image_clip_collection`
- recommended collection: `image_clip_v1`
- vector size: `512`
- distance: `Cosine`
- payload: `image_id`, `tenant_id`, `user_id`, `status`, `oss_url`, `content_type`, `created_at`, `updated_at`

Java 后端不直接操作 Qdrant。后续实现 Python Agent 时，应由 Python 独占 Qdrant upsert/search，并只向 Java 返回 `image_id` 与 `score`。
