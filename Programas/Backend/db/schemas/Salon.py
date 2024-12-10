from pydantic import BaseModel

################## TIPO SALON ##############
class TipoSalonBase(BaseModel):
    Tipo: str
    
    class Config:
        orm_mode = True

class TipoSalonCreate(TipoSalonBase):
    pass

class TipoSalonResponse(TipoSalonBase):
    pass

################## SALON ###############
class SalonBase(BaseModel):
    numSalon: int
    Edificio: int
    Piso: int


class SalonCreate(SalonBase):
    tipo: str
    
    class Config:
        orm_mode = True


class SalonResponse(SalonBase):
    tipoSalon: str
    
    class Config:
        orm_mode = True
