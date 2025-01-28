from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.schemas.Image import DaysETS
from db.session import get_db
from db.models import periodoETS
import datetime

router = APIRouter(prefix="/TimeToETS", tags=["TimeToETS"])

@router.get("/", response_model=DaysETS)
def getDaysETS(db: Session = Depends(get_db)):
    año = datetime.date.today().year
    año_abreviado = str(año)[-2:] 
    mes = datetime.date.today().month
    
    periodo = ""
    
    if (mes >= 8 or mes <= 1):
        periodo = f"{año_abreviado}/1" 
    elif 2 <= mes <= 7:
        periodo = f"{año_abreviado}/2" 
    print(periodo)
    initPeriodo = db.query(periodoETS.fecha_inicio).filter(periodoETS.periodo == periodo).first()
    print(initPeriodo)
    
    if not initPeriodo:
        raise HTTPException(status_code=404, detail="Hubo un error al buscar el periodo.")
    
    fecha_inicio = initPeriodo[0]

    # Aseguramos que ambas fechas sean objetos datetime.date para la resta
    fecha_inicio_date = fecha_inicio if isinstance(fecha_inicio, datetime.date) else datetime.datetime.strptime(fecha_inicio, "%Y-%m-%d").date()

    dias = (fecha_inicio_date - datetime.date.today()).days
    
    texto = f"Faltan {dias} días para el periodo de ETS"
    # Aquí se crea una instancia del modelo DaysETS pasando el texto como un argumento
    return {"text": texto}