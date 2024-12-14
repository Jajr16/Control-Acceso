from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import InscripcionETS, Salon, ETS
from db.schemas.Inscripciones import InscripcionResponse, InscripcionCreate
from db.session import get_db

router = APIRouter(prefix="/inscripciones", tags=["Inscripciones"])

# Obtener todas las relaciones ETS-Salón
@router.get("/", response_model=list[InscripcionResponse])
def getInscripciones(db: Session = Depends(get_db)):
    inscripciones = db.query(InscripcionETS).all()
    
    if not inscripciones:
        raise HTTPException(status_code=404, detail="No hay inscripciones de ETS aún.")
    
    # Construir respuesta enriquecida
    response = [
        {
            "idETS": inscripcion.idETS,
            "Boleta": inscripcion.Boleta,
            "CURP": inscripcion.alumno.CURP,
            "NombreA": inscripcion.alumno.persona.Nombre,
            "ApellidoP": inscripcion.alumno.persona.ApellidoP,
            "ApellidoM": inscripcion.alumno.persona.ApellidoM,
            "Sexo": inscripcion.alumno.persona.sexo.Nombre,
            "Correo": inscripcion.alumno.CorreoI,
            "Carrera": inscripcion.alumno.carrera.Nombre 
        }
        for inscripcion in inscripciones 
    ]
    
    return response

@router.get("/{ETSid}", response_model=InscripcionResponse)
def getOneInscription(ETSid: int, db: Session = Depends(get_db)):
    inscripcion = db.query(InscripcionETS).filter(InscripcionETS.idETS == ETSid).first()
    
    if not inscripcion:
        raise HTTPException(status_code=404, detail="No hay inscripciones de ETS aún.")
    
    # Construir respuesta enriquecida
    response = {
            "idETS": inscripcion.idETS,
            "Boleta": inscripcion.Boleta,
            "CURP": inscripcion.alumno.CURP,
            "NombreA": inscripcion.alumno.persona.Nombre,
            "ApellidoP": inscripcion.alumno.persona.ApellidoP,
            "ApellidoM": inscripcion.alumno.persona.ApellidoM,
            "Sexo": inscripcion.alumno.persona.sexo.Nombre,
            "Correo": inscripcion.alumno.CorreoI,
            "Carrera": inscripcion.alumno.carrera.Nombre 
        }
    
    
    return response



# Asignar un salón a un ETS
@router.post("/", response_model=InscripcionResponse)
def createInscripcion(data: InscripcionCreate, db: Session = Depends(get_db)):
    # Validar si la relación ya existe
    existing = db.query(InscripcionETS).filter(
        InscripcionETS.Boleta == data.Boleta,
        InscripcionETS.idETS == data.idETS
    ).first()
    
    if existing:
        raise HTTPException(status_code=400, detail="La relación ETS-Salón ya existe")
    
    # Crear la relación
    nueva_relacion = InscripcionETS(
        idETS=data.idETS,
        Boleta=data.Boleta
    )
    
    db.add(nueva_relacion)
    db.commit()
    db.refresh(nueva_relacion) 
    
    inscripcion = db.query(InscripcionETS).filter(
        InscripcionETS.Boleta == nueva_relacion.Boleta,
        InscripcionETS.idETS == nueva_relacion.idETS
    ).first()
    
    response = InscripcionResponse(
        idETS=inscripcion.idETS,
        Boleta=inscripcion.Boleta,
        CURP=inscripcion.alumno.CURP,
        NombreA=inscripcion.alumno.persona.Nombre,
        ApellidoP=inscripcion.alumno.persona.ApellidoP,
        ApellidoM=inscripcion.alumno.persona.ApellidoM,
        Sexo=inscripcion.alumno.persona.sexo.Nombre,
        Correo=inscripcion.alumno.CorreoI,
        Carrera=inscripcion.alumno.carrera.Nombre
    )
    
    return response
