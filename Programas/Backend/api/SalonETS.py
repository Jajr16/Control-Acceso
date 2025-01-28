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
        salon = db.query(Salon).filter(Salon.num_salon == rel.num_salon).first()
        ets = db.query(ETS).filter(ETS.idets == rel.idETS).first()
        response.append({
            "salon": {
                "numSalon": salon.num_salon,
                "Edificio": salon.edificio,
                "Piso": salon.piso,
                "tipoSalon": salon.salonType.tipo
            },
            "ets": {
                "tipoETS": ets.periodo.tipo,
                "idETS": ets.idets,
                "idPeriodo": ets.periodo.periodo,
                "turno": ets.turno.nombre,
                "fecha": ets.fecha,
                "cupo": ets.cupo,
                "idUA": ets.UAETS.nombre,
                "duracion": ets.duracion
            }
        })
    
    return response

@router.get("/{ETSid}", response_model=ETSWithSalonsResponse)
def getSalonETS(ETSid: int, db: Session = Depends(get_db)):
    ets = db.query(ETS).filter(ETS.idets == ETSid).first()
    if not ets:
        raise HTTPException(status_code=404, detail="ETS no encontrado")
    
    relaciones = db.query(SalonETS).filter(SalonETS.idets == ETSid).all()
    
    # Construir respuesta para ETS
    ets_data = {
        "unidadAprendizaje": ets.UAETS.nombre,
        "tipoETS": ets.periodo.tipo,
        "idETS": ets.idets,
        "idPeriodo": ets.periodo.periodo,
        "turno": ets.id_turno.nombre,
        "fecha": ets.fecha,
        "cupo": ets.cupo,
        "duracion": ets.duracion
    }
    
    # Construir lista de salones
    salones = [
        {
            "numSalon": salon.num_salon,
            "edificio": salon.edificio,
            "piso": salon.piso,
            "tipoSalon": salon.salonType.tipo
        }
        for rel in relaciones
        if (salon := db.query(Salon).filter(Salon.num_salon == rel.num_salon).first())
    ]
    
    # Retornar respuesta completa
    return {
        "ets": ets_data,
        "salon": salones  # Lista vacía si no hay salones
    }

# Asignar un salón a un ETS
@router.post("/", response_model=SalonETSResponse)
def create_salon_ets(data: SalonETSCreate, db: Session = Depends(get_db)):
    # Validar el ETS
    ets = db.query(ETS).filter(ETS.idets == data.idETS).first()
    if not ets:
        raise HTTPException(status_code=404, detail=f"ETS con ID {data.idETS} no encontrado")
    
    # Validar el salón
    salon = db.query(Salon).filter(Salon.num_salon == data.numSalon).first()
    if not salon:
        raise HTTPException(status_code=404, detail=f"Salón con número {data.numSalon} no encontrado")
    
    # Validar si la relación ya existe
    existing = db.query(SalonETS).filter(
        SalonETS.num_salon == data.numSalon,
        SalonETS.idets == data.idETS
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
        "salon": {
            "numSalon": salon.num_salon,
            "Edificio": salon.edificio,
            "Piso": salon.piso,
            "tipoSalon": salon.salonType.tipo
        },
        "ets": {
            "tipoETS": ets.periodo.tipo,
            "idETS": ets.idets,
            "idPeriodo": ets.periodo.periodo,
            "Turno": ets.id_turno.nombre,
            "Fecha": ets.fecha,
            "Cupo": ets.cupo,
            "idUA": ets.UAETS.nombre,
            "Duracion": ets.duracion
        }
    }