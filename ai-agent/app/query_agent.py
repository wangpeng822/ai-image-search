from openai import OpenAI

from app.config import settings

'''
    查询改写模块
    将用户原始搜索词改成更合适图片索引的短查询
'''
class QueryAgent:
    def __init__(self):
        self.enabled = settings.enable_query_rewrite and bool(settings.deepseek_api_key)
        self.client = None
        if self.enabled:
            self.client = OpenAI(api_key=settings.deepseek_api_key, base_url=settings.deepseek_base_url)

    def rewrite(self, text: str) -> str:
        if not self.enabled or self.client is None:
            return text.strip()

        response = self.client.chat.completions.create(
            model=settings.deepseek_model,
            messages=[
                {
                    "role": "system",
                    "content": (
                        "你是图片检索查询改写助手。"
                        "把用户输入改写成适合中文跨模态图片检索的短查询词。"
                        "只返回查询词，不要解释。"
                    )
                },
                {"role": "user", "content": text}
            ],
            temperature=0.2
        )
        return response.choices[0].message.content.strip()
