from pydantic import BaseModel, constr
from datetime import date, datetime
from typing import Optional

from .Turno import TurnoResponse
from .SalonETS import SalonETSResponse
    
######### PERIODO ETS ###########
class periodoETSBase(BaseModel):
    Periodo: str
    Tipo: str
    Fecha_Inicio: date
    Fecha_Fin: date
    
    class Config:
        orm_mode = True

class periodoETSCreate(periodoETSBase):
    pass

class periodoETSResponse(periodoETSBase):
    pass

############### ETS ##############
class ETSBase(BaseModel):
    idPeriodo: str
    Turno: str
    Fecha: datetime
    Cupo: int
    idUA: str 
    Duracion: int
    
    class Config:
        orm_mode = True
    

class ETSCreate(ETSBase):
    pass


class ETSResponse(ETSBase):
    pass
