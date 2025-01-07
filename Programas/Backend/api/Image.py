from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from db.schemas.Image import ImageMetadata
from db.session import get_db

router = APIRouter(prefix="/ImagePDF", tags=["ImagePDF"])

@router.get("/", response_model=ImageMetadata)
def getCalendar(db: Session = Depends(get_db)):
    