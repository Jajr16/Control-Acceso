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

    # Buscar el usuario en la base de datos
    usuario = db.query(Usuario).filter(Usuario.usuario == login_data.usuario).first()
    if not usuario:
        error_code = 404
        message = "Usuario no encontrado"
        return LoginResponse(
            usuario=None, error_Code=error_code, message=message, rol=None
        )

    # Verificar la contraseña usando bcrypt
    if not pwd_context.verify(login_data.Contraseña, usuario.password):
        error_code = 401
        message = "Contraseña incorrecta"
        return LoginResponse(
            usuario=None, error_Code=error_code, message=message, rol=None
        )

    # Retornar respuesta exitosa
    return LoginResponse(
        usuario=usuario.usuario,
        rol=usuario.tipoUser.tipo,
        error_Code=error_code,
        message=message
    )
