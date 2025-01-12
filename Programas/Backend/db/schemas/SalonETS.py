from datetime import datetime
from typing import List
from pydantic import BaseModel
from .Salon import SalonResponse

class SalonETSBase(BaseModel):
    numSalon: int
    idETS: int
    
    class Config:
        orm_mode: True
        
class SalonETSCreate(SalonETSBase):
    pass

class SalonETSResponse(SalonETSBase):
    pass
    
class ETSResponse(BaseModel):
    UnidadAprendizaje: str
    tipoETS: str
    idETS: int
    idPeriodo: str
    Turno: str
    Fecha: datetime
    Cupo: int
    idUA: str
    Duracion: int
    
class ETSWithSalonsResponse(BaseModel):
    ETS: ETSResponse
    Salones: List[SalonResponse]