from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import UnidadAprendizaje
from db.schemas.UnidadAprendizaje import UnidadAprendizajeCreate, UnidadAprendizajeBase
from db.session import get_db

router = APIRouter(prefix="/unidadAprendizaje", tags=["Unidad Aprendizaje"])

@router.get("/", response_model=list[UnidadAprendizajeBase])
def get_unidadesAprendizaje(db: Session = Depends(get_db)):
    unidades = db.query(UnidadAprendizaje).all()
    return unidades

@router.post("/", response_model=UnidadAprendizajeBase)
def create_unidadAprendizaje(data: UnidadAprendizajeCreate, db: Session = Depends(get_db)):
    nueva_unidad = UnidadAprendizaje(**data.dict())
    db.add(nueva_unidad)
    db.commit()
    db.refresh(nueva_unidad)
    return nueva_unidad