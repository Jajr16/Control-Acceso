from pydantic import BaseModel

class ImageMetadata(BaseModel):
    name: str
    size: int
    type: str