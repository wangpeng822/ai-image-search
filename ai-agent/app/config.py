import os

'''
    属性配置
'''
class Settings:
    qdrant_host: str = os.getenv("QDRANT_HOST", "localhost")
    qdrant_port: int = int(os.getenv("QDRANT_PORT", "6333"))
    collection_name: str = os.getenv("QDRANT_COLLECTION", "image_clip_v1")
    model_version: str = os.getenv("MODEL_VERSION", "chinese-clip-vit-b16-v1")
    vector_size: int = int(os.getenv("VECTOR_SIZE", "512"))

    deepseek_api_key: str = os.getenv("DEEPSEEK_API_KEY", "")
    deepseek_base_url: str = os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com")
    deepseek_model: str = os.getenv("DEEPSEEK_MODEL", "deepseek-chat")
    enable_query_rewrite: bool = os.getenv("ENABLE_QUERY_REWRITE", "true").lower() == "true"


settings = Settings()
