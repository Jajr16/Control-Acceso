from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import InscripcionETS, Salon, ETS,AsistenciaInscripcion
from db.schemas.Inscripciones import InscripcionResponse, InscripcionCreate, UpdateAceptadoRequest, confirmInscripcion
from db.session import get_db

router = APIRouter(prefix="/inscripciones", tags=["Inscripciones"])

@router.get("/confirm/{boleta}", response_model=confirmInscripcion)
def confirmInscription(boleta: int, db: Session = Depends(get_db)):
    inscripcion = db.query(InscripcionETS).filter(InscripcionETS.boleta == boleta)
    
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
    
    boletas = [inscripcion.boleta for inscripcion in inscripciones]

    # Paso 3: Consultar la tabla AsistenciaInscripcion para obtener el estado "Aceptado" por boleta
    asistencias = db.query(AsistenciaInscripcion)\
                    .filter(AsistenciaInscripcion.inscripcionets_boleta.in_(boletas))\
                    .all()

    # Paso 4: Crear un diccionario de {boleta: Aceptado}
    aceptado_dict = {asistencia.inscripcionets_boleta: asistencia.aceptado for asistencia in asistencias}

    # Paso 5: Construir la respuesta con todos los alumnos y agregar el estado "Aceptado"
    response = []
    for inscripcion in inscripciones:
        estado_aceptado = aceptado_dict.get(inscripcion.boleta, False)  # Default a False si no se encuentra
        response.append({
            "idETS": inscripcion.idets,
            "Boleta": inscripcion.boleta,
            "CURP": inscripcion.alumno.curp,
            "NombreA": inscripcion.alumno.persona.nombre,
            "ApellidoP": inscripcion.alumno.persona.apellido_p,
            "ApellidoM": inscripcion.alumno.persona.apellido_m,
            "Sexo": inscripcion.alumno.persona.sexo.nombre,
            "Correo": inscripcion.alumno.correoi,
            "Carrera": inscripcion.alumno.carrera.nombre,
            "Aceptado": estado_aceptado,  # Estado "Aceptado" agregado
        })
    
    return response


@router.get("/{ETSid}", response_model=list[InscripcionResponse])
def getAlumnoList(ETSid: int, db: Session = Depends(get_db)):
    # Paso 1: Obtener todas las inscripciones para el ETS dado
    inscripciones = db.query(InscripcionETS).filter(InscripcionETS.idets == ETSid).all()
    
    if not inscripciones:
        raise HTTPException(status_code=404, detail="No hay inscripciones para este ETS.")
    
    # Paso 2: Obtener las boletas de los alumnos para el ETS dado
    boletas = [inscripcion.boleta for inscripcion in inscripciones]

    # Paso 3: Consultar la tabla AsistenciaInscripcion para obtener el estado "Aceptado" por boleta
    asistencias = db.query(AsistenciaInscripcion)\
                    .filter(AsistenciaInscripcion.inscripcionets_boleta.in_(boletas))\
                    .filter(AsistenciaInscripcion.inscripcionets_idets == ETSid)\
                    .all()

    # Paso 4: Crear un diccionario de {boleta: Aceptado}
    aceptado_dict = {asistencia.inscripcionets_boleta: asistencia.aceptado for asistencia in asistencias}

    # Paso 5: Construir la respuesta con todos los alumnos y agregar el estado "Aceptado"
    response = []
    for inscripcion in inscripciones:
        estado_aceptado = aceptado_dict.get(inscripcion.boleta, False)  # Default a False si no se encuentra
        response.append({
            "idES": inscripcion.idets,
            "Boleta": inscripcion.boleta,
            "CURP": inscripcion.alumno.curp,
            "NombreA": inscripcion.alumno.persona.nombre,
            "ApellidoP": inscripcion.alumno.persona.apellido_p,
            "ApellidoM": inscripcion.alumno.persona.apellido_m,
            "Sexo": inscripcion.alumno.persona.sexo.nombre,
            "Correo": inscripcion.alumno.correoi,
            "Carrera": inscripcion.alumno.carrera.nombre,
            "Aceptado": estado_aceptado,  # Estado "Aceptado" agregado
        })
    
    return response


# Asignar un salón a un ETS
@router.post("/", response_model=InscripcionResponse)
def createInscripcion(data: InscripcionCreate, db: Session = Depends(get_db)):
    # Validar si la relación ya existe
    existing = db.query(InscripcionETS).filter(
        InscripcionETS.boleta == data.Boleta,
        InscripcionETS.idets == data.idETS
    ).first()
    
    if existing:
        raise HTTPException(status_code=400, detail="La relación ETS-Salón ya existe")
    
    # Crear la relación
    nueva_relacion = InscripcionETS(
        idets=data.idETS,
        boleta=data.Boleta
    )
    
    db.add(nueva_relacion)
    db.commit()
    db.refresh(nueva_relacion) 
    
    inscripcion = db.query(InscripcionETS).filter(
        InscripcionETS.boleta == nueva_relacion.boleta,
        InscripcionETS.idets == nueva_relacion.idets
    ).first()
    
    response = InscripcionResponse(
        idets=inscripcion.idets,
        boleta=inscripcion.boleta,
        curp=inscripcion.alumno.curp,
        NombreA=inscripcion.alumno.persona.nombre,
        ApellidoP=inscripcion.alumno.persona.apellido_p,
        ApellidoM=inscripcion.alumno.persona.apellido_m,
        Sexo=inscripcion.alumno.persona.sexo.nombre,
        Correo=inscripcion.alumno.correoi,
        Carrera=inscripcion.alumno.carrera.nombre
    )
    
    return response

@router.post("/updateAceptado", response_model=str)
def update_aceptado(data: UpdateAceptadoRequest, db: Session = Depends(get_db)):
    # Buscar la inscripción que corresponde a la boleta y idets
    inscripcion = db.query(AsistenciaInscripcion).filter(
        AsistenciaInscripcion.inscripcionets_boleta == data.Boleta,
        AsistenciaInscripcion.inscripcionets_idets == data.idETS
    ).first()
    
    if not inscripcion:
        raise HTTPException(status_code=404, detail="No se encontró la inscripción.")
    
    # Actualizar el valor de 'Aceptado'
    inscripcion.aceptado = data.aceptado
    
    # Guardar los cambios en la base de datos
    db.commit()
    
    return "Estado de aceptación actualizado con éxito."