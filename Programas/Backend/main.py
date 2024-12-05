from fastapi import FastAPI
# from api.routes import router
from db.session import init_db

app = FastAPI()

# Registrar rutas
# app.include_router(router)

# Inicializar la base de datos
@app.on_event("startup")
def on_startup():
    init_db()

@app.get("/")
async def root():
    return {"message": "Welcome to FastAPI with SQLAlchemy and PostgreSQL"}
