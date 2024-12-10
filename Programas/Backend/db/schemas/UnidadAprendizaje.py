from pydantic import BaseModel

class UnidadAprendizajeBase(BaseModel):
    idUA: str
    Nombre: str
    Descripcion: str
    idPA: str

    class Config:
        orm_mode = True

class UnidadAprendizajeCreate(UnidadAprendizajeBase):
    pass


class UnidadAprendizajeResponse(UnidadAprendizajeBase):
    pass
    
