from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import ETS, periodoETS
from db.schemas.ETS import periodoETSCreate, periodoETSBase
from db.session import get_db

router = APIRouter(prefix="/periodoETS", tags=["Periodo ETS"])

@router.get("/", response_model=list[periodoETSBase])
def get_periodos(db: Session = Depends(get_db)):
    unidades = db.query(periodoETS).all()
    return unidades

@router.post("/", response_model=periodoETSBase)
def create_periodo(data: periodoETSCreate, db: Session = Depends(get_db)):
    nueva_unidad = periodoETS(**data.dict())
    db.add(nueva_unidad)
    db.commit()
    db.refresh(nueva_unidad)
    return nueva_unidad