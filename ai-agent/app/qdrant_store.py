#Qdrant 官方 Python 客户端，用来连接 Qdrant 服务。
from qdrant_client import QdrantClient
'''
    Distance：定义向量距离算法，这里用的是余弦相似度。
    VectorParams：创建 collection 时配置向量维度和距离算法。
    PointStruct：写入 Qdrant 的单条向量数据结构。
    Filter / FieldCondition / MatchValue：搜索时构造 payload 过滤条件。
'''
from qdrant_client.models import Distance, FieldCondition, Filter, MatchValue, PointStruct, VectorParams

'''
    settings：从 app/config.py:1 读取配置，比如 Qdrant 地址、端口、collection 名、向量维度、模型版本。
'''
from app.config import settings

'''
    Qdrant向量数据库操作模块
    1.创建collection
    2.写入图片向量
    3.根据文本/图片向量检索显示图片
'''
class QdrantStore:
    def __init__(self):
        #用于连接Qdrant
        self.client = QdrantClient(host=settings.qdrant_host, port=settings.qdrant_port)
        #将配置文件中定义的 collection 名称保存为实例变量，供类中其他方法使用。
        # Collection（集合）
        self.collection = settings.collection_name
    #ensure_collection确保 collect存在
    def ensure_collection(self):
        #self.client.get_collections()获取Qdrant当前有的所有collection
        existing = [collection.name for collection in self.client.get_collections().collections]
        if self.collection in existing:
            return

        self.client.create_collection(
            collection_name=self.collection,
            vectors_config=VectorParams(size=settings.vector_size, distance=Distance.COSINE)
        )

    def upsert_image(self, image_id: str, vector: list[float], payload: dict):
        self.client.upsert(
            collection_name=self.collection,
            points=[
                PointStruct(
                    id=image_id,
                    vector=vector,
                    payload=payload
                )
            ]
        )

    def search(self, vector: list[float], tenant_id: str, user_id: str, top_k: int):
        query_filter = Filter(
            must=[
                #当前租户
                FieldCondition(key="tenant_id", match=MatchValue(value=tenant_id)),
                #当前用户
                FieldCondition(key="user_id", match=MatchValue(value=user_id)),
                #active
                FieldCondition(key="status", match=MatchValue(value="active")),
                #当前模型版本
                FieldCondition(key="model_version", match=MatchValue(value=settings.model_version))
            ]
        )

        hits = self.client.search(
            collection_name=self.collection,
            query_vector=vector,
            query_filter=query_filter,
            limit=top_k
        )

        return [
            {
                "image_id": hit.payload["image_id"],
                "score": hit.score
            }
            for hit in hits
        ]
