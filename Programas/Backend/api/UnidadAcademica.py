from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import UnidadAcademica
from db.schemas.UnidadAcademica import UnidadAcademicaCreate, UnidadAcademicaResponse
from db.session import get_db

router = APIRouter(prefix="/unidadAcademica", tags=["Unidad Academica"])

@router.get("/", response_model=list[UnidadAcademicaResponse])
def get_unidadesAcademicas(db: Session = Depends(get_db)):
    unidades = db.query(UnidadAcademica).all()
    return unidades

@router.post("/", response_model=UnidadAcademicaResponse)
def create_unidadAcademica(data: UnidadAcademicaCreate, db: Session = Depends(get_db)):
    nueva_unidad = UnidadAcademica(**data.dict())
    db.add(nueva_unidad)
    db.commit()
    db.refresh(nueva_unidad)
    return nueva_unidad

# @router.get("/{unidad_id}", response_model=UnidadAcademicaResponse)
# def get_unidadAcademica(unidad_id: int, db: Session = Depends(get_db)):
#     unidad = db.query(UnidadAcademica).filter(UnidadAcademica.idUA == unidad_id).first()
#     if not unidad:
#         raise HTTPException(status_code=404, detail="Unidad Académica no encontrada")
#     return unidad
