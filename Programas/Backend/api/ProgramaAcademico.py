from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models import ProgramaAcademico
from db.schemas.ProgramaAcademico import ProgramaAcademicoCreate, ProgramaAcademicoResponse
from db.session import get_db

router = APIRouter(prefix="/programaAcademico", tags=["Programa Academico"])

@router.get("/", response_model=list[ProgramaAcademicoResponse])
def get_programaAcademico(db: Session = Depends(get_db)):
    unidades = db.query(ProgramaAcademico).all()
    
    if not unidades:
        raise HTTPException(status_code=404, detail="No hay programas académicos guardados")
    
    return unidades

@router.post("/", response_model=ProgramaAcademicoResponse)
def create_programaAcademico(data: ProgramaAcademicoCreate, db: Session = Depends(get_db)):
    nueva_unidad = ProgramaAcademico(**data.dict())
    db.add(nueva_unidad)
    db.commit()
    db.refresh(nueva_unidad)
    return nueva_unidad
