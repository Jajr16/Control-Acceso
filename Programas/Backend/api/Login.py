from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import Usuario
from db.schemas.Login import LoginRequest, LoginResponse
from db.session import get_db
from passlib.context import CryptContext

router = APIRouter(prefix="/login", tags=["Login"])

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


@router.post("/", response_model=LoginResponse)
def login(login_data: LoginRequest, db: Session = Depends(get_db)):
    # Variables iniciales para la respuesta
    error_code = 0
    message = "Login exitoso"

    print(Usuario.Usuario)
    print(Usuario.Password)

    # Buscar el usuario en la base de datos
    usuario = db.query(Usuario).filter(Usuario.Usuario == login_data.Usuario).first()
    if not usuario:
        error_code = 404
        message = "Usuario no encontrado"
        return LoginResponse(
            Usuario=None, Error_code=error_code, Message=message
        )

    # Verificar la contraseña usando bcrypt
    if not pwd_context.verify(login_data.Contraseña, usuario.Password):
        error_code = 401
        message = "Contraseña incorrecta"
        return LoginResponse(
            Usuario=None, Error_code=error_code, Message=message
        )

    # Retornar respuesta exitosa
    return LoginResponse(
        Usuario=usuario.Usuario,
        Error_code=error_code,
        Message=message
    )
