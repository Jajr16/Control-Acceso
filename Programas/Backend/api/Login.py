from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import Usuario
from db.schemas.Login import LoginBase, LoginResponse
from db.session import get_db

router = APIRouter(prefix="/login", tags=["Login"])

@router.post("/", response_model=LoginResponse)
def login(login_data: LoginBase, db: Session = Depends(get_db)):
    error_code = 0  # Código de error 0 significa 'sin error'
    message = "Login exitoso"

    # Verificar si el usuario existe
    usuario = db.query(Usuario).filter(Usuario.Usuario == login_data.Usuario).first()
    if not usuario:
        error_code = 404  
        message = "Usuario no encontrado"
        raise HTTPException(status_code=error_code, detail=message)
    
    # Verificar si la contraseña es correcta
    if usuario.Password != login_data.Contraseña:
        error_code = 401  
        message = "Contraseña incorrecta"
        raise HTTPException(status_code=error_code, detail=message)

    return {
        "Usuario": usuario.Usuario,
        "Contraseña": usuario.Password,  # Generalmente no se devuelve la contraseña
        "Error_code": error_code,
        "Message": message
    }
