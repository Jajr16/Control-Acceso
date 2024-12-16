from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import SalonETS, Salon, ETS
from db.schemas.SalonETS import ETSWithSalonsResponse, SalonETSCreate, SalonETSResponse
from db.session import get_db

router = APIRouter(prefix="/SalonETS", tags=["SalonETS"])

# Obtener todas las relaciones ETS-Salón
@router.get("/", response_model=list[SalonETSResponse])
def get_all_salon_ets(db: Session = Depends(get_db)):
    relaciones = db.query(SalonETS).all()
    
    if not relaciones:
        raise HTTPException(status_code=404, detail="No hay relaciones ETS-Salón registradas")
    
    # Construir respuesta enriquecida
    response = []
    for rel in relaciones:
        salon = db.query(Salon).filter(Salon.numSalon == rel.numSalon).first()
        ets = db.query(ETS).filter(ETS.idETS == rel.idETS).first()
        response.append({
            "Salon": {
                "numSalon": salon.numSalon,
                "Edificio": salon.Edificio,
                "Piso": salon.Piso,
                "tipoSalon": salon.salonType.Tipo
            },
            "ETS": {
                "tipoETS": ets.periodo.Tipo,
                "idETS": ets.idETS,
                "idPeriodo": ets.periodo.Periodo,
                "Turno": ets.turno.Nombre,
                "Fecha": ets.Fecha,
                "Cupo": ets.Cupo,
                "idUA": ets.UAETS.Nombre,
                "Duracion": ets.Duracion
            }
        })
    
    return response

@router.get("/{ETSid}", response_model=ETSWithSalonsResponse)
def getSalonETS(ETSid: int, db: Session = Depends(get_db)):
    ets = db.query(ETS).filter(ETS.idETS == ETSid).first()
    if not ets:
        raise HTTPException(status_code=404, detail="ETS no encontrado")
    
    relaciones = db.query(SalonETS).filter(SalonETS.idETS == ETSid).all()
    if not relaciones:
        raise HTTPException(status_code=404, detail="No hay relaciones ETS-Salón registradas")
    
    # Construir respuesta enriquecida
    salones = [
        {
            "numSalon": salon.numSalon,
            "Edificio": salon.Edificio,
            "Piso": salon.Piso,
            "tipoSalon": salon.salonType.Tipo
        }
        for rel in relaciones
        if (salon := db.query(Salon).filter(Salon.numSalon == rel.numSalon).first())
    ]
    
    return {
        "ETS": {
            "tipoETS": ets.periodo.Tipo,
            "idETS": ets.idETS,
            "idPeriodo": ets.periodo.Periodo,
            "Turno": ets.turno.Nombre,
            "Fecha": ets.Fecha,
            "Cupo": ets.Cupo,
            "idUA": ets.UAETS.programaAcademico.Nombre,
            "Duracion": ets.Duracion
        },
        "Salones": salones
    }

# Asignar un salón a un ETS
@router.post("/", response_model=SalonETSResponse)
def create_salon_ets(data: SalonETSCreate, db: Session = Depends(get_db)):
    # Validar el ETS
    ets = db.query(ETS).filter(ETS.idETS == data.idETS).first()
    if not ets:
        raise HTTPException(status_code=404, detail=f"ETS con ID {data.idETS} no encontrado")
    
    # Validar el salón
    salon = db.query(Salon).filter(Salon.numSalon == data.numSalon).first()
    if not salon:
        raise HTTPException(status_code=404, detail=f"Salón con número {data.numSalon} no encontrado")
    
    # Validar si la relación ya existe
    existing = db.query(SalonETS).filter(
        SalonETS.numSalon == data.numSalon,
        SalonETS.idETS == data.idETS
    ).first()
    
    if existing:
        raise HTTPException(status_code=400, detail="La relación ETS-Salón ya existe")
    
    # Crear la relación
    nueva_relacion = SalonETS(
        numSalon=data.numSalon,
        idETS=data.idETS
    )
    db.add(nueva_relacion)
    db.commit()
    db.refresh(nueva_relacion)

    # Respuesta enriquecida
    return {
        "Salon": {
            "numSalon": salon.numSalon,
            "Edificio": salon.Edificio,
            "Piso": salon.Piso,
            "tipoSalon": salon.salonType.Tipo
        },
        "ETS": {
            "tipoETS": ets.periodo.Tipo,
            "idETS": ets.idETS,
            "idPeriodo": ets.periodo.Periodo,
            "Turno": ets.turno.Nombre,
            "Fecha": ets.Fecha,
            "Cupo": ets.Cupo,
            "idUA": ets.UAETS.Nombre,
            "Duracion": ets.Duracion
        }
    }