from fastapi import APIRouter
from .UnidadAcademica import router as unidadAcademicaRouter 
from .ProgramaAcademico import router as programaAcademicoRouter 
from .Salon import router as SalonRouter 
from .SalonETS import router as SalonETSRouter 
from .ETS import router as ETSRouter
from .periodoETS import router as periodoETSRouter
from .UnidadAprendizaje import router as unidadAprendizajeRouter 

api_router = APIRouter()

api_router.include_router(unidadAcademicaRouter)
api_router.include_router(programaAcademicoRouter)
api_router.include_router(SalonRouter)
api_router.include_router(SalonETSRouter)
api_router.include_router(unidadAprendizajeRouter)
api_router.include_router(ETSRouter)
api_router.include_router(periodoETSRouter)