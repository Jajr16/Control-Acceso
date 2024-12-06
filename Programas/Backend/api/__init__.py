from fastapi import APIRouter
from .UnidadAcademica import router as unidadAcademicaRouter 
from .ProgramaAcademico import router as programaAcademicoRouter 

api_router = APIRouter()

api_router.include_router(unidadAcademicaRouter)
api_router.include_router(programaAcademicoRouter)