from pydantic import BaseModel
from typing import Optional

# Login Request: Datos que envía el cliente
class LoginRequest(BaseModel):
    Usuario: str
    Contraseña: str

    class Config:
        orm_mode = True

# Login Response: Datos que devuelve el servidor
class LoginResponse(BaseModel):
    usuario: Optional[str]
    error_Code: int
    message: str
    rol: Optional[str]

    class Config:
        orm_mode = True
