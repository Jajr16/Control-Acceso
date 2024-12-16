from fastapi import FastAPI
from api import api_router  
from db.session import init_db
from starlette.middleware.base import BaseHTTPMiddleware
import asyncio

class TimeoutMiddleware(BaseHTTPMiddleware):
    def __init__(self, app, timeout: int):
        super().__init__(app)
        self.timeout = timeout

    async def dispatch(self, request, call_next):
        try:
            response = await asyncio.wait_for(call_next(request), timeout=self.timeout)
            return response
        except asyncio.TimeoutError:
            return JSONResponse(status_code=408, content={"detail": "Request Timeout"})


app = FastAPI()

# Registrar rutas
app.include_router(api_router)
app.add_middleware(TimeoutMiddleware, timeout=30000) 

# Inicializar la base de datos
@app.on_event("startup")
def on_startup():
    init_db()

@app.get("/")
async def root():
    return {"message": "Welcome to FastAPI with SQLAlchemy and PostgreSQL"}
