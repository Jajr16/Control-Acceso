from pydantic import BaseModel

class ProgramaAcademicoBase(BaseModel):
    Nombre: str
    Descripcion: str


class ProgramaAcademicoCreate(ProgramaAcademicoBase):
    idPA: str   
    
    class Config:
        orm_mode = True


class ProgramaAcademicoResponse(ProgramaAcademicoBase):
    class Config:
        orm_mode = True
