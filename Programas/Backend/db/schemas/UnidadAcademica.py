from pydantic import BaseModel

class UnidadAcademicaBase(BaseModel):
    Nombre: str


class UnidadAcademicaCreate(UnidadAcademicaBase):
    pass


class UnidadAcademicaResponse(UnidadAcademicaBase):
    class Config:
        orm_mode = True
