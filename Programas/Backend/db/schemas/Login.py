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
    Usuario: str
    Error_code: int
    Message: str
    Rol: Optional[str]

    class Config:
        orm_mode = True
