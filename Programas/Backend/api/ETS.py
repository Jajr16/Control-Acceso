from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import ETS, periodoETS, Turno, UnidadAprendizaje
from db.schemas.ETS import ETSCreate, ETSResponse
from db.session import get_db

router = APIRouter(prefix="/ETS", tags=["ETS"])

@router.get("/", response_model=list[ETSResponse])
def get_ETS(db: Session = Depends(get_db)):
    unidades = db.query(ETS).all()
    
    if not unidades:
        raise HTTPException(status_code=404, detail="No hay ETS guardados")
    
    return unidades

@router.post("/", response_model=ETSResponse)
def create_ETS(data: ETSCreate, db: Session = Depends(get_db)):
    periodo = db.query(periodoETS).filter(periodoETS.nombre == data.idPeriodo).first()
    if not periodo:
        raise HTTPException(status_code=404, detail=f"Periodo {data.idPeriodo} no encontrado")
    
    # Buscar el Turno por nombre
    turno = db.query(Turno).filter(Turno.nombre == data.Turno).first()
    if not turno:
        raise HTTPException(status_code=404, detail=f"Turno {data.Turno} no encontrado")
    
    # Buscar el idUA por nombre
    unidad_aprendizaje = db.query(UnidadAprendizaje).filter(UnidadAprendizaje.nombre == data.idUA).first()
    if not unidad_aprendizaje:
        raise HTTPException(status_code=404, detail=f"Unidad de Aprendizaje {data.idUA} no encontrada")
    
    # Crear el nuevo objeto ETS con los ids encontrados
    nueva_unidad = ETS(
        idPeriodo=periodo.idPeriodo,  # Asignar el id de Periodo
        Turno=turno.idTurno,          # Asignar el id de Turno
        Fecha=data.Fecha,
        Cupo=data.Cupo,
        idUA=unidad_aprendizaje.idUA, # Asignar el id de UA
        Duracion=data.Duracion
    )
    
    db.add(nueva_unidad)
    db.commit()
    db.refresh(nueva_unidad)  # Para obtener el objeto con el id asignado
    return nueva_unidad