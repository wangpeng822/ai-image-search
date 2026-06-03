from io import BytesIO

import requests
from PIL import Image


def load_image_from_url(url: str):
    response = requests.get(url, timeout=15)
    response.raise_for_status()
    return Image.open(BytesIO(response.content)).convert("RGB")
