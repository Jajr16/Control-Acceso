from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import InscripcionETS, Salon, ETS,AsistenciaInscripcion
from db.schemas.Inscripciones import InscripcionResponse, InscripcionCreate, UpdateAceptadoRequest, confirmInscripcion
from db.session import get_db

router = APIRouter(prefix="/inscripciones", tags=["Inscripciones"])

@router.get("/confirm/{Boleta}", response_model=confirmInscripcion)
def confirmInscription(Boleta: int, db: Session = Depends(get_db)):
    inscripcion = db.query(InscripcionETS).filter(InscripcionETS.Boleta == Boleta)
    
    if (not inscripcion):
        return confirmInscripcion(
            message= 0
        )
        
    return confirmInscripcion(
        message= 1
    )

# Obtener todas las relaciones ETS-Salón
@router.get("/", response_model=list[InscripcionResponse])
def getInscripciones(db: Session = Depends(get_db)):
    inscripciones = db.query(InscripcionETS).all()
    
    boletas = [inscripcion.Boleta for inscripcion in inscripciones]

    # Paso 3: Consultar la tabla AsistenciaInscripcion para obtener el estado "Aceptado" por boleta
    asistencias = db.query(AsistenciaInscripcion)\
                    .filter(AsistenciaInscripcion.InscripcionETSBoleta.in_(boletas))\
                    .all()

    # Paso 4: Crear un diccionario de {Boleta: Aceptado}
    aceptado_dict = {asistencia.InscripcionETSBoleta: asistencia.Aceptado for asistencia in asistencias}

    # Paso 5: Construir la respuesta con todos los alumnos y agregar el estado "Aceptado"
    response = []
    for inscripcion in inscripciones:
        estado_aceptado = aceptado_dict.get(inscripcion.Boleta, False)  # Default a False si no se encuentra
        response.append({
            "idETS": inscripcion.idETS,
            "Boleta": inscripcion.Boleta,
            "CURP": inscripcion.alumno.CURP,
            "NombreA": inscripcion.alumno.persona.Nombre,
            "ApellidoP": inscripcion.alumno.persona.ApellidoP,
            "ApellidoM": inscripcion.alumno.persona.ApellidoM,
            "Sexo": inscripcion.alumno.persona.sexo.Nombre,
            "Correo": inscripcion.alumno.CorreoI,
            "Carrera": inscripcion.alumno.carrera.Nombre,
            "Aceptado": estado_aceptado,  # Estado "Aceptado" agregado
        })
    
    return response


@router.get("/{ETSid}", response_model=list[InscripcionResponse])
def getAlumnoList(ETSid: int, db: Session = Depends(get_db)):
    # Paso 1: Obtener todas las inscripciones para el ETS dado
    inscripciones = db.query(InscripcionETS).filter(InscripcionETS.idETS == ETSid).all()
    
    if not inscripciones:
        raise HTTPException(status_code=404, detail="No hay inscripciones para este ETS.")
    
    # Paso 2: Obtener las boletas de los alumnos para el ETS dado
    boletas = [inscripcion.Boleta for inscripcion in inscripciones]

    # Paso 3: Consultar la tabla AsistenciaInscripcion para obtener el estado "Aceptado" por boleta
    asistencias = db.query(AsistenciaInscripcion)\
                    .filter(AsistenciaInscripcion.InscripcionETSBoleta.in_(boletas))\
                    .filter(AsistenciaInscripcion.InscripcionETSIdETS == ETSid)\
                    .all()

    # Paso 4: Crear un diccionario de {Boleta: Aceptado}
    aceptado_dict = {asistencia.InscripcionETSBoleta: asistencia.Aceptado for asistencia in asistencias}

    # Paso 5: Construir la respuesta con todos los alumnos y agregar el estado "Aceptado"
    response = []
    for inscripcion in inscripciones:
        estado_aceptado = aceptado_dict.get(inscripcion.Boleta, False)  # Default a False si no se encuentra
        response.append({
            "idETS": inscripcion.idETS,
            "Boleta": inscripcion.Boleta,
            "CURP": inscripcion.alumno.CURP,
            "NombreA": inscripcion.alumno.persona.Nombre,
            "ApellidoP": inscripcion.alumno.persona.ApellidoP,
            "ApellidoM": inscripcion.alumno.persona.ApellidoM,
            "Sexo": inscripcion.alumno.persona.sexo.Nombre,
            "Correo": inscripcion.alumno.CorreoI,
            "Carrera": inscripcion.alumno.carrera.Nombre,
            "Aceptado": estado_aceptado,  # Estado "Aceptado" agregado
        })
    
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

@router.post("/updateAceptado", response_model=str)
def update_aceptado(data: UpdateAceptadoRequest, db: Session = Depends(get_db)):
    # Buscar la inscripción que corresponde a la boleta y idETS
    inscripcion = db.query(AsistenciaInscripcion).filter(
        AsistenciaInscripcion.InscripcionETSBoleta == data.Boleta,
        AsistenciaInscripcion.InscripcionETSIdETS == data.idETS
    ).first()
    
    if not inscripcion:
        raise HTTPException(status_code=404, detail="No se encontró la inscripción.")
    
    # Actualizar el valor de 'Aceptado'
    inscripcion.Aceptado = data.aceptado
    
    # Guardar los cambios en la base de datos
    db.commit()
    
    return "Estado de aceptación actualizado con éxito."