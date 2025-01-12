from pydantic import BaseModel

class ImageMetadata(BaseModel):
    name: str
    size: int
    type: str
    
class DaysETS(BaseModel):
    text: str
    
    class Config:
        orm_mode = True