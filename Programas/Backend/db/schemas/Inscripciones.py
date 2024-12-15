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
    Aceptado: bool  # Agregar el campo Aceptado
    
    class Config:
        orm_mode = True

class UpdateAceptadoRequest(BaseModel):
    Boleta: str
    idETS: int
    aceptado: bool  # El nuevo valor para el campo Aceptado
    
    class Config:
        orm_mode = True

