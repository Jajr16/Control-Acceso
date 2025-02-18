from datetime import datetime
from typing import List
from pydantic import BaseModel
from .Salon import SalonResponse
from typing import Optional

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
    unidadAprendizaje: str
    tipoETS: str
    idETS: int
    idPeriodo: str
    turno: str
    fecha: datetime
    cupo: int
    duracion: int
    
class ETSWithSalonsResponse(BaseModel):
    ets: ETSResponse
    salon: Optional[List[SalonResponse]] = []