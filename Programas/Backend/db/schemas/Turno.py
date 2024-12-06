from pydantic import BaseModel

class TurnoBase(BaseModel):
    Nombre: str
    
    class Config:
        orm_mode = True


class TurnoCreate(TurnoBase):
    pass


class TurnoResponse(TurnoBase):
    pass
