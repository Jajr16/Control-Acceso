from pydantic import BaseModel

class SalonETSBase(BaseModel):
    
    class Config:
        orm_mode: True
        
class SalonETSCreate(SalonETSBase):
    pass

class SalonETSResponse(SalonETSBase):
    Salon: dict
    ETS: dict