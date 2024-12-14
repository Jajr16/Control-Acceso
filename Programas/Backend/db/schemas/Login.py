from pydantic import BaseModel

# Login Request: Datos que envía el cliente
class LoginRequest(BaseModel):
    Usuario: str
    Contraseña: str

    class Config:
        orm_mode = True

# Login Response: Datos que devuelve el servidor
class LoginResponse(BaseModel):
    Error_code: int
    Message: str

    class Config:
        orm_mode = True
