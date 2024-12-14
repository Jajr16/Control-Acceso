from pydantic import BaseModel, constr
from datetime import date, datetime
from typing import Optional

######### LOGIN ###########
class LoginBase(BaseModel):
    
    Usuario: str
    Contraseña: str
    # Tipo_Usuario: str
    # CURP: str
    
    class Config:
        orm_mode = True

# class LoginCreate(LoginBase):
#     pass

class LoginResponse(LoginBase):

    Error_code: int
    Message: str

    class Config:
        orm_mode = True