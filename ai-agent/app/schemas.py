from pydantic import BaseModel, Field


class IndexImageRequest(BaseModel):
    image_id: str
    tenant_id: str
    user_id: str
    oss_url: str
    content_type: str = "image/jpeg"


class TextSearchRequest(BaseModel):
    tenant_id: str
    user_id: str
    text: str
    top_k: int = Field(default=50, ge=1, le=200)
    rewrite: bool = True


class ImageSearchRequest(BaseModel):
    tenant_id: str
    user_id: str
    oss_url: str
    top_k: int = Field(default=50, ge=1, le=200)


class SearchItem(BaseModel):
    image_id: str
    score: float


class SearchResponse(BaseModel):
    items: list[SearchItem]
    rewritten_text: str | None = None
