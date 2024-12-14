from pydantic import BaseModel

################## Inscripcion ###############
class InscripcionBase(BaseModel):
    idETS: int
    Boleta: str
    
    class Config:
        orm_mode = True

class InscripcionCreate(InscripcionBase):
    pass

class InscripcionResponse(InscripcionBase):
    CURP: str
    NombreA: str
    ApellidoP: str
    ApellidoM: str
    Sexo: str
    Correo: str
    Carrera: str
    
    class Config:
        orm_mode = True
