import random

'''
    图片和文字向量生成模块
'''
class ClipEncoder:
    def encode_text(self, text: str) -> list[float]:
        return self._mock_vector(f"text:{text}")

    def encode_image(self, image) -> list[float]:
        return self._mock_vector(f"image:{image.size}")

    def _mock_vector(self, seed_text: str) -> list[float]:
        random.seed(seed_text)
        return [random.random() for _ in range(512)]
