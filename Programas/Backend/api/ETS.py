from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import ETS, periodoETS, Turno, UnidadAprendizaje, InscripcionETS
from db.schemas.ETS import ETSCreate, ETSResponse
from db.session import get_db

router = APIRouter(prefix="/ETS", tags=["ETS"])

@router.get("/", response_model=list[ETSResponse])
def get_ETS(db: Session = Depends(get_db)):
    unidades = db.query(ETS).all()
    
    if not unidades:
        raise HTTPException(status_code=404, detail="No hay ETS guardados")
    
    ETS_response = [
        {
            "idETS": ets.idets,
            "idPeriodo": ets.periodo.periodo,
            "turno": ets.id_turno.nombre,
            "fecha": ets.fecha,
            "unidadAprendizaje": ets.UAETS.nombre
        }
        for ets in unidades
    ]
    return ETS_response

@router.get("/InscripcionAlumno/{usuario}", response_model=list[ETSResponse])
def get_ETS(usuario: str, db: Session = Depends(get_db)):
    unidades = db.query(InscripcionETS).filter(InscripcionETS.boleta == usuario)
    
    if not unidades:
        raise HTTPException(status_code=404, detail="No hay ETS guardados")
    
    ETS_response = [
        {
            "idETS": ets.ets.idets,
            "idPeriodo": ets.ets.periodo.periodo,
            "turno": ets.ets.id_turno.nombre,
            "fecha": ets.ets.fecha,
            "unidadAprendizaje": ets.ets.UAETS.nombre
        }
        for ets in unidades
    ]
    return ETS_response

@router.post("/", response_model=ETSResponse)
def create_ETS(data: ETSCreate, db: Session = Depends(get_db)):
    periodo = db.query(periodoETS).filter(periodoETS.periodo == data.idPeriodo).first()
    if not periodo:
        raise HTTPException(status_code=404, detail=f"Periodo {data.idPeriodo} no encontrado")
    
    # Buscar el Turno por nombre
    turno = db.query(Turno).filter(Turno.nombre == data.Turno).first()
    if not turno:
        raise HTTPException(status_code=404, detail=f"Turno {data.Turno} no encontrado")
    
    # Buscar el idUA por nombre
    unidad_aprendizaje = db.query(UnidadAprendizaje).filter(UnidadAprendizaje.idUA == data.idUA).first()
    if not unidad_aprendizaje:
        raise HTTPException(status_code=404, detail=f"Unidad de Aprendizaje {data.idUA} no encontrada")
    
    # Crear el nuevo objeto ETS con los ids encontrados
    nueva_unidad = ETS(
        idPeriodo=periodo.id_periodo,  
        Turno=turno.id_turno,         
        Fecha=data.Fecha,
        Cupo=data.Cupo,
        idUA=unidad_aprendizaje.idua,
        Duracion=data.Duracion
    )
    
    db.add(nueva_unidad)
    db.commit()
    db.refresh(nueva_unidad)  # Para obtener el objeto con el id asignado
    return nueva_unidad