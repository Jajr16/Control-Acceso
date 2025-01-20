from fastapi import APIRouter, Depends, HTTPException
from fastapi.encoders import jsonable_encoder
from sqlalchemy.orm import Session
from db.models import TipoSalon, Salon
from db.schemas.Salon import SalonResponse, SalonCreate, TipoSalonResponse
from db.session import get_db

router = APIRouter(prefix="/Salon", tags=["Salon"])

@router.get("/", response_model=list[SalonResponse])
def get_Salon(db: Session = Depends(get_db)):
    salones = db.query(Salon).all()
    
    if not salones:
        raise HTTPException(status_code=404, detail="No hay salones registrados")
    
    # Transformamos los salones para incluir el nombre del tipo de salón
    salones_response = [
        {
            "numSalon": salon.num_salon,
            "Edificio": salon.edificio,
            "Piso": salon.piso,
            "tipoSalon": salon.salonType.tipo  # Extraemos el nombre del tipo
        }
        for salon in salones
    ]
    
    return jsonable_encoder(salones_response)

@router.post("/", response_model=SalonResponse)
def create_Salon(data: SalonCreate, db: Session = Depends(get_db)):
    tipo_salon = db.query(TipoSalon).filter(TipoSalon.tipo == data.tipo).first()
    
    if not tipo_salon:
        raise HTTPException(status_code=404, detail=f"El tipo de salón '{data.tipo}' no existe")
    
    nuevoSalon = Salon(
        numSalon = data.numSalon,
        Edificio = data.Edificio,
        Piso = data.Piso,
        tipoSalon = tipo_salon.idts,
    )
    db.add(nuevoSalon)
    db.commit()
    db.refresh(nuevoSalon)
    
    salon_response = {
        "numSalon": nuevoSalon.num_salon,
        "Edificio": nuevoSalon.edificio,
        "Piso": nuevoSalon.piso,
        "tipoSalon": tipo_salon.tipo
    }
    
    return salon_response