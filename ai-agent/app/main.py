from fastapi import FastAPI

from app.clip_encoder import ClipEncoder
from app.config import settings
from app.image_loader import load_image_from_url
from app.qdrant_store import QdrantStore
from app.query_agent import QueryAgent
from app.schemas import ImageSearchRequest, IndexImageRequest, SearchResponse, TextSearchRequest

'''
    agent入口文件
'''
app = FastAPI(title="AI Image Search Agent")

query_agent = QueryAgent()
encoder = ClipEncoder()
store = QdrantStore()


@app.on_event("startup")
def startup():
    store.ensure_collection()


#图片入库
@app.post("/internal/vector/index-image")
def index_image(req: IndexImageRequest):
    image = load_image_from_url(req.oss_url)
    vector = encoder.encode_image(image)

    store.upsert_image(
        image_id=req.image_id,
        vector=vector,
        payload={
            "image_id": req.image_id,
            "tenant_id": req.tenant_id,
            "user_id": req.user_id,
            "status": "active",
            "content_type": req.content_type,
            "model_version": settings.model_version
        }
    )

    return {"image_id": req.image_id, "status": "synced"}


#文搜图
@app.post("/internal/search/text", response_model=SearchResponse)
def search_text(req: TextSearchRequest):
    rewritten_text = query_agent.rewrite(req.text) if req.rewrite else req.text
    vector = encoder.encode_text(rewritten_text)
    items = store.search(
        vector=vector,
        tenant_id=req.tenant_id,
        user_id=req.user_id,
        top_k=req.top_k
    )
    return {"items": items, "rewritten_text": rewritten_text}


#图搜图
@app.post("/internal/search/image", response_model=SearchResponse)
def search_image(req: ImageSearchRequest):
    image = load_image_from_url(req.oss_url)
    vector = encoder.encode_image(image)
    items = store.search(
        vector=vector,
        tenant_id=req.tenant_id,
        user_id=req.user_id,
        top_k=req.top_k
    )
    return {"items": items}
